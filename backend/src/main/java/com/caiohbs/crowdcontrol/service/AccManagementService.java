package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.*;
import com.caiohbs.crowdcontrol.repository.EmailCodeRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

@Service
public class AccManagementService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final JwtService jwtService;

    public AccManagementService(
            UserRepository userRepository,
            EmailCodeRepository emailCodeRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.emailCodeRepository = emailCodeRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ValidationErrorException(e.getMessage());
        }

        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);

    }

    public void createEmailCode(User user, String emailType) {

        try {
            EmailType.valueOf(emailType);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Email type not valid.");
        }

        EmailCode newEmailCode = new EmailCode(
                generateCode(), EmailType.valueOf(emailType), user
        );
        emailCodeRepository.save(newEmailCode);

    }

    public boolean isEmailCodeValid(String emailCode) {

        EmailCode foundCode = emailCodeRepository.findByEmailCode(emailCode);

        if (foundCode == null) {
            throw new ResourceNotFoundException("Email code not found.");
        }

        User foundUser = foundCode.getUser();

        if (Objects.equals(foundCode.getEmailType().toString(), "ENABLE_ACC")) {
            foundUser.setIsEnabled(true);
            userRepository.save(foundUser);
            emailCodeRepository.delete(foundCode);
            return true;
        } else if (Objects.equals(foundCode.getEmailType().toString(), "RECOV_PASS")) {
            foundUser.setPassword(foundUser.encryptPass(
                    foundUser.getLastName().toUpperCase() + "_" +
                    foundUser.getFirstName().toLowerCase() + "_")
            );
            return true;
        } else {
            throw new ResourceNotFoundException("Email type not valid.");
        }

    }

    private String generateCode() {
        // TODO: change this to UUID.
        String validChars = "0123456789abcdefghijklmnopqrstuvwxyz";

        Random random = new SecureRandom();
        StringBuilder code = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int randInt = random.nextInt(validChars.length());
            code.append(validChars.charAt(randInt));
        }
        return code.toString();

    }

}

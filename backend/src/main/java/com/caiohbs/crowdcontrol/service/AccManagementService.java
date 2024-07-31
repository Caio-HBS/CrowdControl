package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.*;
import com.caiohbs.crowdcontrol.repository.EmailCodeRepository;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccManagementService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public AccManagementService(
            UserRepository userRepository,
            EmailCodeRepository emailCodeRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RoleRepository roleRepository
    ) {

        this.userRepository = userRepository;
        this.emailCodeRepository = emailCodeRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request The authentication request containing username and password.
     * @return An authentication response containing a JWT token if successful, otherwise throws an exception.
     * @throws ValidationErrorException  If authentication fails due to invalid credentials.
     * @throws ResourceNotFoundException If the user is not found.
     */
    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) throws ValidationErrorException, ResourceNotFoundException {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new ValidationErrorException(e.getMessage());
        }

        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);

    }

    /**
     * Generates and persists an email verification code for a user.
     *
     * @param user      The user for whom to generate the code.
     * @param emailType The type of email for which the code is intended ({@code ENABLE_ACC} or {@code RECOV_PASS}).
     * @return The generated email verification code.
     * @throws ResourceNotFoundException If the provided email type is invalid.
     */
    public String createEmailCode(
            User user, String emailType
    ) throws ResourceNotFoundException {

        try {
            EmailType.valueOf(emailType);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Email type not valid.");
        }

        String generatedCode = generateCode();

        EmailCode newEmailCode = new EmailCode(generatedCode, true, EmailType.valueOf(emailType), user);
        emailCodeRepository.save(newEmailCode);

        return generatedCode;

    }

    /**
     * Creates a new superuser with administrator privileges if none exists.
     *
     * @param user The user to be assigned superuser privileges.
     * @throws ValidationErrorException If a superuser already exists.
     */
    public void createSuperUser(User user) throws ValidationErrorException {

        Role roleCheck = roleRepository.findByRoleName("ADMIN");

        if (roleCheck != null) {
            throw new ValidationErrorException("Super user already exists.");
        }

        userRepository.save(user);

        Permission[] permissions = Permission.values();
        Role adminRole = new Role("ADMIN", 1, 0,
                Arrays.stream(permissions).map(Enum::name).collect(Collectors.toList())
        );

        roleRepository.save(adminRole);

        user.setRole(adminRole);
        userRepository.save(user);

    }

    /**
     * Validates an email verification code and performs user actions based on the code type.
     *
     * @param emailCode The email verification code to be validated.
     * @return True if the code is valid and the corresponding action is successful, otherwise throws an exception.
     * @throws ResourceNotFoundException If the provided email code is not found.
     * @throws ValidationErrorException  If the code has already been used.
     */
    public boolean isEmailCodeValid(
            String emailCode
    ) throws ResourceNotFoundException, ValidationErrorException {

        EmailCode foundCode = emailCodeRepository.findByEmailCode(emailCode);

        if (foundCode == null) {
            throw new ResourceNotFoundException("Email code not found.");
        }

        if (!foundCode.isCodeActive()) {
            throw new ValidationErrorException("Code was already used.");
        }

        User foundUser = foundCode.getUser();

        if (Objects.equals(foundCode.getEmailType().toString(), "ENABLE_ACC")) {
            foundUser.setIsEnabled(true);
            foundCode.setCodeActive(false);
            emailCodeRepository.save(foundCode);
            userRepository.save(foundUser);

            return true;
        } else if (Objects.equals(foundCode.getEmailType().toString(), "RECOV_PASS")) {
            foundCode.setCodeActive(false);
            emailCodeRepository.save(foundCode);

            return true;
        } else {
            throw new ResourceNotFoundException("Email type not valid.");
        }

    }

    /**
     * Resets a user's password based on the provided information in the DTO.
     *
     * @param user The user whose password needs to be reset.
     * @param dto  The data transfer object containing new password and confirmation details.
     * @throws ValidationErrorException If the new password and confirm password do not match.
     */
    public void resetPassword(
            User user, UserUpdateDTO dto
    ) throws ValidationErrorException {

        if (dto.newPassword() == null || dto.confirmNewPassword() == null ||
            !Objects.equals(dto.newPassword(), dto.confirmNewPassword())
        ) {
            throw new ValidationErrorException("New password and confirm password do not match.");
        }

        user.setPassword(dto.newPassword());
        userRepository.save(user);

    }

    /**
     * Unlocks a user.
     *
     * @param userId The identifier of the user to unlock.
     * @throws ResourceNotFoundException If the user with the provided ID is not found.
     */
    public void unlockUser(Long userId) {

        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = foundUser.get();

        user.setIsAccountNonLocked(true);
        userRepository.save(user);

    }

    /**
     * Generates a random UUID as a string.
     *
     * @return A randomly generated UUID as a string.
     */
    private String generateCode() {
        return UUID.randomUUID().toString();
    }

}

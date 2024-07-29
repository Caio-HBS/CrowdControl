package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.model.*;
import com.caiohbs.crowdcontrol.repository.EmailCodeRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import com.caiohbs.crowdcontrol.service.AccManagementService;
import com.caiohbs.crowdcontrol.service.EmailSenderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthenticationController {

    private final AccManagementService accManagementService;
    private final EmailSenderService emailSenderService;
    private final EmailCodeRepository emailCodeRepository;
    private final UserRepository userRepository;

    public AuthenticationController(
            AccManagementService accManagementService,
            EmailSenderService emailSenderService,
            EmailCodeRepository emailCodeRepository, UserRepository userRepository) {
        this.accManagementService = accManagementService;
        this.emailSenderService = emailSenderService;
        this.emailCodeRepository = emailCodeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Authenticates the user based on provided credentials.
     *
     * @param request The {@link AuthenticationRequest} object containing the
     *                user's credentials (e-mail and password).
     * @return A {@link ResponseEntity} with code 200 - OK the JWT token if the
     * login was successful, or code 400 - BAD REQUEST if the credentials are
     * invalid.
     */
    @PostMapping(path="/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(accManagementService.authenticate(request));
    }

    /**
     * Activates a newly created account. Note that the user will still need to
     * log in after activating their account.
     *
     * @param code A String randomly generated and sent to the user for this
     *             purpose.
     * @return A {@link ResponseEntity} with code 200 - OK if the activation was
     * successful, or code 400 - BAD REQUEST if the activation code was not valid.
     */
    @GetMapping(path="/enable-acc")
    public ResponseEntity<GenericValidResponse> enableAcc(
            @RequestParam(name="code") String code
    ) {

        GenericValidResponse response = new GenericValidResponse();

        if (accManagementService.isEmailCodeValid(code)) {
            response.setMessage("Account enabled successfully.");
            return ResponseEntity.ok(response);
        }
        response.setMessage(
                "Error while trying to activate account. Contact a system administrator"
        );
        return ResponseEntity.badRequest().body(response);

    }

    /**
     * This endpoint is used to request a reset on the password's account. The
     * email is checked and in case it passes the check, en email message is sent
     * to the user so that they may change their password.
     *
     * @param userEmail The email registered as owner of the account.
     * @return A {@link ResponseEntity} with code 200 - OK if the email passes
     * the checks, or code 400 - BAD REQUEST if it doesn't.
     */
    @GetMapping(path="/acc-recovery/{userEmail}")
    public ResponseEntity<GenericValidResponse> recovery(
            @PathVariable String userEmail
    ) {

        Optional<User> findUser = userRepository.findByEmail(userEmail);

        if (findUser.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new GenericValidResponse("User not found.")
            );
        }

        User user = findUser.get();

        String code = accManagementService.createEmailCode(user, "RECOV_PASS");

        new Thread(() -> emailSenderService.sendEmail(
                user.getUsername(), "RECOV_PASS", code)
        ).start();

        return ResponseEntity.ok(
                new GenericValidResponse("A password recovery e-mail was sent. " +
                                         "Please follow the instructions to reset " +
                                         "your password."
                )
        );

    }

    /**
     * This endpoint is used to reset the user's password.
     *
     * @param code      A random string that is sent to the user's email.
     * @param updateDTO The update DTO containing the new password and its
     *                  confirmation.
     * @return A {@link ResponseEntity} with code 200 - OK if the reset is
     * successful, or code 400 - BAD REQUEST if any errors occur during the process.
     */
    @PostMapping(path="/reset-pass")
    public ResponseEntity<GenericValidResponse> resetPassword(
            @RequestParam(name="code") String code,
            @RequestBody UserUpdateDTO updateDTO
    ) {

        if (!accManagementService.isEmailCodeValid(code)) {
            return ResponseEntity.badRequest().body(
                    new GenericValidResponse("Invalid email code.")
            );
        }

        User user = emailCodeRepository.findByEmailCode(code).getUser();
        accManagementService.resetPassword(user, updateDTO);

        return ResponseEntity.ok(new GenericValidResponse("Password reset successful."));

    }

    /**
     * This endpoint is used to create a superuser. This endpoint is one use only,
     * all subsequent requests to it will be automatically denied.
     *
     * @param user A standard (and valid) {@link User} object that will need to
     *             be enabled just as any other users.
     * @return A {@link ResponseEntity} with code 200 - OK if the endpoint is
     * being accessed for the first time and validation passes, or code 400 - BAD
     * REQUEST if the endpoint was successfully accessed before.
     */
    @PostMapping("/create-super-user")
    public ResponseEntity<GenericValidResponse> createSuperUser(
            @Valid @RequestBody User user
    ) {
        accManagementService.createSuperUser(user);
        String code = accManagementService.createEmailCode(user, "ENABLE_ACC");

        new Thread(() -> emailSenderService.sendEmail(
                user.getUsername(), "ENABLE_ACC", code)
        ).start();

        return ResponseEntity.ok(new GenericValidResponse(
                "Super user created successfully. Please enable it through the e-mail."
        ));

    }

    /**
     * This endpoint is used to unlock locked accounts. This endpoint is only
     * accessible by the admin.
     *
     * @param userId The ID of the user to be unlocked.
     * @return A {@link ResponseEntity} with code 200 - OK if account was
     * successfully unlocked, or code 400 - BAD REQUEST if any errors occur
     * during the process.
     */
    @PostMapping(path="/api/v1/users/{userId}/unlock-acc")
    @PreAuthorize("@securityUtils.getAuthRole() == 'ADMIN'")
    public ResponseEntity<GenericValidResponse> unlockAcc(
            @PathVariable Long userId
    ) {

        accManagementService.unlockUser(userId);

        GenericValidResponse response = new GenericValidResponse(
                "Account unlocked successfully."
        );
        return ResponseEntity.ok(response);

    }

}

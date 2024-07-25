package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.model.AuthenticationRequest;
import com.caiohbs.crowdcontrol.model.AuthenticationResponse;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.service.AccManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    private final AccManagementService accManagementService;

    public AuthenticationController(AccManagementService accManagementService) {
        this.accManagementService = accManagementService;
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
    @PutMapping(path="/enable_acc")
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

}

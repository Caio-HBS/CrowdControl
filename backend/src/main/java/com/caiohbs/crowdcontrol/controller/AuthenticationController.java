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

    @PostMapping(path="/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(accManagementService.authenticate(request));
    }

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
                "Error while trying to enable account. Contact a system administrator"
        );
        return ResponseEntity.badRequest().body(response);

    }

}

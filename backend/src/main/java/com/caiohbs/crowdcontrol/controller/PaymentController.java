package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.PaymentDTO;
import com.caiohbs.crowdcontrol.dto.mapper.PaymentDTOMapper;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.model.Payment;
import com.caiohbs.crowdcontrol.model.Permission;
import com.caiohbs.crowdcontrol.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentDTOMapper paymentDTOMapper;

    public PaymentController(
            PaymentService paymentService,
            PaymentDTOMapper paymentDTOMapper
    ) {
        this.paymentService = paymentService;
        this.paymentDTOMapper = paymentDTOMapper;
    }

    /**
     * Retrieves a list of all payments. This endpoint requires the user to have
     * the {@link Permission} "READ_GENERAL" for the request to be authorized.
     *
     * @return A list of {@link PaymentDTO} objects representing the found
     * payments.
     */
    @GetMapping(path="/payments")
    @PreAuthorize("hasAuthority('READ_GENERAL')")
    public ResponseEntity<List<PaymentDTO>> getPaymentList() {

        List<PaymentDTO> payments = paymentService.retrieveAllPayments()
                .stream().map(paymentDTOMapper).toList();

        return ResponseEntity.ok(payments);

    }

    /**
     * Retrieves all payments to a given user. This endpoint requires the user to
     * either be the owner of the asset and have the {@link Permission} "READ_SELF",
     * or to have the {@link Permission} "READ_GENERAL" for the request to be
     * authorized.
     *
     * @param userId The unique identifier (Long) for the user.
     * @return A {@link ResponseEntity} containing a list of {@link PaymentDTO}
     * objects representing the found user's payments, or a {@link ResponseEntity}
     * with a 404 Not Found status code if no user was found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @GetMapping(path="/users/{userId}/payments")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #userId and hasAuthority('READ_SELF') or hasAuthority('READ_GENERAL')"
    )
    public ResponseEntity<List<PaymentDTO>> getPaymentsForSingleUser(
            @PathVariable Long userId
    ) {

        List<PaymentDTO> payments = paymentService
                .retrieveAllPaymentsForSingleUser(userId).stream()
                .map(paymentDTOMapper).toList();

        return ResponseEntity.ok(payments);
    }

    /**
     * Creates a new payment for a single user. This endpoint requires the user
     * to have the {@link Permission} "CREATE_PAYMENT_GENERAL" for the request
     * to be authorized.
     *
     * @param userId  The unique identifier (Long) for the user to which the
     *                payment is being made.
     * @param payment The {@link Payment} object containing the payment amount.
     * @return A {@link ResponseEntity} containing the created resource. ALso
     * provides a URI pointing at the created resource's new endpoint. The
     * response body also contains a message for users indicating said status.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @PostMapping(path="/users/{userId}/payment")
    @PreAuthorize("hasAuthority('CREATE_PAYMENT_GENERAL')")
    public ResponseEntity<GenericValidResponse> createPayment(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Payment payment
    ) {

        paymentService.createPayment(payment, userId);

        String currUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri().toUriString();
        String baseUri = currUri
                .substring(0, currUri.lastIndexOf("/new-payment"));

        URI uri = UriComponentsBuilder.fromUriString(baseUri).build().toUri();

        GenericValidResponse response = new GenericValidResponse(
                "New payment created successfully."
        );

        return ResponseEntity.created(uri).body(response);

    }

    /**
     * Automatically creates a payment for ALL the users in a given role based
     * on its registered salary. This endpoint requires the user to have the
     * {@link Permission} "CREATE_PAYMENT_FOR_ROLE" for the request to be authorized.
     *
     * @param roleId The unique identifier (Long) for the role to which the
     *               payments will be made.
     * @return A {@link ResponseEntity} with the code 200 - OK, and a successful
     * message. The response body also contains a message for users indicating
     * said status.
     * @throws ResourceNotFoundException if the role is not found or is empty.
     */
    @PostMapping(path="/roles/{roleId}/auto-payment")
    @PreAuthorize("hasAuthority('CREATE_PAYMENT_FOR_ROLE')")
    public ResponseEntity<GenericValidResponse> createAutoPayment(
            @PathVariable("roleId") Long roleId
    ) {

        paymentService.createPaymentForRole(roleId);

        GenericValidResponse response = new GenericValidResponse(
                "Auto payment created successfully."
        );

        return ResponseEntity.ok(response);

    }

    /**
     * Deletes a payment based on the ID. This endpoint requires the user to have
     * the {@link Permission} "DELETE_GENERAL" for the request to be authorized.
     *
     * @return A {@link ResponseEntity} with the code 200 - OK, and a successful
     * message. The response body also contains a message for users indicating
     * said status.
     * @throws ResourceNotFoundException if the payment is not found.
     */
    @DeleteMapping(path="/payments/{id}")
    @PreAuthorize("hasAuthority('DELETE_GENERAL')")
    public ResponseEntity<GenericValidResponse> deletePaymentById(
            @PathVariable Long id
    ) {

        paymentService.deletePayment(id);

        GenericValidResponse response = new GenericValidResponse(
                "Payment deleted successfully."
        );

        return ResponseEntity.ok(response);

    }

}

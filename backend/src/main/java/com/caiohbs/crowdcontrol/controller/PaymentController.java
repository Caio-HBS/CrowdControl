package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.PaymentDTO;
import com.caiohbs.crowdcontrol.dto.mapper.PaymentDTOMapper;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.Payment;
import com.caiohbs.crowdcontrol.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
     * Retrieves a list of all payments.
     *
     * @return A list of {@link PaymentDTO} objects representing the found
     * payments.
     */
    @GetMapping(path="/payments")
    public ResponseEntity<List<PaymentDTO>> getPaymentList() {

        List<PaymentDTO> payments = paymentService.retrieveAllPayments()
                .stream().map(paymentDTOMapper).toList();

        return ResponseEntity.ok(payments);

    }

    /**
     * Retrieves all payments to a given user.
     *
     * @param userId The unique identifier (Long) for the user.
     * @return A {@link ResponseEntity} containing a list of {@link PaymentDTO}
     * objects representing the found user's payments, or a {@link ResponseEntity}
     * with a 404 Not Found status code if no user was found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @GetMapping(path="/users/{userId}/payments")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForSingleUser(
            @PathVariable Long userId
    ) {

        List<PaymentDTO> payments = paymentService
                .retrieveAllPaymentsForSingleUser(userId).stream()
                .map(paymentDTOMapper).toList();

        return ResponseEntity.ok(payments);
    }

    /**
     * Creates a new payment for a single user.
     *
     * @param userId  The unique identifier (Long) for the user to which the
     *                payment is being made.
     * @param payment The {@link Payment} object containing the payment amount.
     * @return A {@link ResponseEntity} containing the created resource. ALso
     * provides a URI pointing at the created resource's new endpoint.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @PostMapping(path="/users/{userId}/new-payment")
    public ResponseEntity<Payment> createPayment(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Payment payment
    ) {

        paymentService.createPayment(payment, userId);

        String currUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri().toUriString();
        String baseUri = currUri
                .substring(0, currUri.lastIndexOf("/new-payment"));

        URI uri = UriComponentsBuilder.fromUriString(baseUri).build().toUri();
        return ResponseEntity.created(uri).build();

    }

    /**
     * Automatically creates a payment for ALL the users in a given role based
     * on its registered salary.
     *
     * @param roleId The unique identifier (Long) for the role to which the
     *               payments will be made.
     * @return A {@link ResponseEntity} with the code 200 - OK, and a successful
     * message.
     * @throws ResourceNotFoundException if the role is not found or is empty.
     */
    @PostMapping(path="/roles/{roleId}/auto-payment")
    public ResponseEntity<String> createAutoPayment(
            @PathVariable("roleId") Long roleId
    ) {

        paymentService.createPaymentForRole(roleId);
        return ResponseEntity.ok("Payment created.");

    }

    /**
     * Deletes a payment based on the ID.
     *
     * @return A {@link ResponseEntity} with the code 200 - OK, and a successful
     * message.
     * @throws ResourceNotFoundException if the payment is not found.
     */
    @DeleteMapping(path="/payments/{id}")
    public ResponseEntity<String> deletePaymentById(@PathVariable Long id) {

        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted successfully.");

    }

}

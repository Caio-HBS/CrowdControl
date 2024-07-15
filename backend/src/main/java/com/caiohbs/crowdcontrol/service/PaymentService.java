package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.Payment;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.PaymentRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Creates a new payment for a given user though the repository.
     *
     * @param payment The payment information containing the payment amount.
     * @param userId  The ID of the user for whom the payment is being created.
     * @throws ResourceNotFoundException If the user with the provided ID is not
     *                                   found.
     */
    public void createPayment(
            Payment payment, Long userId
    ) throws ResourceNotFoundException {

        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Payment newPayment = new Payment(
                foundUser.get(), payment.getPaymentAmount()
        );

        paymentRepository.save(newPayment);
    }

    // TODO: change this Optional.

    /**
     * Creates a new payment with the same amount for ALL the users in a given
     * role.
     *
     * @param roleId The ID of the role for whom the auto payment is being created.
     * @throws ResourceNotFoundException If the role with the provided ID is not
     *                                   found.
     */
    public void createPaymentForRole(
            Long roleId
    ) throws ResourceNotFoundException {

        Optional<List<String>> foundUsersInRole = userRepository
                .findUsernamesByRoleId(roleId);

        if (foundUsersInRole.isPresent()) {
            for (String username : foundUsersInRole.get()) {
                Optional<User> foundUser = userRepository.findByEmail(username);

                if (foundUser.isPresent()) {
                    Payment newPayment = new Payment(
                            foundUser.get(), foundUser.get().getRole().getSalary()
                    );
                    paymentRepository.save(newPayment);
                }
            }
        }

    }

    /**
     * Returns all the payments registered on the database.
     */
    public List<Payment> retrieveAllPayments() {

        return paymentRepository.findAll();

    }

    /**
     * Retrieves all the payments corresponding to a given user.
     *
     * @param userId The ID of the user you wish to see the payments for.
     */
    public List<Payment> retrieveAllPaymentsForSingleUser(Long userId) {

        return paymentRepository.findByUserUserId(userId);

    }

    /**
     * Deletes a payment though it's id.
     *
     * @param paymentId The ID of the payment to be deleted.
     * @throws ResourceNotFoundException If the payment with the provided ID is
     *                                   not found.
     */
    public void deletePayment(Long paymentId) throws ResourceNotFoundException {

        try {
            Payment foundPayment = paymentRepository
                    .findById(paymentId).orElseThrow();
            paymentRepository.delete(foundPayment);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Payment not found.");
        }

    }

}

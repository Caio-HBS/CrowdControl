package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.Payment;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.PaymentRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class PaymentServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PaymentRepository paymentRepository;
    @InjectMocks
    PaymentService paymentService;

    private final Role newRole = new Role("TEST_ROLE", 1, 1000, List.of("DELETE_GENERAL"));

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, null, List.of(), newRole);

    private final Payment newPayment = new Payment(null, 20.00);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully create a new payment")
    void createPayment_Success() {

        newPayment.setUser(newUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        paymentService.createPayment(newPayment, 1L);

        verify(paymentRepository, times(1)).save(any(Payment.class));

    }

    @Test
    @DisplayName("Should fail to create payment because user doesn't exist")
    void createPayment_FailedUserNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(newPayment, 1L));

    }

    @Test
    @DisplayName("Should successfully create payment for entire Role")
    void createPaymentForRole_Success() {

        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of("test@email.com"));
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(newUser));

        paymentService.createPaymentForRole(1L);

        verify(paymentRepository, times(1)).save(any(Payment.class));

    }

    @Test
    @DisplayName("Should fail to create payment for Role because there are no Users in it")
    void createPaymentForRole_FailedNoUsersInRole() throws ResourceNotFoundException {

        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(newPayment, 1L));

    }

    @Test
    @DisplayName("Should successfully retrieve all payments in DB")
    void retrieveAllPayments_Success() {

        Payment newPayment_ = new Payment(null, 0.0);
        List<Payment> payments = Arrays.asList(newPayment, newPayment_);

        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> allPayments = paymentService.retrieveAllPayments();

        verify(paymentRepository, times(1)).findAll();

        assertNotNull(allPayments);
        assertEquals(2, allPayments.size());
        assertEquals(20.0, allPayments.get(0).getPaymentAmount());
        assertEquals(0.0, allPayments.get(1).getPaymentAmount());

    }

    @Test
    @DisplayName("Should return an empty list of payments because there are none in the DB")
    void retrieveAllPayments_EmptyList() {

        when(paymentRepository.findAll()).thenReturn(List.of());

        List<Payment> allPayments = paymentService.retrieveAllPayments();

        assertTrue(allPayments.isEmpty());

    }

    @Test
    @DisplayName("Should successfully retrieve all payments for a single user")
    void retrieveAllPaymentsForSingleUser_Success() {

        Payment newPayment_ = new Payment(null, 0.0);

        newPayment.setUser(newUser);
        newPayment_.setUser(newUser);
        List<Payment> payments = Arrays.asList(newPayment, newPayment_);

        when(paymentRepository.findByUserUserId(1L)).thenReturn(payments);

        List<Payment> result = paymentService.retrieveAllPaymentsForSingleUser(1L);

        verify(paymentRepository, times(1)).findByUserUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(20.0, result.get(0).getPaymentAmount());
        assertEquals(0.0, result.get(1).getPaymentAmount());

    }

    @Test
    @DisplayName("Should return an empty list of payments because there none for specific User in the DB")
    void retrieveAllPaymentsForSingleUser_EmptyList() {

        when(paymentRepository.findByUserUserId(1L)).thenReturn(List.of());

        List<Payment> allPayments = paymentService.retrieveAllPaymentsForSingleUser(1L);

        assertTrue(allPayments.isEmpty());

    }

    @Test
    @DisplayName("Should successfully delete a payment in DB")
    void deletePayment_Success() {

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(newPayment));

        paymentService.deletePayment(1L);

        verify(paymentRepository, times(1)).delete(newPayment);

    }

    @Test
    @DisplayName("Should fail to delete payment because can't find User in DB")
    void deletePayment_FailedPaymentNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.deletePayment(1L));

    }

}
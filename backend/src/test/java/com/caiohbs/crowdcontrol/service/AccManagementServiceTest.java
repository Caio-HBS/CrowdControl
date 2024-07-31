package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.*;
import com.caiohbs.crowdcontrol.repository.EmailCodeRepository;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccManagementServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    EmailCodeRepository emailCodeRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtService jwtService;
    @Mock
    RoleRepository roleRepository;
    @InjectMocks
    AccManagementService accManagementService;

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, List.of(), List.of(), null);

    private final Role newRole = new Role("ADMIN", 1, 1.0, List.of("DELETE_GENERAL"));

    private final EmailCode newEmailCode = new EmailCode("this_is_a_code", true, EmailType.ENABLE_ACC, newUser);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully authenticate user")
    void authenticate_Success() {

        AuthenticationRequest request = new AuthenticationRequest("test@email.com", "789");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser, null, newUser.getAuthorities()
        );

        String token = "this_is_not_a_jwt_token";

        when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        when(userRepository.findByEmail(newUser.getUsername())).thenReturn(Optional.of(newUser));
        when(jwtService.generateToken(newUser)).thenReturn(token);

        AuthenticationResponse response = accManagementService.authenticate(request);

        assertEquals(token, response.getToken());

    }

    @Test
    @DisplayName("Should fail user authentication")
    void authenticate_FailedValidation() throws ValidationErrorException {

        AuthenticationRequest request = new AuthenticationRequest("test@email.com", "wrong-password");

        when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException(""));

        assertThrows(ValidationErrorException.class, () -> accManagementService.authenticate(request));

    }

    @Test
    @DisplayName("Should fail because user was not found")
    void authenticate_FailedUserNotFound() throws ResourceNotFoundException {

        AuthenticationRequest request = new AuthenticationRequest("test@email.com", "789");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                newUser, null, newUser.getAuthorities()
        );

        when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        when(userRepository.findByEmail(newUser.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accManagementService.authenticate(request));

    }

    @Test
    @DisplayName("Should successfully create e-mail code in DB")
    void createEmailCode_Success() {

        accManagementService.createEmailCode(newUser, "RECOV_PASS");

        verify(emailCodeRepository, times(1)).save(Mockito.any(EmailCode.class));

    }

    @Test
    @DisplayName("Should fail to create e-mail code because type is invalid")
    void createEmailCode_FailedInvalidType() throws ResourceNotFoundException {
        assertThrows(ResourceNotFoundException.class, () -> accManagementService.createEmailCode(newUser, "WRONG_TYPE"));
    }

    @Test
    @DisplayName("Should create a superuser successfully")
    void createSuperUser_Success() {

        when(roleRepository.findByRoleName("ADMIN")).thenReturn(null);

        accManagementService.createSuperUser(newUser);

        verify(roleRepository, times(1)).save(Mockito.any(Role.class));
        verify(userRepository, times(2)).save(Mockito.any(User.class));

    }

    @Test
    @DisplayName("Should fail to create superuser because it already exists")
    void createSuperUser_FailedSuperUserFound() throws ValidationErrorException {

        when(roleRepository.findByRoleName("ADMIN")).thenReturn(newRole);

        assertThrows(ValidationErrorException.class, () -> accManagementService.createSuperUser(newUser));

    }

    @Test
    @DisplayName("Should successfully enable account when code is valid")
    void isEmailCodeValid_SuccessEnabledAcc() {

        when(emailCodeRepository.findByEmailCode("code")).thenReturn(newEmailCode);

        boolean isValid = accManagementService.isEmailCodeValid("code");

        verify(emailCodeRepository, times(1)).save(Mockito.any(EmailCode.class));
        verify(userRepository, times(1)).save(Mockito.any(User.class));

        assertTrue(isValid);

    }

    @Test
    @DisplayName("Should authenticate code for password recovery")
    void isEmailCodeValid_SuccessRecoveredPassword() {

        newEmailCode.setEmailType(EmailType.RECOV_PASS);
        when(emailCodeRepository.findByEmailCode("code")).thenReturn(newEmailCode);

        boolean isValid = accManagementService.isEmailCodeValid("code");

        verify(emailCodeRepository, times(1)).save(Mockito.any(EmailCode.class));

        assertTrue(isValid);

    }

    @Test
    @DisplayName("Should fail to find code on DB")
    void isEmailCodeValid_FailedCodeNotFound() throws ResourceNotFoundException {

        when(emailCodeRepository.findByEmailCode("code")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> accManagementService.isEmailCodeValid("code"));

    }

    @Test
    @DisplayName("Should prevent code from being user when it was used before")
    void isEmailCodeValid_FailedCodeUsedBefore() throws ValidationErrorException {

        newEmailCode.setCodeActive(false);
        when(emailCodeRepository.findByEmailCode("code")).thenReturn(newEmailCode);

        assertThrows(ValidationErrorException.class, () -> accManagementService.isEmailCodeValid("code"));

    }

    @Test
    @DisplayName("Should successfully reset a password")
    void resetPassword_Success() {

        UserUpdateDTO updateDTO = new UserUpdateDTO("", false, "123", "123", "", true, "", false);

        accManagementService.resetPassword(newUser, updateDTO);

        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should fail to reset passwords when they don't match")
    void resetPassword_FailedPasswordsDontMatch() throws ValidationErrorException {

        UserUpdateDTO updateDTO = new UserUpdateDTO("", false, "123", "321", "", true, "", false);

        assertThrows(ValidationErrorException.class, () -> accManagementService.resetPassword(newUser, updateDTO));

    }

    @Test
    @DisplayName("Should successfully unlock a user")
    void unlockUser_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        accManagementService.unlockUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should fail to unlock an account when user was not found")
    void unlockUser_FailedUserNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accManagementService.unlockUser(1L));

    }

}
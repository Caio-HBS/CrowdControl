package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserInfoUpdateDTO;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.model.UserInfo;
import com.caiohbs.crowdcontrol.repository.UserInfoRespository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserInfoServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserInfoRespository userInfoRespository;
    @InjectMocks
    UserInfoService userInfoService;

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, List.of(), List.of(), null);

    private final UserInfo userInfo = new UserInfo(newUser, "", "ANY", "This is my bio.", "Brazilian");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully create UserInfo")
    void createInfo_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        UserInfo newInfo = userInfoService.createInfo(1L, userInfo);

        verify(userRepository, times(1)).findById(1L);
        verify(userInfoRespository, times(1)).save(newInfo);

    }

    @Test
    @DisplayName("Should fail creating UserInfo because User can't be found")
    void createInfo_FailedUserNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userInfoService.createInfo(1L, userInfo));

    }

    @Test
    @DisplayName("Should successfully retrieve UserInfo for specific User")
    void retrieveInfo_Success() {

        newUser.setUserInfo(userInfo);
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        UserInfo savedUser = userInfoService.retrieveInfo(1L);

        verify(userRepository, times(1)).findById(1L);

        assertThat(savedUser).isEqualTo(userInfo);

    }

    @Test
    @DisplayName("Should successfully retrieve UserInfo because User can't be found")
    void retrieveInfo_FailedUserNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userInfoService.retrieveInfo(1L));

    }

    @Test
    @DisplayName("Should successfully update UserInfo pfp")
    void updateInfo_SuccessPfp() {

        newUser.setUserInfo(userInfo);
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                true, "this_is_a_picture.png", false, "",
                false, "", false, ""
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        String newPfpName = userInfoService.updateInfo(1L, updateDTO);

        verify(userInfoRespository, times(1)).save(userInfo);

        assertTrue(newPfpName.endsWith("_this_is_a_picture.png"));

    }

    @Test
    @DisplayName("Should successfully update UserInfo pronouns")
    void updateInfo_SuccessPronouns() {

        newUser.setUserInfo(userInfo);
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", true, "THEY/THEM",
                false, "", false, ""
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        String result = userInfoService.updateInfo(1L, updateDTO);

        verify(userInfoRespository, times(1)).save(userInfo);

        assertSame("Success", result);

    }

    @Test
    @DisplayName("Should successfully update UserInfo bio")
    void updateInfo_SuccessBio() {

        newUser.setUserInfo(userInfo);
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", false, "",
                true, "This is a test bio.", false, ""
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        String result = userInfoService.updateInfo(1L, updateDTO);

        verify(userInfoRespository, times(1)).save(userInfo);

        assertSame("Success", result);

    }

    @Test
    @DisplayName("Should successfully update UserInfo nationality")
    void updateInfo_SuccessNationality() {

        newUser.setUserInfo(userInfo);
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", false, "",
                false, "", true, "Armenian"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        String result = userInfoService.updateInfo(1L, updateDTO);

        verify(userInfoRespository, times(1)).save(userInfo);

        assertSame("Success", result);

    }

    @Test
    @DisplayName("Should fail to update UserInfo because User can't be found")
    void updateInfo_FailedUserNotFound() throws ResourceNotFoundException {

        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", false, "",
                false, "", false, ""
        );
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userInfoService.updateInfo(1L, updateDTO));

    }

    @Test
    @DisplayName("Should fail to update UserInfo because it wasn't previously initialized")
    void updateInfo_FailedUserNotInitialized() throws ValidationErrorException {

        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", false, "",
                false, "", false, ""
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        assertThrows(ValidationErrorException.class, () -> userInfoService.updateInfo(1L, updateDTO));

    }

    @Test
    @DisplayName("Should fail to update UserInfo because pronouns are invalid")
    void updateInfo_FailedInvalidPronouns() throws ValidationErrorException {

        newUser.setUserInfo(userInfo);
        UserInfoUpdateDTO updateDTO = new UserInfoUpdateDTO(
                false, "", true, "NOT_A_VALID_PRONOUN",
                false, "", false, ""
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        assertThrows(ValidationErrorException.class, () -> userInfoService.updateInfo(1L, updateDTO));

    }

    @Test
    @DisplayName("Should successfully convert a String to a valid, virtually unique file name")
    void convertFileName_Success() {

        String firstString = "name.png";
        String secondString = "alphabet".repeat(50) + ".jpg";

        String firstTest = userInfoService.convertFileName(firstString);
        String secondTest = userInfoService.convertFileName(secondString);

        assertTrue(firstTest.endsWith("_name.png"));
        assertTrue(secondTest.length() <= 255 && secondTest.endsWith(".jpg"));

    }

}
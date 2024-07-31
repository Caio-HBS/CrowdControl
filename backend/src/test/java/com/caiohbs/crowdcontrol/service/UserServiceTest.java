package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.RoleLimitExceededException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @InjectMocks
    UserService userService;

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, List.of(), List.of(), null);

    private final Role newRole = new Role("TEST_ROLE", 1, 1.0, List.of("DELETE_GENERAL"));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully create a new user")
    void createUser_Success() {

        when(userRepository.findByEmail(newUser.getUsername())).thenReturn(Optional.empty());

        User savedUser = userService.createUser(newUser);

        verify(userRepository, times(1)).save(newUser);
        verify(userRepository, times(1)).findByEmail(newUser.getUsername());

        assertThat(savedUser).isEqualTo(newUser);

    }

    @Test
    @DisplayName("Should throw exception for taken username")
    void createUser_Failed() throws NameTakenException {

        when(userRepository.findByEmail(newUser.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(NameTakenException.class, () -> userService.createUser(newUser));

    }

    @Test
    @DisplayName("Should retrieve a single user from DB")
    void retrieveSingleUser_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        assertThat(userService.retrieveSingleUser(1L)).isPresent();

    }

    @Test
    @DisplayName("Should not retrieve any users")
    void retrieveSingleUser_Failed() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(userService.retrieveSingleUser(1L)).isNotPresent();

    }

    @Test
    @DisplayName("Should retrieve a list of all users")
    void retrieveAllUsers_Success() {

        User user2 = new User(
                newUser.getFirstName(), newUser.getLastName(), "test2@email.com", newUser.getPassword(),
                newUser.getBirthDate(), newUser.getRegisterDate(), null, List.of(), List.of(), null
        );

        when(userRepository.findAll()).thenReturn(List.of(newUser, user2));

        List<User> users = userService.retrieveAllUsers();

        verify(userRepository, times(1)).findAll();

        assertThat(users).hasSize(2);
        assertThat(users).contains(newUser, user2);

    }

    @Test
    @DisplayName("Should retrieve an empty list of users")
    void retrieveAllUsers_Empty() {

        List<User> users = userService.retrieveAllUsers();

        assertThat(users.isEmpty()).isTrue();

    }

    @Test
    @DisplayName("Should successfully update username (e-mail)")
    void updateUser_SuccessUsername() {

        UserUpdateDTO updateDTO = new UserUpdateDTO("e@mail.com", true, "", "", "", false, "", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        userService.updateUser(1L, updateDTO);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should raise exception when trying to find User")
    void updateUser_FailedUsername() throws ResourceNotFoundException {

        UserUpdateDTO updateDTO = new UserUpdateDTO("e@mail.com", true, "123", "123", "321", true, "ADMIN", true);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateDTO));

    }

    @Test
    @DisplayName("Should successfully update password")
    void updateUser_SuccessPassword() {

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "", false, "123", "123", "789", true, "", false
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        userService.updateUser(1L, updateDTO);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should raise exception when trying to update password due to them not matching")
    void updateUser_FailedPasswordsDontMatch() throws ValidationErrorException {

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "", false, "123", "123aa", "789", true, "", false
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        ValidationErrorException exception = assertThrows(ValidationErrorException.class, () -> userService.updateUser(
                1L, updateDTO
        ));

        assertThat(Objects.equals(exception.getMessage(), "New password and confirm password do not match.")).isTrue();

    }

    @Test
    @DisplayName("Should raise exception when trying to update password due to the old one being wrong")
    void updateUser_FailedPasswordWrong() {

        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "", false, "123", "123", "wrong", true, "", false
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        ValidationErrorException exception = assertThrows(ValidationErrorException.class, () -> userService.updateUser(
                1L, updateDTO
        ));

        assertThat(Objects.equals(exception.getMessage(), "Old password is invalid.")).isTrue();

    }

    @Test
    @DisplayName("Should successfully update user role")
    void updateUser_SuccessRole() {

        UserUpdateDTO updateDTO = new UserUpdateDTO("", false, "123", "123", "789", false, "TEST_ROLE", true);
        newRole.setRoleId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        when(roleRepository.findByRoleName(newRole.getRoleName())).thenReturn(newRole);

        userService.updateUser(1L, updateDTO);

        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByRoleName(newRole.getRoleName());
        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should raise exception when trying to find Role")
    void updateUser_Failed_Role() {

        UserUpdateDTO updateDTO = new UserUpdateDTO("", false, "123", "123", "789", false, "WRONG", true);
        newRole.setRoleId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(1L, updateDTO)
        );

        assertThat(Objects.equals(exception.getMessage(), "Role not found.")).isTrue();

    }

    @Test
    @DisplayName("Should successfully assign a new role to a user")
    void assignRole_Success() {

        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));

        userService.assignRole(newUser, 1L);

        verify(userRepository, times(1)).findUsernamesByRoleId(1L);
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(newUser);

    }

    @Test
    @DisplayName("Should throw exception when maximum number of users in role is met")
    void assignRole_Failed() {

        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of("e@mail.com"));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));

        assertThrows(RoleLimitExceededException.class, () -> userService.assignRole(newUser, 1L));

    }

    @Test
    @DisplayName("Should successfully delete a user")
    void deleteUser_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(newUser);

    }

    @Test
    @DisplayName("Should throw exception when it fails to find user")
    void deleteUser_Failed() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));

    }

}
package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.RoleUpdateDTO;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    RoleService roleService;

    private final Role newRole = new Role("TEST_ROLE", 1, 20.0, List.of("DELETE_GENERAL"));

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, null, List.of(), null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully create Role")
    void createRole_Success() {

        when(roleRepository.findByRoleName("TEST_ROLE")).thenReturn(null);

        roleService.createRole(newRole);

        verify(roleRepository, times(1)).save(newRole);

    }

    @Test
    @DisplayName("Should fail to create Role because name is already in use")
    void createRole_FailedNameTaken() throws NameTakenException {

        when(roleRepository.findByRoleName("TEST_ROLE")).thenReturn(newRole);

        assertThrows(NameTakenException.class, () -> roleService.createRole(newRole));

    }

    @Test
    @DisplayName("Should successfully retrieve a Role")
    void retrieveSingleRole_Success() {

        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));

        Optional<Role> result = roleService.retrieveSingleRole(1L);

        verify(roleRepository, times(1)).findById(1L);

        assertTrue(result.isPresent());
        assertEquals(newRole, result.get());

    }

    @Test
    @DisplayName("Should fail to retrieve Role because there are no Roles")
    void retrieveSingleRole_Empty() {

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.retrieveSingleRole(1L);

        verify(roleRepository, times(1)).findById(1L);

        assertFalse(result.isPresent());

    }

    @Test
    @DisplayName("Should successfully retrieve all Roles in DB")
    void retrieveAllRoles_Success() {

        Role newRole_ = new Role("NEW_TEST_ROLE", 1, 20.0, List.of("UPDATE_GENERAL"));

        when(roleRepository.findAll()).thenReturn(List.of(newRole, newRole_));

        List<Role> result = roleService.retrieveAllRoles();

        verify(roleRepository, times(1)).findAll();

        assertEquals(result, List.of(newRole, newRole_));

    }

    @Test
    @DisplayName("Should fail to any retrieve Role because there are none in DB")
    void retrieveAllRoles_EmptyList() {

        when(roleRepository.findAll()).thenReturn(List.of());

        List<Role> result = roleService.retrieveAllRoles();

        verify(roleRepository, times(1)).findAll();

        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("Should successfully update an existing Role")
    void updateRole_SuccessMaxNumUsers() {

        RoleUpdateDTO updateDTO = new RoleUpdateDTO(true, 2, false, 0);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));

        roleService.updateRole(1L, updateDTO);

        verify(roleRepository, times(1)).save(newRole);

    }

    @Test
    @DisplayName("Should successfully update an existing Role")
    void updateRole_SuccessSalary() {

        RoleUpdateDTO updateDTO = new RoleUpdateDTO(false, 0, true, 200.0);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));

        roleService.updateRole(1L, updateDTO);

        verify(roleRepository, times(1)).save(newRole);

    }

    @Test
    @DisplayName("Should fail to update Role because it was not found")
    void updateRole_FailedRoleNotFound() throws ResourceNotFoundException {

        RoleUpdateDTO updateDTO = new RoleUpdateDTO(false, 0, false, 0.0);

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(1L, updateDTO));

    }

    @Test
    @DisplayName("Should successfully delete Role")
    void deleteRole_SuccessWithUsers() {

        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));
        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of("test@email.com"));
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(newUser));

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("test@email.com");
        verify(userRepository, times(1)).save(newUser);
        verify(roleRepository, times(1)).delete(newRole);

    }

    @Test
    @DisplayName("Should successfully delete Role")
    void deleteRole_SuccessNoUsers() {

        when(roleRepository.findById(1L)).thenReturn(Optional.of(newRole));
        when(userRepository.findUsernamesByRoleId(1L)).thenReturn(List.of());

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).delete(newRole);

    }

    @Test
    @DisplayName("Should fail to delete Role because it was not found")
    void deleteRole_FailedRoleNotFound() throws ResourceNotFoundException {

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(1L));

    }

}
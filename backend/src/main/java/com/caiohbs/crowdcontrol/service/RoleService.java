package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.RoleUpdateDTO;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new role through the repository.
     *
     * @param role Role object containing the data of the role to be created.
     * @return The newly created Role object, or throws an exception if the role name is already in use.
     * @throws NameTakenException If the provided name for role is already registered in the system.
     */
    public Role createRole(Role role) throws NameTakenException {

        Role dbRoleNameCheck = roleRepository.findByRoleName(role.getRoleName());

        if (dbRoleNameCheck != null) {
            throw new NameTakenException("Role name already taken.");
        }

        roleRepository.save(role);
        return role;

    }

    /**
     * Retrieves a single role from the database based on the ID.
     *
     * @param roleId The ID of the role to retrieve.
     * @return An {@link Optional} object containing the found role, or an empty {@link Optional} if the role was not
     * found.
     */
    public Optional<Role> retrieveSingleRole(Long roleId) {
        return roleRepository.findById(roleId);
    }

    /**
     * Retrieves all roles found on the database.
     *
     * @return A {@link List} containing all the roles present in the database.
     */
    public List<Role> retrieveAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Updates a role's information in the database.
     *
     * @param roleId         The ID of the user to update.
     * @param updateRoleInfo A {@link RoleUpdateDTO} object containing the update information.
     * @throws ResourceNotFoundException If the role being assigned is not found.
     */
    public void updateRole(Long roleId, RoleUpdateDTO updateRoleInfo) throws ResourceNotFoundException {

        Optional<Role> roleToUpdate = roleRepository.findById(roleId);
        if (roleToUpdate.isEmpty()) {
            throw new ResourceNotFoundException("Role not found.");
        }
        Role foundRole = roleToUpdate.get();

        if (updateRoleInfo.isMaxNumUsersPresent()) {
            foundRole.setMaxNumberOfUsers(updateRoleInfo.maxNumberOfUsers());
        }
        if (updateRoleInfo.isSalaryPresent()) {
            foundRole.setSalary(updateRoleInfo.salary());
        }
        roleRepository.save(foundRole);

    }

    /**
     * Deletes a role from the database based on the ID.
     *
     * @param roleId The ID of the role to be deleted.
     * @throws ResourceNotFoundException If the role with the provided ID is not found.
     */
    public void deleteRole(Long roleId) throws ResourceNotFoundException {

        try {
            Role foundRole = roleRepository.findById(roleId).orElseThrow();

            List<String> userWithRole = userRepository.findUsernamesByRoleId(roleId);

            if (!userWithRole.isEmpty()) {
                // Unassigning role from every user before deleting it.
                for (String username : userWithRole) {
                    Optional<User> user = userRepository.findByEmail(username);
                    user.get().setRole(null);
                    userRepository.save(user.get());
                }
                roleRepository.delete(foundRole);
            } else {
                roleRepository.delete(foundRole);
            }
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Role not found.");
        }

    }

}

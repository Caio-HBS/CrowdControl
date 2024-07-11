package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.exception.RoleLimitExceededException;
import com.caiohbs.crowdcontrol.exception.UsernameTakenException;
import com.caiohbs.crowdcontrol.exception.ValidationErrorException;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Creates a new user through the repository.
     *
     * @param user User object containing the data of the user to be created.
     * @return The newly created User object, or throws an exception if the
     * username is already in use.
     * @throws UsernameTakenException If the provided email is already registered
     *                                in the system.
     */
    public User createUser(User user) throws RuntimeException {

        Optional<User> dbUsernameCheck = userRepository.findByEmail(user.getUsername());

        if (dbUsernameCheck.isPresent()) {
            throw new UsernameTakenException();
        }

        userRepository.save(user);
        return user;

    }

    /**
     * Retrieves a single user from the database based on the ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An {@link Optional} object containing the found user, or an empty
     * Optional if the user is not found.
     */
    public Optional<User> retrieveSingleUser(Long userId) {

        return userRepository.findById(userId);

    }

    /**
     * Retrieves a single user from the database based on the ID.
     *
     * @return A {@link List} containing all the users present in the database.
     */
    public List<User> retrieveAllUsers() {

        return userRepository.findAll();

    }

    /**
     * Updates a user's information in the system.
     *
     * @param userId     The ID of the user to update.
     * @param updateInfo A {@link UserUpdateDTO} object containing the update
     *                   information.
     * @throws ResourceNotFoundException If the user or role being assigned is
     *                                   not found.
     * @throws ValidationErrorException  If the provided old password is invalid
     *                                   or the new password and confirm password
     *                                   do not match.
     */
    public void updateUser(
            Long userId, UserUpdateDTO updateInfo
    ) throws ResourceNotFoundException, ValidationErrorException {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User foundUser = user.get();

        if (updateInfo.isUsernamePresent()) {
            foundUser.setUsername(updateInfo.username());
        }
        if (updateInfo.isNewPasswordPresent()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(updateInfo.oldPassword(), foundUser.getPassword()) &&
                Objects.equals(updateInfo.newPassword(), updateInfo.confirmNewPassword())
            ) {
                foundUser.setPassword(updateInfo.newPassword());
            } else if (
                    !encoder.matches(updateInfo.oldPassword(), foundUser.getPassword())
            ) {
                throw new ValidationErrorException("Old password is invalid.");
            } else if (
                    !Objects.equals(updateInfo.newPassword(), updateInfo.confirmNewPassword())
            ) {
                throw new ValidationErrorException(
                        "New password and confirm password do not match."
                );
            }
        }
        if (updateInfo.isRolesPresent()) {
            Role foundRole = roleRepository.findByRoleName(
                    updateInfo.role().toUpperCase()
            );

            if (foundRole == null) {
                throw new ResourceNotFoundException("Role not found.");
            }
            assignRole(foundUser, foundRole.getRoleId());
        }
        if (updateInfo.isChangeEnabledPresent()) {
            foundUser.setIsEnabled(updateInfo.isEnabled());
        }
        if (updateInfo.isChangeAccountLockedPresent()) {
            foundUser.setIsAccountNonLocked(updateInfo.isAccountNonLocked());
        }
        userRepository.save(foundUser);

    }

    /**
     * Assigns a role to a user in the database.
     *
     * @param user   The {@link User} object to be assigned a role.
     * @param roleId The ID of the role to be assigned.
     * @throws RoleLimitExceededException If the role has reached its maximum
     *                                    number of users.
     */
    public void assignRole(
            User user, long roleId
    ) throws RoleLimitExceededException {

        int userCurrCount, roleMaxCount;

        Optional<List<String>> usersList = userRepository.findUsernamesByRoleId(roleId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (usersList.isPresent() && role.isPresent()) {
            userCurrCount = usersList.get().size();
            roleMaxCount = role.get().getMaxNumberOfUsers();

            if (userCurrCount >= roleMaxCount) {
                throw new RoleLimitExceededException(
                        "The maximum number of users for role '" +
                        role.get().getRoleName() +
                        "' has been reached."
                );
            }
            user.setRole(role.get());
            userRepository.save(user);
        }

    }

    /**
     * Deletes a user from the database based on the ID.
     *
     * @param userId The ID of the user to be deleted.
     * @throws ResourceNotFoundException If the user with the provided ID is not
     *                                   found.
     */
    public void deleteUser(Long userId) throws ResourceNotFoundException {

        try {
            User foundUser = userRepository.findById(userId).orElseThrow();
            userRepository.delete(foundUser);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("User not found.");
        }

    }

}

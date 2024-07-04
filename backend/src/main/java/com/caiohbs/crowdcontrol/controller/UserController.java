package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserDTO;
import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.UserDTOMapper;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class UserController {

    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(
            UserRepository userRepository,
            UserDTOMapper userDTOMapper, RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.roleRepository = roleRepository;
    }

//    TODO: add view to retrieve base info when logging in.

    /**
     * Retrieves a list of all users.
     *
     * @return A list of {@link UserDTO} objects representing the found users.
     */
    @GetMapping(path="/users")
    public ResponseEntity<List<UserDTO>> getUsersList() {

        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);

    }

    /**
     * Retrieves a single user based on ID tag.
     *
     * @param pathId The unique identifier (Long) of the user to be retrieved.
     *
     * @return containing a {@link UserDTO} object representing the found user,
     * or a {@link ResponseEntity} with a 404 Not Found status code if no user
     * is found.
     */
    @GetMapping(path="/users/{pathId}")
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable Long pathId) {

        return userRepository.findById(pathId)
                .map(user -> ResponseEntity.ok(userDTOMapper.apply(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Creates a new user.
     *
     * @param user The user object to be created. The object should be a valid
     * {@link User} with all required fields populated.
     *
     * @return A {@link ResponseEntity} with the according status code. 201
     * CREATED indicates creation of the resource. Any errors (including
     * validation) will result in a 400 BAD REQUEST. If the user was created
     * successfully, the response will include a Location header pointing to the
     * URI of the newly created user.
     *
     */
    @PostMapping(path="/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

        try {
            User savedUser = userRepository.save(user);

            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedUser.getUserId())
                    .toUri();
            return ResponseEntity.created(uri).build();
        } catch (Exception e) {
            // TODO: gracefully handle this.
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Updates an existing user.
     *
     * @param id The unique identifier (Long) of the user to update.
     * @param updatedUserDTO The {@link UserUpdateDTO} object containing the
     * update information for the user. Only fields
     * present in the DTO will be updated.
     *
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the resource was updated successfully. 400 BAD REQUEST
     * indicates issues on request (including validation). 404 NOT FOUND
     * indicates the requested resource does not exist or couldn't be found. If
     * the user was updated successfully, the response will include a Location
     * header pointing to the URI of the newly updated user.
     *
     */
    @PutMapping(path="/users/{id}")
    public ResponseEntity<String> updateUserById(
            @RequestBody UserUpdateDTO updatedUserDTO,
            @PathVariable Long id
    ) {

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User foundUser = user.get();

        try {
            if (updatedUserDTO.isUsernamePresent()) {
                foundUser.setUsername(updatedUserDTO.username());
            }
            if (updatedUserDTO.isNewPasswordPresent()) {
                foundUser.setPassword(updatedUserDTO.newPassword());
            }
            if (updatedUserDTO.isRolesPresent()) {
                Role foundRole = roleRepository.findByRoleName(updatedUserDTO.role().toUpperCase());

                foundUser.setRole(foundRole);
                if (foundRole == null) {
                    return ResponseEntity.notFound().build();
                }
            }
            if (updatedUserDTO.isChangeEnabledPresent()) {
                foundUser.setIsEnabled(updatedUserDTO.isEnabled());
            }
            if (updatedUserDTO.isChangeAccountLockedPresent()) {
                foundUser.setIsAccountNonLocked(updatedUserDTO.isAccountNonLocked());
            }
            userRepository.save(foundUser);

            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .buildAndExpand(foundUser.getUserId())
                    .toUri();

            return ResponseEntity.created(uri).build();

        } catch (Exception e) {
            // TODO: gracefully handle this.
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The unique identifier (Long) of the user to delete.
     *
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the user was successfully deleted. 404 NOT FOUND indicates the
     * requested resource couldn't be found.
     *
     */
    @DeleteMapping(path="/users/{id}")
    public ResponseEntity<String> deleteSingleUser(@PathVariable Long id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}

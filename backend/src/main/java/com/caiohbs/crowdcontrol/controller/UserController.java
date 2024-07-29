package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserDTO;
import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.UserDTOMapper;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.model.Permission;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.service.AccManagementService;
import com.caiohbs.crowdcontrol.service.EmailSenderService;
import com.caiohbs.crowdcontrol.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class UserController {

    private final UserDTOMapper userDTOMapper;
    private final UserService userService;
    private final AccManagementService accManagementService;
    private final EmailSenderService emailSenderService;

    public UserController(
            UserService userService,
            UserDTOMapper userDTOMapper,
            AccManagementService accManagementService,
            EmailSenderService emailSenderService) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
        this.accManagementService = accManagementService;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Retrieves a list of all users. This endpoint requires the user to have the
     * {@link Permission} "READ_GENERAL" for the request to be authorized.
     *
     * @return A list of {@link UserDTO} objects representing the found users.
     */
    @GetMapping(path="/users")
    @PreAuthorize("hasAuthority('READ_GENERAL')")
    public ResponseEntity<List<UserDTO>> getUsersList() {

        return ResponseEntity.ok(userService.retrieveAllUsers()
                .stream().map(userDTOMapper)
                .collect(Collectors.toList()));

    }

    /**
     * Retrieves a single user based on an ID tag. This endpoint requires either
     * the user making the request being the owner of the asset AND having the
     * {@link Permission} "READ_SELF", or the {@link Permission} "READ_GENERAL"
     * for the request to be authorized.
     *
     * @param userId The unique identifier (Long) of the user to be retrieved.
     * @return containing a {@link UserDTO} object representing the found user,
     * or a {@link ResponseEntity} with a 404 Not Found status code if no user
     * is found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @GetMapping(path="/users/{userId}")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #userId and hasAuthority('READ_SELF') or hasAuthority('READ_GENERAL')"
    )
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable Long userId) {

        return userService.retrieveSingleUser(userId)
                .map(user -> ResponseEntity.ok(userDTOMapper.apply(user)))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found.")
                );

    }

    /**
     * Creates a new user. This endpoint requires the user to have the
     * {@link Permission} "CREATE_USER_GENERAL" for the request to be authorized.
     *
     * @param user The user object to be created. The object should be a valid
     *             {@link User} with all required fields populated.
     * @return A {@link ResponseEntity} with the according status code. 201
     * CREATED indicates creation of the resource. Any errors (including
     * validation) will result in a 400 BAD REQUEST. If the user was created
     * successfully, the response will include a Location header pointing to the
     * URI of the newly created user. The response body also contains a message
     * for users indicating said status.
     * @throws NameTakenException if the username (e-mail) is already in use.
     */
    @PostMapping(path="/users")
    @PreAuthorize("hasAuthority('CREATE_USER_GENERAL')")
    public ResponseEntity<GenericValidResponse> createUser(
            @Valid @RequestBody User user
    ) {

        User savedUser = userService.createUser(user);
        String code = accManagementService.createEmailCode(user, "ENABLE_ACC");

        new Thread(() -> emailSenderService.sendEmail(
                user.getUsername(), "ENABLE_ACC", code)
        ).start();


        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getUserId())
                .toUri();

        GenericValidResponse response = new GenericValidResponse(
                "User created successfully. Please check your e-mail for " +
                "information on how to activate your account."
        );

        return ResponseEntity.created(uri).body(response);

    }

    /**
     * Updates an existing user. This endpoint requires the user to have the
     * {@link Permission} "UPDATE_GENERAL" for the request to be authorized.
     *
     * @param userId         The unique identifier (Long) of the user to update.
     * @param updatedUserDTO The {@link UserUpdateDTO} object containing the
     *                       update information for the user. Only fields
     *                       present in the DTO will be updated.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the resource was updated successfully. 400 BAD REQUEST
     * indicates issues on request (including validation). 404 NOT FOUND
     * indicates the requested resource does not exist or couldn't be found. If
     * the user was updated successfully, the response will include a Location
     * header pointing to the URI of the newly updated user. The response body
     * also contains a message for users indicating said status.
     */
    @PutMapping(path="/users/{userId}")
    @PreAuthorize("hasAuthority('UPDATE_GENERAL')")
    public ResponseEntity<GenericValidResponse> updateUserById(
            @Valid @RequestBody UserUpdateDTO updatedUserDTO,
            @PathVariable Long userId
    ) {

        userService.updateUser(userId, updatedUserDTO);

        GenericValidResponse response = new GenericValidResponse(
                "User updated successfully."
        );

        return ResponseEntity.ok(response);

    }

    /**
     * Deletes a user by their ID. This endpoint requires the user to have the
     * {@link Permission} "DELETE_GENERAL" for the request to be authorized.
     *
     * @param userId The unique identifier (Long) of the user to delete.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the user was successfully deleted. 404 NOT FOUND indicates the
     * requested resource couldn't be found. The response body also contains a
     * message for users indicating said status.
     * @throws ResourceNotFoundException if the user ID is not valid.
     */
    @DeleteMapping(path="/users/{userId}")
    @PreAuthorize("hasAuthority('DELETE_GENERAL')")
    public ResponseEntity<GenericValidResponse> deleteSingleUser(
            @PathVariable Long userId
    ) {

        userService.deleteUser(userId);

        GenericValidResponse response = new GenericValidResponse(
                "User deleted successfully."
        );

        return ResponseEntity.ok(response);

    }

}

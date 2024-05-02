package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserDTO;
import com.caiohbs.crowdcontrol.dto.UserUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.UserDTOMapper;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import com.caiohbs.crowdcontrol.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: add documentation and fix ResponseEntity.

@RestController
@RequestMapping(path="/api/v1")
public class UserController {

    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public UserController(
            UserRepository userRepository,
            UserDTOMapper userDTOMapper,
            RoleRepository roleRepository,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @GetMapping(path="/users")
    public List<UserDTO> getUsers() {

        return userRepository.findAll()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    @PostMapping(path="/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(user);
    }


    @GetMapping(path="/users/{pathId}")
    public Optional<UserDTO> getSingleUser(@PathVariable Long pathId) {

        return userRepository.findById(pathId).map(userDTOMapper);
    }

    @PutMapping(path="/users/{id}")
    public ResponseEntity updateUserById(
            @Valid @RequestBody UserUpdateDTO updatedUserDTO,
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
                foundUser.setRole(updatedUserDTO.role());
            }
            if (updatedUserDTO.isChangeEnabledPresent()) {
                foundUser.setIsEnabled(updatedUserDTO.isEnabled());
            }
            if (updatedUserDTO.isChangeAccountLockedPresent()) {
                foundUser.setIsAccountNonLocked(updatedUserDTO.isAccountNonLocked());
            }
            userRepository.save(foundUser);

            // TODO: change this to return URI.
            return ResponseEntity.ok(foundUser);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(path="/users/{id}")
    public ResponseEntity deleteSingleUser(@PathVariable Long id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        // TODO: change this to return message.
        return ResponseEntity.ok().build();
    }

}

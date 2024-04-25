package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserDTO;
import com.caiohbs.crowdcontrol.dto.mapper.UserDTOMapper;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class UserController {

    private final UserDTOMapper userDTOMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, UserDTOMapper userDTOMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.roleRepository = roleRepository;
    }

    @GetMapping(path="/users")
    public List<UserDTO> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    @PostMapping(path="/users")
    public void createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        System.out.println("Saved user: " + savedUser);
    }


    @GetMapping(path="/users/{pathId}")
    public Optional<UserDTO> getSingleUser(@PathVariable Long pathId) {
        return userRepository.findById(pathId).map(userDTOMapper);
    }

    // TODO: this view.
//    @PutMapping(path="/users/{id}")
//    public ResponseEntity updateUserById(@RequestBody UserDTO userDTO, @PathVariable Long id ) {
//        Optional<User> user = userRepository.findById(id);
//        if(user.isPresent()) {}
//    }

//    TODO: this view.
//    @DeleteMapping(path="/users/{id}")
//    public ResponseEntity<?> deleteSingleUser(@PathVariable Long id) {}

}

package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.RoleDTO;
import com.caiohbs.crowdcontrol.dto.RoleUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.RoleDTOMapper;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// TODO: documentation.

@RestController
@RequestMapping(path="/api/v1")
public class RoleController {

    private final RoleRepository roleRepository;
    private final RoleDTOMapper roleDTOMapper;

    public RoleController(
            RoleRepository roleRepository, RoleDTOMapper roleDTOMapper
    ) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @GetMapping(path="/roles")
    public ResponseEntity<List<RoleDTO>> roles() {

        List<RoleDTO> roles = roleRepository.findAll()
                .stream().map(roleDTOMapper)
                .toList();

        if (roles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(roles);

    }

    @GetMapping(path="/roles/{id}")
    public ResponseEntity<RoleDTO> singleRole(@PathVariable Long id) {

        return roleRepository.findById(id)
                .map(role -> ResponseEntity.ok(roleDTOMapper.apply(role)))
                .orElse(ResponseEntity.notFound().build());

    }

    // TODO: Gracefully handle this exception.
    @PostMapping(path="/roles")
    public void createRole(@Valid @RequestBody Role role) {

        try {
            Role savedRole = roleRepository.save(role);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

    }

    @PutMapping(path="/roles/{id}")
    public ResponseEntity<String> updateRole(
            @RequestBody RoleUpdateDTO role, @PathVariable Long id
    ) {

        Optional<Role> roleToUpdate = roleRepository.findById(id);
        if (roleToUpdate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Role foundRole = roleToUpdate.get();

        try {
            if (role.isMaxNumUsersPresent()) {
                foundRole.setMaxNumberOfUsers(role.maxNumberOfUsers());
            } if (role.isSalaryPresent()) {
                foundRole.setSalary(role.salary());
            }
            roleRepository.save(foundRole);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // TODO: Gracefully handle this.
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping(path="/roles/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        // TODO: make this set users in role to null BEFORE deleting.
        Optional<Role> foundRole = roleRepository.findById(id);

        if (foundRole.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        roleRepository.deleteById(id);
        return ResponseEntity.ok("Role deleted successfully.");
    }

}

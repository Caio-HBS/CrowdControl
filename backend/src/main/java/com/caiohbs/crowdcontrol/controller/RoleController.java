package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.RoleDTO;
import com.caiohbs.crowdcontrol.dto.RoleUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.RoleDTOMapper;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class RoleController {

    private final RoleDTOMapper roleDTOMapper;
    private final RoleService roleService;

    public RoleController(
            RoleDTOMapper roleDTOMapper, RoleService roleService
    ) {
        this.roleDTOMapper = roleDTOMapper;
        this.roleService = roleService;
    }

    /**
     * Retrieves a list of all roles.
     *
     * @return A list of {@link RoleDTO} objects representing the found roles.
     */
    @GetMapping(path="/roles")
    public ResponseEntity<List<RoleDTO>> getRolesList() {

        return ResponseEntity.ok(roleService.retrieveAllRoles()
                .stream().map(roleDTOMapper)
                .collect(Collectors.toList()));

    }

    /**
     * Retrieves a single role based on an ID tag.
     *
     * @param id The unique identifier (Long) of the role to be retrieved.
     * @return containing a {@link RoleDTO} object representing the found role,
     * or a {@link ResponseEntity} with a 404 Not Found status code if no role
     * is found.
     * @throws ResourceNotFoundException if the role is not found.
     */
    @GetMapping(path="/roles/{id}")
    public ResponseEntity<RoleDTO> getSingleRole(@PathVariable Long id) {

        return roleService.retrieveSingleRole(id)
                .map(role -> ResponseEntity.ok(roleDTOMapper.apply(role)))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));

    }

    /**
     * Creates a new role.
     *
     * @param role The role object to be created. The object should be a valid
     *             {@link Role} with all required fields populated.
     * @return A {@link ResponseEntity} with the according status code. 201
     * CREATED indicates creation of the resource. Any errors (including
     * validation) will result in a 400 BAD REQUEST. If the role was created
     * successfully, the response will include a Location header pointing to the
     * URI of the newly created role.
     * @throws NameTakenException if the role name is already in use.
     */
    @PostMapping(path="/roles")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {

        Role createdRole = roleService.createRole(role);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRole.getRoleId())
                .toUri();
        return ResponseEntity.created(uri).build();

    }

    /**
     * Updates an existing role.
     *
     * @param id            The unique identifier (Long) of the role to update.
     * @param updateRoleDTO The {@link RoleUpdateDTO} object containing the
     *                      update information for the role. Only fields
     *                      present in the DTO will be updated.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the resource was updated successfully. 400 BAD REQUEST
     * indicates issues on request (including validation). 404 NOT FOUND
     * indicates the requested resource does not exist or couldn't be found. If
     * the role was updated successfully, the response will include a Location
     * header pointing to the URI of the newly updated role.
     */
    @PutMapping(path="/roles/{id}")
    public ResponseEntity<String> updateRoleById(
            @RequestBody RoleUpdateDTO updateRoleDTO, @PathVariable Long id
    ) {

        roleService.updateRole(id, updateRoleDTO);
        return ResponseEntity.ok().build();

    }

    /**
     * Deletes a role by their ID.
     *
     * @param id The unique identifier (Long) of the role to be deleted.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the role was successfully deleted. 404 NOT FOUND indicates the
     * requested resource couldn't be found.
     * @throws ResourceNotFoundException if the role ID is not valid.
     */
    @DeleteMapping(path="/roles/{id}")
    public ResponseEntity<String> deleteSingleRole(@PathVariable Long id) {

        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully.");

    }

}

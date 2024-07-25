package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.RoleDTO;
import com.caiohbs.crowdcontrol.dto.RoleUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.RoleDTOMapper;
import com.caiohbs.crowdcontrol.exception.NameTakenException;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.model.Permission;
import com.caiohbs.crowdcontrol.model.Role;
import com.caiohbs.crowdcontrol.service.RoleService;
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
     * Retrieves a list of all roles. This endpoint requires the user to have the
     * {@link Permission} "READ_GENERAL" for the request to be authorized.
     *
     * @return A list of {@link RoleDTO} objects representing the found roles.
     */
    @GetMapping(path="/roles")
    @PreAuthorize("hasAuthority('READ_GENERAL')")
    public ResponseEntity<List<RoleDTO>> getRolesList() {

        return ResponseEntity.ok(roleService.retrieveAllRoles()
                .stream().map(roleDTOMapper)
                .collect(Collectors.toList()));

    }

    /**
     * Retrieves a single role based on an ID tag. This endpoint requires the user
     * to be the owner of the asset and have the {@link Permission} "READ_SELF",
     * or have the {@link Permission} "READ_GENERAL" for the request to be authorized.
     *
     * @param roleId The unique identifier (Long) of the role to be retrieved.
     * @return containing a {@link RoleDTO} object representing the found role,
     * or a {@link ResponseEntity} with a 404 Not Found status code if no role
     * is found.
     * @throws ResourceNotFoundException if the role is not found.
     */
    @GetMapping(path="/roles/{roleId}")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #roleId and hasAuthority('READ_SELF') or hasAuthority('READ_GENERAL')"
    )
    public ResponseEntity<RoleDTO> getSingleRole(@PathVariable Long roleId) {

        return roleService.retrieveSingleRole(roleId)
                .map(role -> ResponseEntity.ok(roleDTOMapper.apply(role)))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));

    }

    /**
     * Creates a new role. This endpoint requires the user to have the
     * {@link Permission} "CREATE_ROLE_GENERAL" for the request to be authorized.
     *
     * @param role The role object to be created. The object should be a valid
     *             {@link Role} with all required fields populated.
     * @return A {@link ResponseEntity} with the according status code. 201
     * CREATED indicates creation of the resource. Any errors (including
     * validation) will result in a 400 BAD REQUEST. If the role was created
     * successfully, the response will include a Location header pointing to the
     * URI of the newly created role. The response body also contains a message
     * for users indicating said status.
     * @throws NameTakenException if the role name is already in use.
     */
    @PostMapping(path="/roles")
    @PreAuthorize("hasAuthority('CREATE_ROLE_GENERAL')")
    public ResponseEntity<GenericValidResponse> createRole(
            @Valid @RequestBody Role role
    ) {

        Role createdRole = roleService.createRole(role);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRole.getRoleId())
                .toUri();

        GenericValidResponse response = new GenericValidResponse(
                "Role created successfully."
        );
        return ResponseEntity.created(uri).body(response);

    }

    /**
     * Updates an existing role. This endpoint requires the user to have the
     * {@link Permission} "UPDATE_GENERAL" for the request to be authorized.
     *
     * @param roleId        The unique identifier (Long) of the role to update.
     * @param updateRoleDTO The {@link RoleUpdateDTO} object containing the
     *                      update information for the role. Only fields
     *                      present in the DTO will be updated.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the resource was updated successfully. 400 BAD REQUEST
     * indicates issues on request (including validation). 404 NOT FOUND
     * indicates the requested resource does not exist or couldn't be found. If
     * the role was updated successfully, the response will include a Location
     * header pointing to the URI of the newly updated role. The response body
     * also contains a message for users indicating said status.
     */
    @PutMapping(path="/roles/{roleId}")
    @PreAuthorize("hasAuthority('UPDATE_GENERAL')")
    public ResponseEntity<GenericValidResponse> updateRoleById(
            @RequestBody RoleUpdateDTO updateRoleDTO, @PathVariable Long roleId
    ) {

        roleService.updateRole(roleId, updateRoleDTO);

        GenericValidResponse response = new GenericValidResponse(
                "Role updated successfully."
        );
        return ResponseEntity.ok().body(response);

    }

    /**
     * Deletes a role by their ID. This endpoint requires the user to have the
     * {@link Permission} "DELETE_GENERAL" for the request to be authorized.
     *
     * @param roleId The unique identifier (Long) of the role to be deleted.
     * @return A {@link ResponseEntity} with the according status code. 200 OK
     * indicates the role was successfully deleted. 404 NOT FOUND indicates the
     * requested resource couldn't be found. The response body also contains a
     * message for users indicating said status.
     * @throws ResourceNotFoundException if the role ID is not valid.
     */
    @DeleteMapping(path="/roles/{roleId}")
    @PreAuthorize("hasAuthority('DELETE_GENERAL')")
    public ResponseEntity<GenericValidResponse> deleteSingleRole(
            @PathVariable Long roleId
    ) {

        roleService.deleteRole(roleId);

        GenericValidResponse response = new GenericValidResponse(
                "Role deleted successfully."
        );
        return ResponseEntity.ok(response);

    }

}

package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.UserInfoDTO;
import com.caiohbs.crowdcontrol.dto.UserInfoUpdateDTO;
import com.caiohbs.crowdcontrol.dto.mapper.UserInfoDTOMapper;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.model.Permission;
import com.caiohbs.crowdcontrol.model.UserInfo;
import com.caiohbs.crowdcontrol.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping(path="/api/v1")
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * Retrieves user information for a given user ID. This endpoint requires
     * the user to either have the {@link Permission} "READ_SELF", or
     * "READ_GENERAL" for the request to be authorized.
     *
     * @param userId The ID of the user whose information to retrieve.
     * @return A ResponseEntity with an OK status and the user's information as
     * a {@link UserInfoDTO} object.
     */
    @GetMapping(path="users/{userId}/info")
    @PreAuthorize("hasAuthority('READ_SELF') or hasAuthority('READ_GENERAL')")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long userId) {

        UserInfo foundInfo = userInfoService.retrieveInfo(userId);
        UserInfoDTO userInfoDTO = new UserInfoDTOMapper().apply(foundInfo);

        return ResponseEntity.ok(userInfoDTO);

    }

    /**
     * Creates user information for a given user ID. Handles the incoming POST
     * request to create user information. Should the call be successful, returns
     * the URI of the updated user in the response header. This endpoint requires
     * the user to be the owner of the asset AND have the {@link Permission}
     * "CREATE_INFO_SELF" for the request to be authorized.
     *
     * @param userId   The ID of the user for whom to create user information.
     * @param userInfo The {@link UserInfo} information to be created.
     * @return A ResponseEntity with a CREATED status, a location header pointing
     * to the newly created resource, and a response body containing the profile
     * picture filename.
     */
    @PostMapping("users/{userId}/info")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #userId and hasAuthority('CREATE_INFO_SELF')"
    )
    public ResponseEntity<GenericValidResponse> createUserInfo(
            @PathVariable Long userId, @RequestBody UserInfo userInfo
    ) {

        UserInfo newUserInfo = userInfoService.createInfo(userId, userInfo);

        String currUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri().toUriString();
        String baseUri = currUri
                .substring(0, currUri.lastIndexOf("/info"));

        URI uri = UriComponentsBuilder.fromUriString(baseUri).build().toUri();

        GenericValidResponse response = new GenericValidResponse(newUserInfo.getPfp());
        return ResponseEntity.created(uri).body(response);

    }

    /**
     * Updates user information for a given user ID. Handles the incoming PUT
     * request to update user information. This endpoint requires the user to
     * be the owner of the asset and have the {@link Permission} "UPDATE_INFO_SELF"
     * for the request to be authorized.
     *
     * @param userId   The ID of the user whose information to update.
     * @param userInfo The updated user information to be applied.
     * @return A ResponseEntity with a GenericValidResponse indicating success
     * or the result of the update operation.
     */
    @PutMapping(path="users/{userId}/info")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #userId and hasAuthority('UPDATE_INFO_SELF')"
    )
    public ResponseEntity<GenericValidResponse> updateUserInfo(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserInfoUpdateDTO userInfo
    ) {

        String tryToUpdate = userInfoService.updateInfo(userId, userInfo);

        GenericValidResponse response = new GenericValidResponse();
        if (Objects.equals(tryToUpdate, "Success")) {
            response.setMessage("User info updated successfully.");
        } else {
            response.setMessage(tryToUpdate);
        }

        return ResponseEntity.ok(response);

    }

}

package com.caiohbs.crowdcontrol.dto;

import jakarta.validation.constraints.*;

public record UserUpdateDTO(

        @Email
        String username,
        boolean isUsernamePresent,

        String newPassword,
        String confirmNewPassword,
        String oldPassword,
        boolean isNewPasswordPresent,

        String role,
        boolean isRolesPresent

) {
}

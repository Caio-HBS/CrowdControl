package com.caiohbs.crowdcontrol.dto;

public record UserUpdateDTO(
        String username,
        boolean isUsernamePresent,

        String newPassword,
        String confirmNewPassword,
        boolean isNewPasswordPresent,

        boolean isRolesPresent,
        String role,

        boolean isEnabled,
        boolean isChangeEnabledPresent,

        boolean isAccountNonLocked,
        boolean isChangeAccountLockedPresent
) {
}

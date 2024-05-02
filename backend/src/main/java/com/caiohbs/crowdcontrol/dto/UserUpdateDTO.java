package com.caiohbs.crowdcontrol.dto;

import com.caiohbs.crowdcontrol.model.Role;

public record UserUpdateDTO(
        String username,
        boolean isUsernamePresent,

        String newPassword,
        String confirmNewPassword,
        boolean isNewPasswordPresent,

        boolean isRolesPresent,
        Role role,

        boolean isEnabled,
        boolean isChangeEnabledPresent,

        boolean isAccountNonLocked,
        boolean isChangeAccountLockedPresent
) {
}

package com.caiohbs.crowdcontrol.dto;

import java.util.List;

public record RoleDTO(
        long roleId,
        String roleName,
        int maxNumberOfUsers,
        double salary,
        List<String> permissions,
        List<String> usersInGroup
) {
}

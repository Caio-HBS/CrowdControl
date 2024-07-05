package com.caiohbs.crowdcontrol.dto;

import java.util.List;

public record RoleUpdateDTO(
        boolean isMaxNumUsersPresent,
        int maxNumberOfUsers,

        boolean isSalaryPresent,
        double salary
) {
}

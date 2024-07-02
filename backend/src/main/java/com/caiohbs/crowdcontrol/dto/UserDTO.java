package com.caiohbs.crowdcontrol.dto;

import java.time.LocalDate;
import java.util.List;

public record UserDTO(
        Long userId,
        String username,
        String firstName,
        String lastName,
        LocalDate birthDate,
        LocalDate registerDate,
        boolean isEnabled,
        boolean isAccountNonLocked,
        String role,
        List<String> authorities
) {
}

package com.caiohbs.crowdcontrol.dto;

public record UserInfoDTO(
        Long userId,
        String pfp,
        String pronouns,
        String bio,
        String nationality
) {
}

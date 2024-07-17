package com.caiohbs.crowdcontrol.dto;

import jakarta.validation.constraints.Size;

public record UserInfoUpdateDTO(
        boolean isPfpPresent,
        String  pfp,

        boolean isPronounsPresent,
        String pronouns,

        boolean isBioPresent,
        @Size(max=279)
        String bio,

        boolean isNationalityPresent,
        String nationality
) {
}

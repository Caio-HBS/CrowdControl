package com.caiohbs.crowdcontrol.dto;

import java.time.LocalDate;

public record SickNoteDTO(
        Long userId,
        Long sickNoteId,
        String sickNote,
        LocalDate sickNoteDate
) {
}

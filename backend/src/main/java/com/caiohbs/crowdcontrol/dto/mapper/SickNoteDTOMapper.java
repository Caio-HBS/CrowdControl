package com.caiohbs.crowdcontrol.dto.mapper;

import com.caiohbs.crowdcontrol.dto.SickNoteDTO;
import com.caiohbs.crowdcontrol.model.SickNote;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SickNoteDTOMapper implements Function<SickNote, SickNoteDTO> {

    @Override
    public SickNoteDTO apply(SickNote sickNote) {
        return new SickNoteDTO(
                sickNote.getUser().getUserId(),
                sickNote.getSickNoteId(),
                "/" + sickNote.getSickNote(),
                sickNote.getSickNoteDate()
        );
    }

}

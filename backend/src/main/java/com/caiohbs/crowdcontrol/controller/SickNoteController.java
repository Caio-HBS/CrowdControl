package com.caiohbs.crowdcontrol.controller;

import com.caiohbs.crowdcontrol.dto.SickNoteDTO;
import com.caiohbs.crowdcontrol.dto.mapper.SickNoteDTOMapper;
import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.GenericValidResponse;
import com.caiohbs.crowdcontrol.model.Permission;
import com.caiohbs.crowdcontrol.model.SickNote;
import com.caiohbs.crowdcontrol.service.SickNoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class SickNoteController {

    private final SickNoteService sickNoteService;
    private final SickNoteDTOMapper sickNoteDTOMapper;

    public SickNoteController(
            SickNoteService sickNoteService, SickNoteDTOMapper sickNoteDTOMapper
    ) {
        this.sickNoteService = sickNoteService;
        this.sickNoteDTOMapper = sickNoteDTOMapper;
    }

    /**
     * Retrieves a list of all sick notes. This endpoint requires the user to
     * have the {@link Permission} "READ_GENERAL" for the request to be
     * authorized.
     *
     * @return A list of {@link SickNoteDTO} objects representing the found
     * sick notes.
     */
    @GetMapping(path="/sick-notes")
    @PreAuthorize("hasAuthority('READ_GENERAL')")
    public ResponseEntity<List<SickNoteDTO>> getSickNoteList() {

        List<SickNoteDTO> sickNotes = sickNoteService.retrieveAllSickNotes()
                .stream().map(sickNoteDTOMapper).toList();

        return ResponseEntity.ok(sickNotes);

    }

    /**
     * Retrieves all sick notes to a given user. This endpoint requires the user
     * to either be the owner of the asset and have the {@link Permission}
     * "READ_SELF", or have the {@link Permission} "READ_GENERAL" for the request to
     * be authorized.
     *
     * @param userId The unique identifier (Long) for the user.
     * @return A {@link ResponseEntity} containing a list of {@link SickNoteDTO}
     * objects representing the found user's sick notes, or a {@link ResponseEntity}
     * with a 404 Not Found status code if no user was found.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @GetMapping(path="/users/{userId}/sick-notes")
    @PreAuthorize(
            "@securityUtils.getAuthUserId() == #userId and hasAuthority('READ_SELF') or hasAuthority('READ_GENERAL')"
    )
    public ResponseEntity<List<SickNoteDTO>> getSickNotesForSingleUser(
            @PathVariable Long userId
    ) {

        List<SickNoteDTO> sickNotes = sickNoteService
                .retrieveAllSickNotesForSingleUser(userId).stream()
                .map(sickNoteDTOMapper).toList();

        return ResponseEntity.ok(sickNotes);

    }

    /**
     * Creates a new sick note for a single user. This endpoint requires the user
     * to either have the {@link Permission} "CREATE_SICK_NOTE_SELF" or
     * "CREATE_SICK_NOTE_GENERAL" for the request to be authorized.
     *
     * @param userId   The unique identifier (Long) for the user to which the
     *                 sick note belongs.
     * @param sickNote The {@link SickNote} object containing the information.
     * @return A {@link ResponseEntity} containing the created resource. ALso
     * provides a URI pointing at the created resource's new endpoint. The
     * response body also contains a message for users indicating said status.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @PostMapping(path="/users/{userId}/sick-notes")
    @PreAuthorize(
            "hasAuthority('CREATE_SICK_NOTE_SELF') or hasAuthority('CREATE_SICK_NOTE_GENERAL')"
    )
    public ResponseEntity<GenericValidResponse> createSickNote(
            @PathVariable Long userId, @Valid @RequestBody SickNote sickNote
    ) {

        sickNoteService.createSickNote(sickNote, userId);

        String currUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri().toUriString();
        String baseUri = currUri
                .substring(0, currUri.lastIndexOf("/new-sick-note"));

        URI uri = UriComponentsBuilder.fromUriString(baseUri).build().toUri();

        GenericValidResponse response = new GenericValidResponse(
                "New sick note created successfully."
        );
        return ResponseEntity.created(uri).body(response);

    }

    /**
     * Deletes a sick note based on the ID. This endpoint requires the user to
     * have the {@link Permission} "DELETE_GENERAL" for the request to be
     * authorized.
     *
     * @param sickNoteId The ID of the sick note to be deleted.
     * @return A {@link ResponseEntity} with the code 200 - OK, and a successful
     * message. The response body also contains a message for users indicating
     * said status.
     * @throws ResourceNotFoundException if the sick note is not found.
     */
    @DeleteMapping(path="/sick-notes/{sickNoteId}")
    @PreAuthorize("hasAuthority('DELETE_GENERAL')")
    public ResponseEntity<GenericValidResponse> deleteSickNoteById(
            @PathVariable Long sickNoteId
    ) {

        sickNoteService.deleteSickNote(sickNoteId);

        GenericValidResponse response = new GenericValidResponse(
                "Sick note deleted successfully."
        );
        return ResponseEntity.ok(response);

    }

}

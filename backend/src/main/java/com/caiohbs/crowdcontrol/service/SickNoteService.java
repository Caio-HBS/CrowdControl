package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.SickNote;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.SickNoteRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SickNoteService {

    private final SickNoteRepository sickNoteRepository;
    private final UserRepository userRepository;
    private final UserInfoService userInfoService;

    public SickNoteService(
            UserRepository userRepository, UserInfoService userInfoService, SickNoteRepository sickNoteRepository
    ) {
        this.userRepository = userRepository;
        this.userInfoService = userInfoService;
        this.sickNoteRepository = sickNoteRepository;
    }

    /**
     * Creates a new sick note for a given user though the repository.
     *
     * @param sickNote The {@link SickNoteService} object containing the information to be saved.
     * @param userId   The ID of the user who's sick note is being saved.
     * @throws ResourceNotFoundException If the user with the provided ID is not found.
     */
    public void createSickNote(SickNote sickNote, Long userId) throws ResourceNotFoundException {

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found.");
        }

        SickNote newSickNote = new SickNote(
                // See .convertFileName() documentation for information on how
                // this method works.
                userInfoService.convertFileName(sickNote.getSickNote()),
                sickNote.getSickNoteDate(), user.get()
        );

        sickNoteRepository.save(newSickNote);

    }

    /**
     * Retrieves all the sick notes registered on the database.
     */
    public List<SickNote> retrieveAllSickNotes() {
        return sickNoteRepository.findAll();
    }

    /**
     * Retrieves all the sick notes corresponding to a given user.
     *
     * @param userId The ID of the user you wish to see the sick notes.
     */
    public List<SickNote> retrieveAllSickNotesForSingleUser(Long userId) {
        return sickNoteRepository.findByUserUserId(userId);
    }

    /**
     * Deletes a sick note though it's id.
     *
     * @param sickNoteId The ID of the sick note to be deleted.
     * @throws ResourceNotFoundException If a sick note with the provided ID is not found.
     */
    public void deleteSickNote(Long sickNoteId) throws ResourceNotFoundException {

        try {
            SickNote foundNote = sickNoteRepository.findById(sickNoteId).orElseThrow();
            sickNoteRepository.delete(foundNote);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Sick note not found.");
        }

    }

}

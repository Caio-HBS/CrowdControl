package com.caiohbs.crowdcontrol.service;

import com.caiohbs.crowdcontrol.exception.ResourceNotFoundException;
import com.caiohbs.crowdcontrol.model.SickNote;
import com.caiohbs.crowdcontrol.model.User;
import com.caiohbs.crowdcontrol.repository.SickNoteRepository;
import com.caiohbs.crowdcontrol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class SickNoteServiceTest {

    @Mock
    SickNoteRepository sickNoteRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserInfoService userInfoService;
    @InjectMocks
    SickNoteService sickNoteService;

    private final User newUser = new User("John", "Doe", "test@email.com", "789",
            LocalDate.now().minusYears(18), LocalDate.now(), null, List.of(), List.of(), null);

    private final SickNote newSickNote = new SickNote("test.pdf", LocalDate.now(), newUser);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully create SickNote")
    void createSickNote_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        sickNoteService.createSickNote(newSickNote, 1L);

        verify(userRepository, times(1)).findById(1L);
        verify(sickNoteRepository, times(1)).save(any(SickNote.class));

    }

    @Test
    @DisplayName("Should fail to create SickNote because no User was found")
    void createSickNote_FailedUserNotFound() throws ResourceNotFoundException {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sickNoteService.createSickNote(newSickNote, 1L));

    }

    @Test
    @DisplayName("Should successfully retrieve all SickNotes on DB")
    void retrieveAllSickNotes_Success() {

        SickNote newSickNote_ = new SickNote("test2.pdf", LocalDate.now(), newUser);
        List<SickNote> sickNotes = Arrays.asList(newSickNote, newSickNote_);

        when(sickNoteRepository.findAll()).thenReturn(sickNotes);

        List<SickNote> result = sickNoteService.retrieveAllSickNotes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newSickNote, result.getFirst());
        assertEquals(newSickNote_, result.getLast());

    }

    @Test
    @DisplayName("Should fail to retrieve all SickNotes on DB because there are none")
    void retrieveAllSickNotes_Empty() {

        when(sickNoteRepository.findAll()).thenReturn(List.of());

        List<SickNote> result = sickNoteService.retrieveAllSickNotes();

        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("Should successfully retrieve all SickNotes for single User on DB")
    void retrieveAllSickNotesForSingleUser_Success() {

        SickNote newSickNote_ = new SickNote("test2.pdf", LocalDate.now(), newUser);
        List<SickNote> sickNotes = Arrays.asList(newSickNote, newSickNote_);

        when(sickNoteRepository.findByUserUserId(1L)).thenReturn(sickNotes);

        List<SickNote> result = sickNoteService.retrieveAllSickNotesForSingleUser(1L);

        verify(sickNoteRepository, times(1)).findByUserUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newSickNote, result.getFirst());
        assertEquals(newSickNote_, result.getLast());

    }

    @Test
    @DisplayName("Should fail to retrieve SickNotes for single User on DB because there are none")
    void retrieveAllSickNotesForSingleUser_Empty() {

        when(sickNoteRepository.findByUserUserId(1L)).thenReturn(List.of());

        List<SickNote> result = sickNoteService.retrieveAllSickNotesForSingleUser(1L);

        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("Should successfully delete SickNote")
    void deleteSickNote_Success() {

        when(sickNoteRepository.findById(1L)).thenReturn(Optional.of(newSickNote));

        sickNoteService.deleteSickNote(1L);

        verify(sickNoteRepository, times(1)).findById(1L);
        verify(sickNoteRepository, times(1)).delete(newSickNote);

    }

    @Test
    @DisplayName("Should fail to delete SickNote because it was not found")
    void deleteSickNote_FailedSickNoteNotFound() {

        when(sickNoteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sickNoteService.deleteSickNote(1L));

    }

}
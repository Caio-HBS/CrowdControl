package com.caiohbs.crowdcontrol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Entity
public class SickNote {

    @Id
    @GeneratedValue
    private Long sickNoteId;
    @NotNull
    private String sickNote;
    @NotNull
    @PastOrPresent(message="sickNoteDate has to be a past or present date.")
    private LocalDate sickNoteDate;
    @ManyToOne(fetch=FetchType.EAGER)
    @JsonIgnore
    private User user;

    public SickNote() {
    }

    public SickNote(String sickNote, LocalDate sickNoteDate, User user) {
        this.sickNote = sickNote;
        this.sickNoteDate = sickNoteDate;
        this.user = user;
    }

    public Long getSickNoteId() {
        return sickNoteId;
    }

    public void setSickNoteId(Long sickNoteId) {
        this.sickNoteId = sickNoteId;
    }

    public @NotNull String getSickNote() {
        return sickNote;
    }

    public void setSickNote(@NotNull String sickNote) {
        this.sickNote = sickNote;
    }

    public LocalDate getSickNoteDate() {
        return sickNoteDate;
    }

    public void setSickNoteDate(LocalDate sickNoteDate) {
        this.sickNoteDate = sickNoteDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SickNote{" +
               "sickNoteId=" + sickNoteId +
               ", sickNote='" + sickNote + '\'' +
               ", sickNoteDate=" + sickNoteDate +
               ", user=" + user +
               '}';
    }

}

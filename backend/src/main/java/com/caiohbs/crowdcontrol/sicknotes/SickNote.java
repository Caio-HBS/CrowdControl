package com.caiohbs.crowdcontrol.sicknotes;

import com.caiohbs.crowdcontrol.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class SickNote {

    @Id
    @GeneratedValue
    private long sickNoteId;
    @NotNull
    @Size(min=10, max=100)
    private String sickNoteTitle;
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    private User user;

    public SickNote() {
    }

    public SickNote(long sickNoteId, User user, String sickNoteTitle) {
        this.sickNoteId = sickNoteId;
        this.user = user;
        this.sickNoteTitle = sickNoteTitle;
    }

    public long getSickNoteId() {
        return sickNoteId;
    }

    public void setSickNoteId(long sickNoteId) {
        this.sickNoteId = sickNoteId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNoteNoteTitle() {
        return sickNoteTitle;
    }

    public void setNoteNoteTitle(String sickNoteTitle) {
        this.sickNoteTitle = sickNoteTitle;
    }

    @Override
    public String toString() {
        return "SickNote{" +
               "sickNoteId=" + sickNoteId +
               ", user=" + user +
               ", noteNoteTitle='" + sickNoteTitle + '\'' +
               '}';
    }

}

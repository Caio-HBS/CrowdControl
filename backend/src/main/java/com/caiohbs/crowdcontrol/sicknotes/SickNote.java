package com.caiohbs.crowdcontrol.sicknotes;

import com.caiohbs.crowdcontrol.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class SickNote {

    @Id
    @GeneratedValue
    private long sickNoteId;
    @OneToOne
    private User user;
    private String noteNoteTitle;

    public SickNote() {
    }

    public SickNote(long sickNoteId, User user, String noteNoteTitle) {
        this.sickNoteId = sickNoteId;
        this.user = user;
        this.noteNoteTitle = noteNoteTitle;
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
        return noteNoteTitle;
    }

    public void setNoteNoteTitle(String noteNoteTitle) {
        this.noteNoteTitle = noteNoteTitle;
    }

    @Override
    public String toString() {
        return "SickNote{" +
               "sickNoteId=" + sickNoteId +
               ", user=" + user +
               ", noteNoteTitle='" + noteNoteTitle + '\'' +
               '}';
    }

}

package com.caiohbs.crowdcontrol.model;

import jakarta.persistence.*;

@Entity
public class EmailCode {

    @Id
    @GeneratedValue
    private Long emailId;
    private String emailCode;
    private boolean isCodeActive;
    @Enumerated(EnumType.STRING)
    private EmailType emailType;
    @ManyToOne
    private User user;

    public EmailCode() {
    }

    public EmailCode(
            String emailCode, boolean isCodeActive, EmailType emailType, User user
    ) {
        this.emailCode = emailCode;
        this.isCodeActive = isCodeActive;
        this.emailType = emailType;
        this.user = user;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {

        try {
            EmailType.valueOf(emailType.name());
            this.emailType = emailType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid email type: " + emailType);
        }

    }

    public boolean isCodeActive() {
        return isCodeActive;
    }

    public void setCodeActive(boolean codeActive) {
        isCodeActive = codeActive;
    }
}

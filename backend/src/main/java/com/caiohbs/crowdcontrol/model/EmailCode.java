package com.caiohbs.crowdcontrol.model;

import jakarta.persistence.*;

@Entity
public class EmailCode {

    @Id
    @GeneratedValue
    private Long emailId;
    private String emailCode;
    @Enumerated(EnumType.STRING)
    private EmailType emailType;
    @ManyToOne
    private User user;

    public EmailCode() {
    }

    public EmailCode(String emailCode, EmailType emailType, User user) {
        this.emailCode = emailCode;
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

}

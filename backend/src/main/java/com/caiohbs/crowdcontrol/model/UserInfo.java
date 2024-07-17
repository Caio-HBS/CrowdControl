package com.caiohbs.crowdcontrol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="user_info")
public class UserInfo {

    @Id
    @GeneratedValue
    private Long userInfoId;
    @OneToOne(fetch=FetchType.EAGER)
    private User user;
    private String pfp;
    private String pronouns;
    @Size(min=0, max=280, message="field 'bio' has to be between 0 and 280 characters")
    private String bio;
    private String nationality;

    public UserInfo() {
    }

    public UserInfo(
            User user, String pfp, String pronouns, String bio, String nationality
    ) {
        this.user = user;
        this.pfp = pfp;
        this.pronouns = pronouns;
        this.bio = bio;
        this.nationality = nationality;
    }

    public void setId(Long id) {
        this.userInfoId = id;
    }

    public Long getId() {
        return userInfoId;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        List<String> valid = new ArrayList<>(
                Arrays.asList("HE/HIM", "SHE/HER", "THEY/THEM", "ANY", "UNDISCLOSED")
        );

        if (!valid.contains(pronouns)) {
            throw new IllegalArgumentException(
                    "Couldn't find pronouns: " + pronouns
            );
        }

        this.pronouns = pronouns;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

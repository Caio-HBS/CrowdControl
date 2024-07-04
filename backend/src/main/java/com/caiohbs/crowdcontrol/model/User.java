package com.caiohbs.crowdcontrol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name="_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long userId;
    @NotNull(message="field 'firstName' may not be null")
    @Size(min=1, max=50, message="field 'first_name' has to be between 1 and 50 characters")
    private String firstName;
    @NotNull(message="field 'lastName' may not be null")
    @Size(min=1, max=50, message="field 'last_name' has to be between 1 and 50 characters")
    private String lastName;
    @NotNull(message="field 'username' may not be null")
    @Email(message="field 'username' has to be a valid email")
    @Size(min=1, max=100, message="field 'email' has to be between 1 and 100 characters")
    @Column(unique=true)
    private String email;
    @NotNull(message="field 'password' may not be null")
    private String password;
    @NotNull(message="field 'birthDate' may not be null")
    @Past(message="field 'birthDate' has to be a past date")
    private LocalDate birthDate;
    @JsonIgnore
    @CreatedDate
    private LocalDate registerDate;
    @OneToMany(mappedBy="user", fetch=FetchType.EAGER)
    @JsonIgnore
    private List<Payment> payments;
    @OneToMany(mappedBy="user", fetch=FetchType.EAGER)
    @JsonIgnore
    private List<SickNote> sickNotes;
    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    private Role role;
    private boolean isEnabled;
    private boolean isAccountNonLocked;

    public User() {
    }

    public User(
            Long userId, String firstName, String lastName, String email,
            String password, LocalDate birthDate, LocalDate localDate,
            List<Payment> payments, List<SickNote> sickNotes, Role role
    ) {


        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = encryptPass(password);
        this.birthDate = birthDate;
        this.registerDate = localDate;
        this.payments = payments;
        this.sickNotes = sickNotes;
        this.role = role;
        this.isEnabled = false;
        this.isAccountNonLocked = true;
    }

    public String encryptPass(String password) {

        BCryptPasswordEncoder encrypt = new BCryptPasswordEncoder();
        return encrypt.encode(password);

    }

    @PrePersist
    public void prePersist() {
        this.registerDate = LocalDate.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }

        List<String> permissionsList = role.getPermissions();
        List<GrantedAuthority> authorities = new ArrayList<>(List.of());

        for (String permission : permissionsList) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = encryptPass(password);
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setUsername(String username) {
        this.email = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<SickNote> getSickNotes() {
        return sickNotes;
    }

    public void setSickNotes(List<SickNote> sickNotes) {
        this.sickNotes = sickNotes;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setIsAccountNonLocked(boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", birthDate=" + birthDate +
               ", registerDate=" + registerDate +
               ", payments=" + payments +
               ", sickNotes=" + sickNotes +
               ", role=" + role +
               '}';
    }
}

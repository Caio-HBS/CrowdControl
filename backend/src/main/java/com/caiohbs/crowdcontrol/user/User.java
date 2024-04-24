package com.caiohbs.crowdcontrol.user;

import com.caiohbs.crowdcontrol.roles.Permission;
import com.caiohbs.crowdcontrol.roles.Role;
import com.caiohbs.crowdcontrol.sicknotes.SickNote;
import com.caiohbs.crowdcontrol.payments.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private String email;
    @NotNull(message="field 'password' may not be null")
    @Size(min=1, max=20, message="field 'password' has to be between 1 and 20 characters")
    private String password;
    @NotNull(message="field 'birthDate' may not be null")
    @Past(message="field 'birthDate' has to be a past date")
    private LocalDate birthDate;
    @NotNull(message="field 'registerDate' may not be null")
    @FutureOrPresent(message="field 'registerDate' has to be the present date")
    private LocalDate registerDate;
    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Payment> payments;
    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<SickNote> sickNotes;
    @ManyToOne
    private Role role;

    public User() {
    }

    public User(
            Long userId, String firstName, String lastName, String email,
            String password, LocalDate birthDate, LocalDate registerDate,
            List<Payment> payments, List<SickNote> sickNotes, Role role
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.registerDate = registerDate;
        this.payments = payments;
        this.sickNotes = sickNotes;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        this.password = password;
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
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", birthDate=" + birthDate +
               ", registerDate=" + registerDate +
               ", payments=" + payments +
               ", sickNotes=" + sickNotes +
               ", role=" + role +
               '}';
    }
}

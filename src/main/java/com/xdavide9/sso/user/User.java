package com.xdavide9.sso.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.role.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is a User model, entity and custom implementation of {@link UserDetails} at the same time.
 * There is a no args constructor for Jackson and JPA and a constructor with the fields that must be provided when registering an account.
 * Username, email and password must be provided when registering an account.
 * {@link UUID} is the primary key, is immutable and auto-generated for each user.
 * Phone number, country, first name, last name, date of birth and gender can be provided as additional information.
 * Role, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled have default values and are handled by the system.
 * Authorities are derived by the {@link Role} and therefore cannot be set
 * arbitrarily (no setter).
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "sso_user")
public class User implements UserDetails {
    /**
     * It is used as primary key. It is immutable and always server generated.
     * These qualities make it the best candidate for PUT controller methods for users
     * (used to modify Users)
     */
    @Id
    private final UUID uuid = UUID.randomUUID();
    /**
     * It is used to authenticate (can also use email)
     */
    @Column(nullable = false, unique = true)
    private String username;

    // TODO create email authenticator that sends a verification email (twilio)
    /**
     * It is used to authenticate (can also use username)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * It is used to authenticate
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    // TODO implement sending of a confirm sms
    @Column(unique = true)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "country_code")
    private Country country;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * Defines permissions for the user
     * @see Role
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    /**
     * accountNonExpired always set to true
     */
    @Column(nullable = false)
    private boolean accountNonExpired = true;
    /**
     * accountNonLocked set to false when there are too many
     * failed login attempts
     */
    @Column(nullable = false)
    private boolean accountNonLocked = true;    // TODO set to false to prevent spamming login attempts
    /**
     * credentialsNonExpired set to false when too much time has passed
     * since the last password change
     */
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;   // TODO set to false to require a password change
    /**
     * enabled set to true unless the user is banned or timed out
     */
    @Column(nullable = false)
    private boolean enabled = true;

    // Object creation

    /**
     * Public constructor to be used to create user objects,
     * mostly in tests, and by JPA
     */
    public User() {
    }

    /**
     * Constructor used in the signup process with the bare minimum because
     * username, email and password must be provided when trying to register an account
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // GETTERS

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Authorities are created from {@link Role} and cannot be set arbitrarily
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public Country getCountry() {
        return country;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // SETTERS

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * raw password is encoded after validation to be stored in database
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * It is used by the system to expire an account
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * It is used by the system to lock an account
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * It is used by the system to expire credentials
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * It is used to time out or ban a user
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // EQUALS, HASHCODE, TOSTRING

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", country=" + country +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", role=" + role +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}

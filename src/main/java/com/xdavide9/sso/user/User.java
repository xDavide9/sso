package com.xdavide9.sso.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is a User model, entity and custom implementation of {@link UserDetails} at the same time.
 * Object creation is done using the JavaBeans pattern. There is also a constructor that requires username,
 * email and password because these fields must be provided and cannot be null nor blank (when registering a new account).
 * Every other field has a default value that may be overridden (with valid values).
 * Authorities are defined by the {@link Role} and from it, they cannot be set
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
    @NotBlank(message = "Username cannot be blank nor null")
    private String username;

    // TODO create email authenticator that sends a verification email
    /**
     * It is used to authenticate (can also use username)
     */
    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank nor null")
    private String email;

    /**
     * It is used to authenticate
     */
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank nor null")
    @JsonIgnore
    private String password;
    // TODO implement a phone number validator
    /**
     * phoneNumber
     * @since 0.0.1-SNAPSHOT
     */
    private String phoneNumber;

    /**
     * Defines permissions for the user
     * @see Role
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role = Role.USER;
    /**
     * accountNonExpired always set to true
     */
    private boolean accountNonExpired = true;
    /**
     * accountNonLocked set to false when there are too many
     * failed login attempts
     */
    private boolean accountNonLocked = true;    // TODO set to false to prevent spamming login attempts
    /**
     * credentialsNonExpired set to false when too much time has passed
     * since the last password change
     */
    private boolean credentialsNonExpired = true;   // TODO set to false to require a password change
    /**
     * enabled set to true unless an Admin or Operator timed the account out
     */
    private boolean enabled = true;

    // Object creation

    /**
     * Public constructor to be used to create user objects (set fields via setters following javaBeans pattern),
     * mostly in tests, and by JPA
     */
    public User() {
    }

    /**
     * constructor used in the signup process because
     * username, email and password must be provided when trying to register an account
     * @param username username input by user
     * @param email email input by user
     * @param password password input by user
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

    /**
     * It is used by anyone to change their username
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * It is used by anyone to change their password
     * @param password new password (raw)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * It is used by anyone to change their email
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * It is used by anyone to change their phone number
     * @param phoneNumber new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * It is used by people with high authority to change authorities of someone
     * @param role new role
     * @see Role
     */
    public void setRole(Role role) {
        this.role = role;
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
     * It is used by an Operator or Admin to time out an account
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
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}

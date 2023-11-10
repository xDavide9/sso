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
 * email and password because these fields must be provided and cannot be null nor blank.
 * Every other field has a default value that may be overridden (with valid values).
 * The uuid cannot be changed.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */

@Entity
@Table(name = "sso_user")
public class User implements UserDetails {
    /**
     * It is used a primary key
     * @since 0.0.1-SNAPSHOT
     */
    @Id
    private final UUID uuid = UUID.randomUUID();
    /**
     * It is used to authenticate (can also use email)
     * @since 0.0.1-SNAPSHOT
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be blank nor null")
    private String username;

    // TODO create email authenticator that sends a verification email
    /**
     * It is used to authenticate (can also use username)
     * @since 0.0.1-SNAPSHOT
     */
    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank nor null")
    private String email;

    /**
     * It is used to authenticate
     * @since 0.0.1-SNAPSHOT
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
     * constructor used in the signup process because
     * username, email and password must be provided when trying to register an account
     * @param username username
     * @param email email
     * @param password password
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    /**
     * Defines permissions for the user
     * @see Role
     * @since 0.0.1-SNAPSHOT
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role = Role.USER;
    /**
     * accountNonExpired always set to true
     * @since 0.0.1-SNAPSHOT
     */
    private boolean accountNonExpired = true;
    /**
     * accountNonLocked set to false when there are too many
     * failed login attempts
     * @since 0.0.1-SNAPSHOT
     */
    private boolean accountNonLocked = true;    // TODO set to false to prevent spamming login attempts
    /**
     * credentialsNonExpired set to false when too much time has passed
     * since the last password change
     * @since 0.0.1-SNAPSHOT
     */
    private boolean credentialsNonExpired = true;   // TODO set to false to require a password change
    /**
     * enabled set to true unless an Admin or Operator timed the account out
     * @since 0.0.1-SNAPSHOT
     */
    private boolean enabled = true; // TODO set to false to time out someone

    /**
     * Public constructor to be used to create user objects (set fields via setters following javaBeans pattern)
     * and by JPA
     * @since 0.0.1-SNAPSHOT
     */
    public User() {
    }

    // GETTERS

    /**
     * It is a simple getter
     * @since 0.0.1-SNAPSHOT
     * @return uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * It is a simple getter
     * @since 0.0.1-SNAPSHOT
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * It is a simple getter
     * @since 0.0.1-SNAPSHOT
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * It is a simple getter
     * @since 0.0.1-SNAPSHOT
     * @return role
     */
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
     * @since 0.0.1-SNAPSHOT
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * It is used by anyone to change their password
     * @since 0.0.1-SNAPSHOT
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * It is used by anyone to change their email
     * @since 0.0.1-SNAPSHOT
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * It is used by anyone to change their phone number
     * @since 0.0.1-SNAPSHOT
     * @param phoneNumber phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * It is used by Admins to promote Users to Operators
     * @since 0.0.1-SNAPSHOT
     * @param role role
     * @see Role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * It is used by the system to expire an account
     * @since 0.0.1-SNAPSHOT
     * @param accountNonExpired accountNonExpired
     */
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * It is used by the system to lock an account
     * @since 0.0.1-SNAPSHOT
     * @param accountNonLocked accountNonLocked
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * It is used by the system to expire credentials
     * @since 0.0.1-SNAPSHOT
     * @param credentialsNonExpired credentialsNonExpired
     */
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * It is used by an Operator or Admin to time out an account
     * @since 0.0.1-SNAPSHOT
     * @param enabled enabled
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

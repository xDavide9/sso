package com.xdavide9.sso.user;

import java.util.Objects;
import java.util.UUID;

/**
 * This class is a data-transfer-object for the {@link User} class.
 * It contains all the fields of the User class that are meant to be exposed by Apis.
 * The password and authorities are the only fields excluded as security is handled internally
 * by the server and there is no reason to share them. An instance of this class
 * can be created in different ways: no args constructor + setters, all args constructors,
 * by passing a user instance.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class UserDTO {
    private UUID uuid;
    private String username, email, phoneNumber;
    private Role role;
    private boolean enabled, accountNonLocked, accountNonExpired, credentialsNonExpired;

    // Object creation

    public UserDTO(UUID uuid,
                   String username,
                   String email,
                   String phoneNumber,
                   Role role,
                   boolean enabled,
                   boolean accountNonLocked,
                   boolean accountNonExpired,
                   boolean credentialsNonExpired) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public UserDTO() {}

    /**
     * Creates and instance of {@link UserDTO} from a given {@link User}.
     * All the user fields, except for password and authorities, are copied.
     */
    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired()
        );
    }

    // Getters and Setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return enabled == userDTO.enabled && accountNonLocked == userDTO.accountNonLocked && accountNonExpired == userDTO.accountNonExpired && credentialsNonExpired == userDTO.credentialsNonExpired && Objects.equals(uuid, userDTO.uuid) && Objects.equals(username, userDTO.username) && Objects.equals(email, userDTO.email) && Objects.equals(phoneNumber, userDTO.phoneNumber) && Objects.equals(role, userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username, email, phoneNumber, role, enabled, accountNonLocked, accountNonExpired, credentialsNonExpired);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", accountNonLocked=" + accountNonLocked +
                ", accountNonExpired=" + accountNonExpired +
                ", credentialsNonExpired=" + credentialsNonExpired +
                '}';
    }
}

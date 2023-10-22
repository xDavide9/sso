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
 * Object creation is done using the JavaBeans pattern. The fields username, email and password must be provided
 * and cannot be null nor blank. Every other field has a default value that may be overridden (with valid values).
 * The uuid is cannot be changed.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */

@Entity
@Table(name = "sso_user")
public class User implements UserDetails {
    @Id
    private final UUID uuid = UUID.randomUUID();
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be blank nor null")
    private String username;
    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank nor null")
    @JsonIgnore
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role = Role.USER;
    private boolean accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true;

    public User() {
    }

    // GETTERS
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

    public void setUsername(String username) {
        this.username = username;
    }

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

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

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

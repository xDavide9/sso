package com.xdavide9.sso.user;


import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is a User model, entity and custom implementation of {@link UserDetails} at the same time.
 * You can only create objects of this class using the builder pattern; do NOT use the no args constructor required by JPA.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */

@Entity
@Table(name = "sso_user")
public class User implements UserDetails {
    @Id
    private UUID uuid;
    @Column(nullable = false, unique = true)
    private String username, email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean accountNonExpired, accountNonLocked, credentialsNonExpired, enabled;

    public User() {
    }

    private User(Builder builder) {
        this.uuid = builder.uuid;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.role = builder.role;
        this.accountNonExpired = builder.isAccountNonExpired;
        this.accountNonLocked = builder.isAccountNonLocked;
        this.credentialsNonExpired = builder.isCredentialsNonExpired;
        this.enabled = builder.isEnabled;
    }

    public static class Builder {

        private UUID uuid = UUID.randomUUID();
        private String username, password, email;
        private Role role = Role.USER;
        private boolean isAccountNonExpired = true,
                isAccountNonLocked = true,
                isCredentialsNonExpired = true,
                isEnabled = true;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder accountNonExpired(boolean isAccountNonExpired) {
            this.isAccountNonExpired = isAccountNonExpired;
            return this;
        }

        public Builder accountNonLocked(boolean isAccountNonLocked) {
            this.isAccountNonLocked = isAccountNonLocked;
            return this;
        }

        public Builder credentialsNonExpired(boolean isCredentialsNonExpired) {
            this.isCredentialsNonExpired = isCredentialsNonExpired;
            return this;
        }

        public Builder enabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO implement custom permissions associated with role
        return List.of(new SimpleGrantedAuthority(String.format("ROLE_%s", role)));
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return accountNonExpired == user.accountNonExpired && accountNonLocked == user.accountNonLocked && credentialsNonExpired == user.credentialsNonExpired && enabled == user.enabled && Objects.equals(uuid, user.uuid) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username, password, email, role, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}

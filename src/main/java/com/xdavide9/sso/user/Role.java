package com.xdavide9.sso.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xdavide9.sso.user.Permission.*;

/**
 * This enum represents roles to which {@link Permission}s are attached to create
 * authorities for {@link User}s.
 * There are three level of authorisation:
 * 1) USER: has permission to only manage their personal details
 * and information within the application they are using (get, put).
 * 2) OPERATOR: has User privileges and also permission to manage other users information
 * with a limited degree of freedom (get, put).
 * 3) ADMIN: has User and Operator privileges as well as any other permission in the system
 * including destructive operations (get, put, delete).
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public enum Role {
    USER(Set.of(
            USER_GET,
            USER_PUT
    )),
    OPERATOR(Set.of(
            USER_GET,
            USER_PUT,
            OPERATOR_GET,
            OPERATOR_PUT
    )),
    ADMIN(Set.of(
            USER_GET,
            USER_PUT,
            OPERATOR_GET,
            OPERATOR_PUT,
            ADMIN_GET,
            ADMIN_PUT,
            ADMIN_DELETE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", this.name())));
        return authorities;
    }
}

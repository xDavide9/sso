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
    /**
     * role
     * @since 0.0.1-SNAPSHOT
     */
    USER(Set.of(
            USER_GET,
            USER_PUT
    )),
    /**
     * role
     * @since 0.0.1-SNAPSHOT
     */
    OPERATOR(Set.of(
            USER_GET,
            USER_PUT,
            OPERATOR_GET,
            OPERATOR_PUT
    )),
    /**
     * role
     * @since 0.0.1-SNAPSHOT
     */
    ADMIN(Set.of(
            USER_GET,
            USER_PUT,
            OPERATOR_GET,
            OPERATOR_PUT,
            ADMIN_GET,
            ADMIN_PUT,
            ADMIN_DELETE
    ));

    /**
     * permissions
     * @since 0.0.1-SNAPSHOT
     */
    private final Set<Permission> permissions;

    /**
     * constructor for a role based on permissions
     * @param permissions permissions
     * @since 0.0.1-SNAPSHOT
     */
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * getter
     * @return permissions
     * @since 0.0.1-SNAPSHOT
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * constructs authorities from permissions associated with role
     * @since 0.0.1-SNAPSHOT
     * @return list of authorities for each role
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", this.name())));
        return authorities;
    }
}

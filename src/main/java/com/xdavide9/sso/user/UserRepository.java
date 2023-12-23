package com.xdavide9.sso.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * This repository connects to a PostgreSQL database named "sso_x" where there is a
 * "sso_user" table within the public schema. There are different connections depending
 * on Spring profile used "sso_dev", "sso_test", "sso_prod".
 * It makes use of the {@link User} entity.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /**
     * Finds by subject (either username or email);
     * theoretically both should be passed to method, but the same string is passed
     * twice because there is no way to know if the user will log in by username or email
     * @param s1 subject
     * @param s2 subject
     * @return Optional of User with matching username or email
     * @since 0.0.1-SNAPSHOT
     */
    Optional<User> findByUsernameOrEmail(String s1, String s2);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}

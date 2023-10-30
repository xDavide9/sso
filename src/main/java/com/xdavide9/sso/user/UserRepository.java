package com.xdavide9.sso.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * This repository connects to a PostgreSQL database named "sso" where there is a
 * "sso_user" table within the public schema. It makes use of the {@link User} entity.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * finds by username
     * @param username username
     * @return Optional of User with matching username
     * @since 0.0.1-SNAPSHOT
     */
    Optional<User> findByUsername(String username);

    /**
     * finds by Email
     * @param email email
     * @return Optional of User with matching email
     * @since 0.0.1-SNAPSHOT
     */
    Optional<User> findByEmail(String email);

}

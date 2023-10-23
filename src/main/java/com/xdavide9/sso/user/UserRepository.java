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
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String username);

}

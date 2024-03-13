package com.xdavide9.sso.user.change;

import com.xdavide9.sso.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository connects to a PostgreSQL database named "sso_x" where there is a
 * "sso_user_change" table within the public schema. There are different connections depending
 * on Spring profile used "sso_dev", "sso_test", "sso_prod".
 * It makes use of the {@link UserChange} entity.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface UserChangeRepository extends JpaRepository<UserChange, Long> {
    List<UserChange> findAllByUserIs(User user);
}

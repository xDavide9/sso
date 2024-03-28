package com.xdavide9.sso.user.fields.country;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository connects to a PostgreSQL database named "sso_x" where there is a
 * "sso_country" table within the public schema. There are different connections depending
 * on Spring profile used "sso_dev", "sso_test", "sso_prod".
 * It makes use of the {@link Country} entity.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
}

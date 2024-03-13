package com.xdavide9.sso.common.config;

import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import com.xdavide9.sso.user.fields.role.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * This class holds configuration for the testing database. This testing environment is active
 * when the spring profile "test" is used. A {@link CommandLineRunner} bean is registered if this is the case
 * and saves some custom {@link User}s to the database.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@TestConfiguration
public class TestingDatabaseConfig {

    // TODO update this to save users with more details for more in depth teseting

    /**
     * Jpa repository to interact with the database.
     */
    private final UserRepository userRepository;
    /**
     * It is a {@link PasswordEncoder} implementation defined in {@link SecurityConfig}.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * Logger from slf4j interface.
     */
    private static final Logger log = LoggerFactory.getLogger(TestingDatabaseConfig.class);

    @Autowired
    public TestingDatabaseConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Command line runner bean that is registered only when the "test" profile is active. It
     * saves 1 admin, 1 operator and 1 plain user to the testing database to ease the process.
     * @return the custom command line runner bean described
     */
    @Bean
    @Profile({"test"})
    CommandLineRunner setUpTestingDatabase() {
        return args -> {
            User user = new User(
                    "userUsername",
                    "user@email.com",
                    passwordEncoder.encode("userPassword")
            );
            User operator = new User(
                    "operatorUsername",
                    "operator@email.com",
                    passwordEncoder.encode("operatorPassword")
            );
            User admin = new User(
                    "adminUsername",
                    "admin@email.com",
                    passwordEncoder.encode("adminPassword")
            );
            operator.setRole(Role.OPERATOR);
            admin.setRole(Role.ADMIN);
            userRepository.saveAll(List.of(user, operator, admin));
            log.info("Testing profile active - Saving 1 admin, 1 operator, 1 plain user to testing database");
        };
    }
}

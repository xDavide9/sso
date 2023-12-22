package com.xdavide9.sso.config;

import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class TestingDatabaseConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(TestingDatabaseConfig.class);

    @Autowired
    public TestingDatabaseConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Profile("test")
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

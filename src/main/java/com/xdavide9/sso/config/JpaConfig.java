package com.xdavide9.sso.config;

import com.xdavide9.sso.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * This class contains configuration related to JPA.
 * Auditing is enabled and the auditorAware is provided.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();
            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    authentication instanceof AnonymousAuthenticationToken
            ) return Optional.empty();
            User user = (User) authentication.getPrincipal();
            return Optional.ofNullable(user.getUuid());
        };
    }
}

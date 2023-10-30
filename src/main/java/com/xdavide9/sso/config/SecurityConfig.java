package com.xdavide9.sso.config;

import com.xdavide9.sso.properties.App;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static java.lang.String.format;
/**
 * This class provides essential security beans such as SecurityFilterChain, passwordEncoder,
 * DaoAuthenticationProvider, userDetailsService.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final App app;
    private final UserRepository repository;

    /**
     * constructor
     * @param repository user repository
     * @param app application.properties' properties with prefix app
     * @since 0.0.1-SNAPSHOT
     */
    @Autowired
    public SecurityConfig(UserRepository repository, App app) {
        this.repository = repository;
        this.app = app;
    }

    /**
     * constructs custom security filter chain
     * where web security matchers are configured as well as other important security
     * configuration
     * @since 0.0.1-SNAPSHOT
     * @param http http
     * @return custom security filter chain
     * @throws Exception any
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String version = app.getVersion();
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(
                                        format("/api/v%s/operator/users", version),
                                        format("/api/v%s/operator/users/uuid/**", version),
                                        format("/api/v%s/operator/users/username/**", version),
                                        format("/api/v%s/operator/users/email/**", version)
                                )
                                .hasAuthority("OPERATOR_GET")
                        ).build();
    }

    /**
     * provides the default BCryptPasswordEncoder implementation
     * @return BcryptPasswordEncoder
     * @since 0.0.1-SNAPSHOT
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO provide a custom query that finds either by username or email

    /**
     * provides a custom userDetailsService
     * which uses {@link UserRepository} to return {@link com.xdavide9.sso.user.User} entities
     * @return custom userDetailsService
     * @since 0.0.1-SNAPSHOT
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found."));
    }

    /**
     * provides a {@link DaoAuthenticationProvider} implementation
     * with set passwordEncoder and userDetailsService
     * @since 0.0.1-SNAPSHOT
     * @return daoAuthenticationProvider implementation
     * @since 0.0.1-SNAPSHOT
     */
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }
}

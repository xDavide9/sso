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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final App app;
    private final UserRepository repository;

    @Autowired
    public SecurityConfig(UserRepository repository, App app) {
        this.repository = repository;
        this.app = app;
    }

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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO provide a custom query that finds either by username or email
    @Bean
    UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found."));
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }
}

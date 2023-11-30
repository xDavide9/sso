package com.xdavide9.sso.config;

import com.xdavide9.sso.jwt.JwtAuthenticationFilter;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.authentication.RepositoryUserDetailsService;
import com.xdavide9.sso.user.Role;
import com.xdavide9.sso.user.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    /**
     * implemented by {@link RepositoryUserDetailsService}
     * @since 0.0.1-SNAPSHOT
     */
    private final UserDetailsService userDetailsService;
    /**
     * It is a custom-made filter to handle jwt token.
     * Operates strictly with {@link JwtService}
     * @since 0.0.1-SNAPSHOT
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // TODO implement logout handler

    /**
     * Heart of the security configuration.
     * Disables csrf (cross-site-request-forgery) protection as it is not needed
     * in a completely stateless api that uses jwt token to authenticate.
     * Protects controller endpoints with the appropriate {@link Permission}s and {@link Role}s
     * by providing requestMatchers. Note that each method is also protected with method security in its controller.
     * Two special endpoints that require no authentication are also configured to allow any incoming request (/signup, /login).
     * The {@link JwtAuthenticationFilter} is added.
     * Sessions are configured to be completely stateless.
     * An appropriate {@link DaoAuthenticationProvider} is configured to communicate with the database (below in this class)
     * @since 0.0.1-SNAPSHOT
     * @param http http
     * @return custom security filter chain
     * @throws Exception any
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/v0.0.1/auth/**")
                                .permitAll()
                                .requestMatchers(
                                        "/api/v0.0.1/users",
                                        "/api/v0.0.1/users/uuid/**",
                                        "/api/v0.0.1/users/username/**",
                                        "/api/v0.0.1/users/email/**"
                                )
                                .hasAnyAuthority("OPERATOR_GET", "ADMIN_GET")
                        )
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
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
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}

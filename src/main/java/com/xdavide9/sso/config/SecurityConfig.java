package com.xdavide9.sso.config;

import com.xdavide9.sso.authentication.RepositoryUserDetailsService;
import com.xdavide9.sso.jwt.JwtAuthenticationFilter;
import com.xdavide9.sso.jwt.JwtService;
import com.xdavide9.sso.jwt.JwtTokenValidatorFilter;
import com.xdavide9.sso.user.fields.role.Permission;
import com.xdavide9.sso.user.fields.role.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class provides essential security beans such as SecurityFilterChain, passwordEncoder,
 * DaoAuthenticationProvider, userDetailsService, accessDeniedHandler
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * It is implemented by {@link RepositoryUserDetailsService}.
     */
    private final UserDetailsService userDetailsService;
    /**
     * It is a custom-made filter that validates the token sent with http requests.
     * Depends on the methods provided by {@link JwtService}.
     */
    private final JwtTokenValidatorFilter jwtTokenValidatorFilter;

    /**
     * It is a custom-made filter that registers the user
     * contained in the validated jwt token in the security context
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtTokenValidatorFilter jwtTokenValidatorFilter, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenValidatorFilter = jwtTokenValidatorFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Heart of the security configuration.
     * Disables csrf (cross-site-request-forgery) protection as it is not needed
     * in a completely stateless api that uses jwt token to authenticate.
     * Protects controller endpoints with the appropriate {@link Permission}s and {@link Role}s
     * by providing requestMatchers. Note that each method is also protected with method security in its controller.
     * Two special endpoints that require no authentication are also configured to allow any incoming request (/signup, /login).
     * The {@link JwtTokenValidatorFilter} and {@link com.xdavide9.sso.jwt.JwtAuthenticationFilter} are added
     * Sessions are configured to be completely stateless.
     * An appropriate {@link DaoAuthenticationProvider} is configured to communicate with the database.
     * @param http {@link HttpSecurity} object to manipulate configuration
     * @return The custom security filter chain bean described
     * @throws Exception any exception
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/v0.0.1/auth/*")
                                .permitAll()
                                .requestMatchers(
                                        "/api/v0.0.1/users",
                                        "/api/v0.0.1/users/uuid/*",
                                        "/api/v0.0.1/users/username/*",
                                        "/api/v0.0.1/users/email/*"
                                )
                                .hasAnyAuthority("OPERATOR_GET", "ADMIN_GET")
                                .requestMatchers(
                                        "/api/v0.0.1/users/timeout/*",
                                        "/api/v0.0.1/users/change/username/*",
                                        "/api/v0.0.1/users/change/email/*"
                                )
                                .hasAnyAuthority("OPERATOR_PUT", "ADMIN_PUT")
                                .requestMatchers(
                                        "/api/v0.0.1/users/promote/*",
                                        "/api/v0.0.1/users/demote/*",
                                        "/api/v0.0.1/users/unban/*"
                                )
                                .hasAuthority("ADMIN_PUT")
                                .requestMatchers(
                                        "/api/v0.0.1/users/ban/*"
                                )
                                .hasAuthority("ADMIN_DELETE")
                        )
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(configurer -> configurer.accessDeniedHandler(accessDeniedHandler()))
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenValidatorFilter, JwtAuthenticationFilter.class)
                .build();
    }

    /**
     * Provides the default {@link BCryptPasswordEncoder} implementation.
     * @return The default {@link BCryptPasswordEncoder}
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a {@link DaoAuthenticationProvider} implementation
     * with set passwordEncoder and userDetailsService.
     * @return the custom daoAuthenticationProvider bean described
     */
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * Allows to modify the response sent back to client when a request is
     * sent with authorization but is not enough to access the requested resource.
     */
    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException e) -> {
            response.setStatus(403); // Forbidden
            response.getWriter().write("Access Denied. You do not have enough authorization to access the request resource.");
        };
    }
}

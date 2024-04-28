package com.xdavide9.sso.jwt;

import com.xdavide9.sso.authentication.verification.EmailVerifier;
import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

import static java.lang.String.format;

/**
 * This class is the second filter that handles the process of authentication with jwt.
 * Its responsibility is to check that the account associated with
 * a valid jwt token is eligible to be registered to the security context.
 * This process is done thanks to specific boolean fields (e.g. enabled)
 * defined in {@link User}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see SecurityConfig
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final Clock clock;
    private final UserRepository repository;
    private final EmailVerifier emailVerifier;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService,
                                   JwtService jwtService,
                                   Clock clock,
                                   UserRepository repository,
                                   EmailVerifier emailVerifier) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.clock = clock;
        this.repository = repository;
        this.emailVerifier = emailVerifier;
    }

    /**
     * The filter simply registers the user with corresponding username contained in the token
     * to the security context.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // do not process the filter if request is about authentication (login/signup)
        if (request.getRequestURI().contains("api/v0.0.1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = (String) request.getAttribute("username");
        String token = (String) request.getAttribute("token");
        if (username != null && token != null && securityContext().getAuthentication() == null) {
            User user = (User) userDetailsService.loadUserByUsername(username);
            if (!jwtService.isTokenValid(token, user)) {
                response.setStatus(403);
                response.getWriter()
                        .write("Invalid Jwt token. Login again.");
                return;
            }
            // check enabled, accountNonLocked, accountExpired, credentialsNonExpired
            if (!user.isEnabled()) {
                if (user.getDisabledUntil() != null && LocalDateTime.now(clock).isBefore(user.getDisabledUntil())) {
                    response.setStatus(403);
                    response.getWriter().write(format("This account [%s] has been disabled by an Admin.", user.getUuid()));
                    return;
                } else {
                    user.setEnabled(true);
                    repository.save(user);
                }
            }
            if (!emailVerifier.isEmailVerified(user)) {
                response.setStatus(403);
                response.getWriter()
                        .write("You need to verify your email before proceeding, check your inbox or request a new verification email");
                return;
            }
            // ...
            // now register to security context
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
            authToken.setDetails(
                    new WebAuthenticationDetails(request)
            );
            securityContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Wrapping security context holder for testability (a static utility cannot be mocked).
     * @return {@link SecurityContext} object that is not static
     */
    protected SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }
}

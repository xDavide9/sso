package com.xdavide9.sso.jwt;

import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.user.User;
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

/**
 * This class is the second filter that handles the process of authentication with jwt.
 * Its responsibility is to only register the user with corresponding username contained in the token
 * to the security context. No checks are needed because everything has already been validated by
 * {@link JwtTokenValidatorFilter}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 * @see SecurityConfig
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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
        final String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        User user = (User) userDetailsService.loadUserByUsername(username);
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

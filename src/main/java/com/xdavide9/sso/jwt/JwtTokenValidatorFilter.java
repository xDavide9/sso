package com.xdavide9.sso.jwt;

import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This class is the first filter that handles the process of authenticating with jwt. The filter is executed only
 * once per request and uses the Authorization header (which is supposed to be sent
 * with every request) to retrieve the jwt token. It then proceeds to validate the token by checking if
 * it's expired. If the token is not expired the username contained in it is set an attribute in the request and passed
 * to {@link JwtAuthenticationFilter}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 * @see SecurityConfig
 */
@Component
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    /**
     * Logger from Slf4j
     */
    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidatorFilter.class);
    /**
     * Service that allows to work with jwt with ease
     */
    private final JwtService jwtService;

    @Autowired
    public JwtTokenValidatorFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * The filter is executed only
     * once per request and uses the Authorization header (which is supposed to be sent
     * with every request) to retrieve the jwt token. It then proceeds to validate the token by checking if
     * it's expired. If the token is not expired the username contained in it is set an attribute in the request and passed
     * to {@link JwtAuthenticationFilter}.
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
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401); // unauthorised
            response.getWriter().write("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
            return;
        }
        final String token = authHeader.substring(7);
        try {
            final String username = jwtService.extractUsername(token);
            request.setAttribute("username", username);
            request.setAttribute("token", token);
        } catch (ExpiredJwtException e) {
            response.setStatus(401); // unauthorised
            response.getWriter()
                    .write("Expired jwt token. Login again to provide a new one.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

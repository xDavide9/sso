package com.xdavide9.sso.jwt;

import com.xdavide9.sso.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * This class is the heart of jwt security configuration. The filter is executed only
 * once per request and uses the Authorization header (which is supposed to be sent
 * with every request) to retrieve the jwt token. It then proceeds to validate the token and register
 * an authentication for the current request only (stateless). If the token is not present an unauthorised code
 * is sent straight back to the client otherwise the FilterChain proceeds with other filters.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 * @see com.xdavide9.sso.config.SecurityConfig
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service used to manage operations with jwt tokens
     * @since 0.0.1-SNAPSHOT
     */
    private final JwtService jwtService;
    /**
     * It is defined in {@link com.xdavide9.sso.config.SecurityConfig}
     * @since 0.0.1-SNAPSHOT
     */
    private final UserDetailsService userDetailsService;

    /**
     * constructor
     * @since 0.0.1-SNAPSHOT
     * @param jwtService jwtService
     * @param userDetailsService userDetailsService
     */
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * The actual filter implementation that processes the tokens
     * sent within http requests.
     * @since 0.0.1-SNAPSHOT
     * @param request client request
     * @param response server response
     * @param filterChain filterChain
     * @throws ServletException exception
     * @throws IOException exception
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Jwt token.");
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt); // can also be the email
        if (username != null && securityContext().getAuthentication() == null) {
            User user = (User) userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, user)) {
                // if token is not valid appropriate exceptions are thrown
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
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Wrapping security context holder for testability (a static utility cannot be mocked)
     * @since 0.0.1-SNAPSHOT
     * @return securityContext
     */
    protected SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }
}

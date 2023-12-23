package com.xdavide9.sso.jwt;

import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.exception.jwt.MissingTokenException;
import com.xdavide9.sso.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.lang.String.format;

/**
 * This class is the heart of jwt security configuration. The filter is executed only
 * once per request and uses the Authorization header (which is supposed to be sent
 * with every request) to retrieve the jwt token. It then proceeds to validate the token and register
 * an authentication for the current request only (stateless). If the token is not present an unauthorised code
 * is sent straight back to the client otherwise the FilterChain proceeds with other filters.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 * @see SecurityConfig
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Logger from Slf4j
     */
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    /**
     * Service that allows to work with jwt with ease
     */
    private final JwtService jwtService;
    /**
     * It is defined in {@link SecurityConfig}
     */
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * The filter that processes the jwt tokens sent inside requests. The tokens are found in
     * the Authorization header as bearer token. If the request is sent to the authentication api
     * the filter is skipped for obvious reasons. If the request does not contain the header a
     * {@link MissingTokenException} is thrown. Finally, if the token is provided and is valid the user is
     * granted authentication for the current request. The token must always be provided
     * in every request as the server is completely stateless.
     * @param request client request
     * @param response server response
     * @param filterChain filterChain
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info(format("Incoming request of type [%s] at [%s]", request.getMethod(), request.getRequestURI()));
        // do not process the filter if request is about authentication
        if (request.getRequestURI().contains("api/v0.0.1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new MissingTokenException(
                    format("Request of type [%s] at [%s] must contain a jwt token", request.getMethod(), request.getRequestURI()
                    ));
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
     * Wrapping security context holder for testability (a static utility cannot be mocked).
     * @return {@link SecurityContext} object that is not static
     */
    protected SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }
}

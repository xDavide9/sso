package com.xdavide9.sso.security;

import com.xdavide9.sso.exception.jwt.JwtSubjectMissMatchException;
import com.xdavide9.sso.properties.JwtProperties;
import com.xdavide9.sso.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This service provides various methods to work with jwt tokens.
 * These operations include issuing, managing and inspecting tokens.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class JwtService {

    /**
     * This object contains fundamental properties to work with jwt tokens
     * such as secret key and expiration time.
     * @since 0.0.1-SNAPSHOT
     */
    private final JwtProperties jwtProperties;

    /**
     * constructor
     * @param jwtProperties jwtProperties
     * @since 0.0.1-SNAPSHOT
     */
    @Autowired
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // extract claims

    /**
     * This method extracts all claims (like json fields) from a token
     * @param token token
     * @return claims as a Claims object
     * @since 0.0.1-SNAPSHOT
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * This method extracts a specific claim from a token
     * @param token token
     * @param claimsResolver Function that specifies which token
     * @return Generic T type of the token returned by Function
     * @param <T> type of the token to be returned
     * @since 0.0.1-SNAPSHOT
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * This method uses extractClaim to extract the username (subject in the context of jwt) from token
     * @param token token
     * @return username
     * @since 0.0.1-SNAPSHOT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * This method uses extractClaim to extract the expiration time from token
     * @param token token
     * @return expiration
     * @since 0.0.1-SNAPSHOT
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // generate token

    /**
     * This method issues a token with set subject (username of {@link User}),
     * expiration and signInKey. It also allows to set extra claims.
     * @see JwtProperties
     * @param extraClaims extraClaims to be added
     * @param user user
     * @return the token
     * @since 0.0.1-SNAPSHOT
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * This method issues a token with set subject (username of {@link User}),
     * expiration and signInKey.
     * @see JwtProperties
     * @param user user
     * @return the token
     * @since 0.0.1-SNAPSHOT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // token validation

    // TODO handle SignatureException by rotating security key

    /**
     * This method checks if a token is valid by combining the results of isTokenSubjectMatching and isTokenExpired.
     * If any of the two is false it returns false, if both are true it returns true
     * @param token token
     * @param user user
     * @return boolean
     * @since 0.0.1-SNAPSHOT
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return !isTokenExpired(token) && isTokenSubjectMatching(token, userDetails);
    }

    /**
     * This method returns true if the subject in the token is the same User passed to it. Otherwise it throws a
     * {@link JwtSubjectMissMatchException}
     * @param token token
     * @param user user
     * @return boolean
     * @since 0.0.1-SNAPSHOT
     */
    public boolean isTokenSubjectMatching(String token, UserDetails userDetails) {
        if (!(extractUsername(token).equals(userDetails.getUsername()))) {
            throw new JwtSubjectMissMatchException();
        }
        return true;
    }

    // TODO handle expired exception by redirecting to login again

    /**
     * This method checks if a token is expired by comparing current system time
     * to expiration. It returns true if the token is not expired and throws a
     * {@link io.jsonwebtoken.ExpiredJwtException} exception if the token is expired (by {@link Jwts} parser)
     * @see JwtProperties
     * @param token token
     * @return boolean
     * @since 0.0.1-SNAPSHOT
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // sign in key in hmac-sha265 / hs256 (minimum requirement)

    // TODO change key and store it properly

    /**
     * This method returns the sign in key decoded from secret key.
     * The key uses the hmac-sha265 algorithm which is the minimum requirement for jwt tokens.
     * @return sign in key
     */
    private Key getSignInKey() {
        byte[] decodedKey = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(decodedKey);
    }
}

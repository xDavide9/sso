package com.xdavide9.sso.jwt;

import com.xdavide9.sso.properties.JwtProperties;
import com.xdavide9.sso.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;
    private JwtService underTest;
    private String secretKey;
    private long expiration;

    @BeforeEach
    void setUp() {
        underTest = new JwtService(jwtProperties);
        secretKey = "536756659703373357638792F423F4528482B4D6251655468576D5A71347437";  // hs256
    }

    @Test
    void itShouldGenerateTokenWithoutExtraClaims() {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        expiration = 1000 * 60 * 60 * 24;
        given(jwtProperties.getSecretKey()).willReturn(secretKey);
        given(jwtProperties.getExpiration()).willReturn(expiration);
        // when
        String token = underTest.generateToken(user);
        // then
        assertThat(underTest.isTokenExpired(token)).isFalse();
        assertThat(underTest.isTokenValid(token, user)).isTrue();
        assertThat(underTest.extractUsername(token)).isEqualTo(username);
    }

    @Test
    void itShouldGenerateTokenWithExtraClaims() {
        // given
        String username = "xdavide9";
        String email = "xdavide9@gmail.com";
        User user = new User();
        user.setUsername(username);
        expiration = 1000 * 60 * 60 * 24;
        given(jwtProperties.getSecretKey()).willReturn(secretKey);
        given(jwtProperties.getExpiration()).willReturn(expiration);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", email);
        // when
        String token = underTest.generateToken(extraClaims, user);
        // then
        assertThat(underTest.isTokenExpired(token)).isFalse();
        assertThat(underTest.isTokenSubjectMatching(token, user)).isTrue();
        assertThat(underTest.isTokenValid(token, user)).isTrue();
        assertThat(underTest.extractUsername(token)).isEqualTo(username);
        String returnedEmail = (String) underTest.extractClaim(token, resolver -> resolver.get("email"));
        assertThat(returnedEmail).isEqualTo(email);
    }

    @Test
    void itShouldRecogniseExpiredToken() {
        // given
        String username = "xdavide9";
        User user = new User();
        user.setUsername(username);
        expiration = 0; // generate a token that expires immediately
        given(jwtProperties.getSecretKey()).willReturn(secretKey);
        given(jwtProperties.getExpiration()).willReturn(expiration);
        String expiredToken = underTest.generateToken(user);
        // when
        boolean expired = underTest.isTokenExpired(expiredToken);
        // then
        assertThat(expired).isTrue();
    }

    @Test
    void itShouldRecogniseUsernameMissMatch() {
        // given
        User user = new User();
        User user2 = new User();
        user.setUsername("username");
        user2.setUsername("username2");
        expiration = 1000 * 60 * 60 * 24; // generate a token that expires immediately
        given(jwtProperties.getSecretKey()).willReturn(secretKey);
        given(jwtProperties.getExpiration()).willReturn(expiration);
        String missMatchedToken = underTest.generateToken(user);
        // when
        boolean tokenSubjectMatching = underTest.isTokenSubjectMatching(missMatchedToken, user2);
        // then
        assertThat(tokenSubjectMatching).isFalse();
    }
}
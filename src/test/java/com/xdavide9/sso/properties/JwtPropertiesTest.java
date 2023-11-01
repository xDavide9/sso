package com.xdavide9.sso.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// integration test to see if jwt properties are correctly injected in JwtProperties POJO

@SpringBootTest
class JwtPropertiesTest {

    @Autowired
    private JwtProperties underTest;

    @Test
    void itShouldInjectJwtProperties() {
        // given
        String expectedSecretKey = "536756659703373357638792F423F4528482B4D6251655468576D5A71347437";
        long expectedExpiration = 24 * 60 * 60 * 1000;
        // when
        String secretKey = underTest.getSecretKey();
        long expiration = underTest.getExpiration();
        // then
        assertThat(secretKey).isEqualTo(expectedSecretKey);
        assertThat(expiration).isEqualTo(expectedExpiration);
    }
}
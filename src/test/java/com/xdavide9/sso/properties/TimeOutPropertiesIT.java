package com.xdavide9.sso.properties;

// integration test to see if api properties are correctly injected in AppProperties POJO

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TimeOutPropertiesIT {

    @Autowired
    private TimeOutProperties underTest;

    @Test
    void itShouldInjectJwtProperties() {
        assertThat(underTest.getDefaultTimeOutDuration()).isEqualTo(60);
        assertThat(underTest.getNumberOfThreads()).isEqualTo(1);
    }
}

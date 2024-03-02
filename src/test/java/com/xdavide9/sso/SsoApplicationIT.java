package com.xdavide9.sso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// this test checks if specific environment is loaded correctly

@SpringBootTest
@ActiveProfiles("test")
class SsoApplicationIT {

    @Test
    void contextLoads() {
    }

}

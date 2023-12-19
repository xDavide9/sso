package com.xdavide9.sso.user.api;

import com.xdavide9.sso.util.JsonParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// Tests the integration between OperatorController, OperatorService and UserRepository

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OperatorApiIT {

    @Autowired
    private MockMvc mockMvc;
    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;
    // also using repository to easily set up the environment
    // (authentication and registration processes already tested)

    @Test
    void itShould() {
        // given
        // when
        // then
    }
}

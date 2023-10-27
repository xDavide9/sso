package com.xdavide9.sso.user.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// TODO can use this setup to unit test controller logic (e.g. request building) but it's not implemented

@WebMvcTest(OperatorController.class)
class OperatorControllerTest {

    // TODO inject this version from application.properties that injects it from pom.xml
    private final String version = "0.0.1-SNAPSHOT";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OperatorService operatorService;
    @Test
    @WithMockUser // security features are not tested here and this is enough to bypass them
    void itShouldGetUsers() {
    }

    // ...
}
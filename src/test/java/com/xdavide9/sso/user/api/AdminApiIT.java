package com.xdavide9.sso.user.api;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.util.JsonParserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between AdminController, AdminService and UserRepository

// All tests should be wrapped in transactions because they modify database state
// and might lead to test failure elsewhere

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminApiIT {
    @Autowired
    private MockMvc mockMvc;
    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;

    // HELPER METHODS (functionality widely tested before)

    private String loginAsAdmin() throws Exception {
        String loginUsername = "adminUsername";
        String loginPassword = "adminPassword";
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        loginResultActions.andExpect(status().isOk());
        String responseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(responseBody, AuthenticationResponse.class);
        return loginResponse.token();
    }

    private UUID getUuidOfUserWithRoleUser(String token) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", token))
        );
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(responseBody, User.class);
        return user.getUuid();
    }

    // ACTUAL TESTS

    @Test
    @Transactional
    void itShouldPromoteUserToOperator() throws Exception {
        // given
        String token = loginAsAdmin();
        UUID uuid = getUuidOfUserWithRoleUser(token);
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("The user [%s] has been successfully promoted to operator", uuid));
    }

    // TODO continue testing from here (use @Transactional)
}

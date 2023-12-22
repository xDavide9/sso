package com.xdavide9.sso.user.api;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.util.JsonParserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between OperatorController, OperatorService and UserRepository
// against a db with 1 admin, 1 operator, 1 plain user; refer to TestingDatabaseConfig.java
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

    @Test
    void itShouldGetUserByUsername() throws Exception {
        // given
        // login to get jwt token first
        String operatorUsername = "operatorUsername";
        String operatorPassword = "operatorPassword";
        LoginRequest loginRequest = new LoginRequest(operatorUsername, operatorPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        String loginJsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = parser.java(loginJsonResponse, AuthenticationResponse.class);
        System.out.println("TOKEN" + authenticationResponse.token());
        String username = "userUsername";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", authenticationResponse.token()))
        );
        // then
        resultActions.andExpect(status().isOk());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getUsername()).isEqualTo(username);
    }
}

package com.xdavide9.sso.authentication.api;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.util.JsonParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between AuthenticationController, AuthenticationService and UserRepository

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;

    @Test
    void itShouldSignupANewAccount() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password";
        SignupRequest request = new SignupRequest(username, email, password);
        String json = parser.json(request);
        // when
        ResultActions signupResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );
        // then
        signupResultActions.andExpect(status().isOk());
        String jsonResponse = signupResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse response = parser.java(jsonResponse, AuthenticationResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.token()).isNotNull();
    }

    @Test
    void itShouldLoginIntoExistingAccount() throws Exception{
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password";
        SignupRequest request = new SignupRequest(username, email, password);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        mockMvc.perform(get("/logout"));
        LoginRequest loginRequest = new LoginRequest(username, password);
        // when
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        // then
        loginResultActions.andExpect(status().isOk());
        String jsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse response = parser.java(jsonResponse, AuthenticationResponse.class);
        assertThat(response.token()).isNotNull();
    }
}

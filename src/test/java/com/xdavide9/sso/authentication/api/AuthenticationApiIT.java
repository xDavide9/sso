package com.xdavide9.sso.authentication.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.util.JsonParserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between AuthenticationController, AuthenticationService and UserRepository
// tests are running in test database configured in application-test.properties

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthenticationApiIT {

    @Autowired
    private MockMvc mockMvc;

    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;

    // signup

    @Test
    void itShouldSignupANewAccount() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
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
    void itShouldNotSignupUsernameTaken() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
        SignupRequest request = new SignupRequest(username, email, password);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // no need to logout as jwt is stateless
        // when
        // try to signup again with same username
        String email2 = "another@email.com";
        String password2 = "password2"; // > 8 characters
        SignupRequest request2 = new SignupRequest(username, email2, password2);
        ResultActions usernameTakenResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request2))
        );
        // then
        usernameTakenResultActions.andExpect(status().isConflict());
        String jsonResponse = usernameTakenResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(jsonResponse, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
        assertThat(responseBody.get("error")).isEqualTo("Username already taken");
        assertThat(responseBody.get("message")).isEqualTo(format("Username [%s] is already taken", username));
    }

    @Test
    void itShouldNotSignupEmailTaken() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
        SignupRequest request = new SignupRequest(username, email, password);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // no need to logout as jwt is stateless
        // when
        // try to signup again with same email
        String username2 = "another username";
        String password2 = "password2"; // > 8 characters
        SignupRequest request2 = new SignupRequest(username2, email, password2);
        ResultActions emailTakenResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request2))
        );
        // then
        emailTakenResultActions.andExpect(status().isConflict());
        String jsonResponse = emailTakenResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(jsonResponse, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
        assertThat(responseBody.get("error")).isEqualTo("Email already taken");
        assertThat(responseBody.get("message")).isEqualTo(format("Email [%s] is already taken", email));
    }

    @Test
    void itShouldNotSignupPasswordTooShort() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "short"; // < 8 characters
        SignupRequest request = new SignupRequest(username, email, password);
        String json = parser.json(request);
        // when
        ResultActions passwordTooShortResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
        );
        // then
        passwordTooShortResultActions.andExpect(status().isBadRequest());
        String jsonResponse = passwordTooShortResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(jsonResponse, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
        assertThat(responseBody.get("error")).isEqualTo("Input password is too short (< 8 characters)");
        assertThat(responseBody.get("message")).isEqualTo("Password must be at least 8 characters long");
    }

    // login

    @Test
    void itShouldLoginIntoExistingAccount() throws Exception{
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
        SignupRequest request = new SignupRequest(username, email, password);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // no need to logout as jwt is stateless
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

    @Test
    void itShouldNotLoginAccountDoesNotExist() throws Exception{
        // given
        String username = "username";
        String password = "password1"; // > 8 characters
        LoginRequest loginRequest = new LoginRequest(username, password);
        // when
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        // then
        loginResultActions.andExpect(status().isNotFound());
        String jsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(jsonResponse, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Subject (username/email) not found");
        assertThat(responseBody.get("message")).isEqualTo(format("User with subject [%s] not found.", username));
        assertThat(responseBody.get("status")).isEqualTo(NOT_FOUND.toString());
    }

    @Test
    void itShouldNotLoginExistingAccountIncorrectPassword() throws Exception{
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
        SignupRequest request = new SignupRequest(username, email, password);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        String incorrectPassword = "incorrect";
        LoginRequest loginRequest = new LoginRequest(username, incorrectPassword);
        // when
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        // then
        loginResultActions.andExpect(status().isUnauthorized());
        String jsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(jsonResponse, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("incorrect input password at login");
        assertThat(responseBody.get("message")).isEqualTo(format("Incorrect input password at login for subject [%s]", username));
        assertThat(responseBody.get("status")).isEqualTo(UNAUTHORIZED.toString());
    }
}

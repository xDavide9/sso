package com.xdavide9.sso.authentication.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.authentication.SignupRequest;
import com.xdavide9.sso.user.PasswordDTO;
import com.xdavide9.sso.util.JsonParserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
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
        String password = "Password1!";
        PasswordDTO passwordDTO = new PasswordDTO(password);
        SignupRequest request = new SignupRequest(username, email, passwordDTO);
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
    void itShouldNotSignupInvalidUserInput() throws Exception{
        // given
        String username = "username";
        String password = "Password1!";
        PasswordDTO passwordDTO = new PasswordDTO(password);
        String email = "invalid email";
        SignupRequest request = new SignupRequest(username, email, passwordDTO);
        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // then
        resultActions.andExpect(status().isBadRequest());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        List<String> violations = List.of("email: Invalid email format");
        assertThat(responseBody.get("violations")).isEqualTo(violations);
        assertThat(responseBody.get("error")).isEqualTo("Invalid user input");
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
        assertThat(responseBody.get("message")).isEqualTo("One or more constraints have been violated, provide valid input next time");
    }

    @Test
    void itShouldNotSignupUsernameTaken() throws Exception {
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "Password1!";
        PasswordDTO passwordDTO = new PasswordDTO(password);
        SignupRequest request = new SignupRequest(username, email, passwordDTO);
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // no need to logout as jwt is stateless
        // when
        // try to signup again with same username
        String email2 = "another@email.com";
        String password2 = "Password2!"; // > 8 characters
        SignupRequest request2 = new SignupRequest(username, email2, new PasswordDTO(password2));
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
        String password = "Password1!";
        SignupRequest request = new SignupRequest(username, email, new PasswordDTO(password));
        mockMvc.perform(
                post("/api/v0.0.1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(parser.json(request))
        );
        // no need to logout as jwt is stateless
        // when
        // try to signup again with same email
        String username2 = "another username";
        String password2 = "Password2!"; // > 8 characters
        SignupRequest request2 = new SignupRequest(username2, email, new PasswordDTO(password2));
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

    // login

    @Test
    void itShouldLoginIntoExistingAccount() throws Exception{
        // given
        String username = "username";
        String email = "email@email.com";
        String password = "password1"; // > 8 characters
        SignupRequest request = new SignupRequest(username, email, new PasswordDTO(password));
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
        SignupRequest request = new SignupRequest(username, email, new PasswordDTO(password));
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

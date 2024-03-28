package com.xdavide9.sso.user.api;

import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.common.util.JsonParserService;
import com.xdavide9.sso.common.util.TestAuthenticator;
import com.xdavide9.sso.user.User;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between UserController, UserService and UserRepository

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({TestAuthenticator.class, JsonParserService.class})
@Transactional
public class UserApiIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAuthenticator authenticator;
    @Autowired
    private JsonParserService parser;

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetPersonalDetails(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions getDetails = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        getDetails.andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldChangeUsername(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions changeUsername = mockMvc.perform(
                put("/api/v0.0.1/principal/change/username?value=" + loginUsername + "9")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        changeUsername.andExpect(status().isOk());
        String newToken = changeUsername.andReturn().getResponse().getContentAsString();
        ResultActions getDetails = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + newToken)
        );
        String json = getDetails.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getUsername()).isEqualTo(loginUsername+"9");
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldChangeEmail(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions changeEmail = mockMvc.perform(
                put("/api/v0.0.1/principal/change/email?value=" + loginUsername + "new@email.com")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        changeEmail.andExpect(status().isOk());
        String newToken = changeEmail.andReturn().getResponse().getContentAsString();
        ResultActions getDetails = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + newToken)
        );
        String json = getDetails.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getEmail()).isEqualTo(loginUsername + "new@email.com");
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldChangePassword(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions changePassword = mockMvc.perform(
                put("/api/v0.0.1/principal/change/password?value=VeryStrong123!")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        changePassword.andExpect(status().isOk());
        ResultActions loginAgainWithNewPassword = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, "VeryStrong123!")))
        );
        loginAgainWithNewPassword.andExpect(status().isOk());
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword,+393339977001",
            "operatorUsername,operatorPassword,+393339977002",
            "adminUsername,adminPassword,+393339977003"
    })
    void itShouldChangePhoneNumber(String loginUsername, String loginPassword, String phoneNumber) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions changePhoneNumber = mockMvc.perform(
                put("/api/v0.0.1/principal/change/phoneNumber?value=" + phoneNumber)
                        .header("Authorization", "Bearer " + token)
        );
        // then
        changePhoneNumber.andExpect(status().isOk());
        String newToken = changePhoneNumber.andReturn().getResponse().getContentAsString();
        ResultActions getDetails = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + newToken)
        );
        String json = getDetails.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldChangeCountry(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions changeCountry = mockMvc.perform(
                put("/api/v0.0.1/principal/change/country?value=IT")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        changeCountry.andExpect(status().isOk());
        String newToken = changeCountry.andReturn().getResponse().getContentAsString();
        ResultActions getDetails = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + newToken)
        );
        String json = getDetails.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getCountry().getCountryCode()).isEqualTo("IT");
    }
}

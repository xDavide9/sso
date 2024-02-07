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

@SpringBootTest
@ActiveProfiles("test2")
@AutoConfigureMockMvc
public class TimeOutDefaultDurationIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonParserService parser;

    // HELPER METHODS

    private User getUser(String username, String token) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/" + username)
                        .header("Authorization", format("Bearer %s", token))
        );
        resultActions.andExpect(status().isOk());
        return parser.java(resultActions.andReturn().getResponse().getContentAsString(), User.class);
    }

    private User getUserWithRoleUser(String token) throws Exception {
        return getUser("userUsername", token);
    }
    private User getUserWithRoleOperator(String token) throws Exception {
        return getUser("operatorUsername", token);
    }
    private User getUserWithRoleAdmin(String token) throws Exception {
        return getUser("adminUsername", token);
    }

    private String loginAsOperator() throws Exception {
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("operatorUsername", "operatorPassword")))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        return loginResponse.token();
    }

    @Test
    @Transactional
    void itShouldTimeOutUserCorrectlyWithDefaultDuration() throws Exception {
        // given
        // log in
        String token = loginAsOperator();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("User with uuid [%s] has been timed out for the default duration", uuid));
        User timedOutUser = getUserWithRoleUser(token);
        assertThat(timedOutUser.isEnabled()).isFalse();
        Thread.sleep(1000);
        // need to log in again because jwt expired too
        token = loginAsOperator();
        User enabledUser = getUserWithRoleUser(token);
        assertThat(enabledUser.isEnabled()).isTrue();
    }
}

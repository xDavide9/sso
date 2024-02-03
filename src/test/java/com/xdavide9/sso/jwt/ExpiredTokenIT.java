package com.xdavide9.sso.jwt;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.util.JsonParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// holds a simple integration test to see what happens after a token expires

@SpringBootTest
@ActiveProfiles("test2")
@AutoConfigureMockMvc
public class ExpiredTokenIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonParserService parser;

    @Test
    void itShouldNotGetUserByUsernameExpiredToken() throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("operatorUsername", "operatorPassword")))
        );
        loginResultActions.andExpect(status().isOk());
        String contentAsString = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(contentAsString, AuthenticationResponse.class);
        String token = loginResponse.token();
        // waiting at least 1 second token must be expired by now
        Thread.sleep(1000);
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Expired jwt token. Login again to provide a new one.");
    }
}

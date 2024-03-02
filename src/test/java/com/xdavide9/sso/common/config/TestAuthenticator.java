package com.xdavide9.sso.common.config;

import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.common.util.JsonParserService;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// todo creare docs per questo sistema di testing

@Import(TestingDatabaseConfig.class)
@TestComponent
public class TestAuthenticator {

    private final MockMvc mockMvc;
    private final JsonParserService parser;

    @Autowired
    public TestAuthenticator(MockMvc mockMvc, JsonParserService parser) {
        this.mockMvc = mockMvc;
        this.parser = parser;
    }

    public String login(String loginUsername, String loginPassword) throws Exception {
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

    public String loginAsAdmin() throws Exception {
        return login("adminUsername", "adminPassword");
    }

    public String loginAsOperator() throws Exception {
        return login("operatorUsername", "operatorPassword");
    }

    public String loginAsUser() throws Exception {
        return login("userUsername", "userPassword");
    }

    public User getGenericUser(String token, String username) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .header("Authorization", format("Bearer %s", token))
        );
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        return parser.java(responseBody, User.class);
    }

    public User getUserWithRoleUser(String token) throws Exception {
        return getGenericUser(token, "userUsername");
    }

    public User getUserWithRoleOperator(String token) throws Exception {
        return getGenericUser(token, "operatorUsername");
    }

    public User getUserWithRoleAdmin(String token) throws Exception {
        return getGenericUser(token, "adminUsername");
    }
}

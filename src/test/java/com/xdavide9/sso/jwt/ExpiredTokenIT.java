package com.xdavide9.sso.jwt;

import com.xdavide9.sso.common.config.OverrideJwtPropertiesConfig;
import com.xdavide9.sso.common.util.TestAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// holds a simple integration test to see what happens after a token expires
// overrides duration with special property bean
// abstract authentication logic (already tested) with TestAuthenticator

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({OverrideJwtPropertiesConfig.class, TestAuthenticator.class})
public class ExpiredTokenIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAuthenticator authenticator;

    @Test
    void itShouldNotGetUserByUsernameExpiredToken() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        // waiting at least 1 second token must be expired by now @see OverrideJwtPropertiesConfig
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

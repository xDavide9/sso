package com.xdavide9.sso.user.api;

import com.xdavide9.sso.common.config.OverrideTimeOutPropertiesConfig;
import com.xdavide9.sso.common.config.TestingDatabaseConfig;
import com.xdavide9.sso.common.util.TestAuthenticator;
import com.xdavide9.sso.user.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({TestAuthenticator.class, TestingDatabaseConfig.class, OverrideTimeOutPropertiesConfig.class})
public class TimeOutDefaultDurationIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAuthenticator authenticator;

    @Test
    @Transactional
    void itShouldTimeOutUserCorrectlyWithDefaultDuration() throws Exception {
        // given
        // log in
        String token = authenticator.loginAsOperator();
        String userToken = authenticator.loginAsUser();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // not 30 minutes in this case but 1000 millis because of OverrideTimeOutPropertiesConfig
        // but that is still the message returned in the real situation
        assertThat(responseBody).isEqualTo(format("User with uuid [%s] has been timed out for 30 minutes.", uuid));
        User timedOutUser = authenticator.getUserWithRoleUser(token);
        assertThat(timedOutUser.isEnabled()).isFalse();
        // now check he is enabled again wait for 1000 millis
        Thread.sleep(1000);
        // user should perform a request so that filter can enable him
        ResultActions reEnable = mockMvc.perform(
                get("/api/v0.0.1/principal")
                        .header("Authorization", "Bearer " + userToken)
        );
        reEnable.andExpect(status().isOk());
        User reEnabledUser = authenticator.getUserWithRoleUser(token);
        assertThat(reEnabledUser.isEnabled()).isTrue();
    }
}

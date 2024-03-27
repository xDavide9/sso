package com.xdavide9.sso.user.change;

import com.xdavide9.sso.common.util.JsonParserService;
import com.xdavide9.sso.common.util.TestAuthenticator;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.fields.UserField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({TestAuthenticator.class, JsonParserService.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserChangeApiIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonParserService parser;
    @Autowired
    private TestAuthenticator authenticator;

    @Test
    void itShouldPersistChangeMadeToAUser() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        User user = authenticator.getUserWithRoleUser(token);
        UUID uuid = user.getUuid();
        ResultActions ban = mockMvc.perform(
                put("/api/v0.0.1/users/change/username/" + uuid + "?username=newUsername")
                        .header("Authorization", "Bearer " + token)
        );
        ban.andExpect(status().isOk());
        // when
        ResultActions getChange = mockMvc.perform(
                get("/api/v0.0.1/users/changes/1")
                        .header("Authorization", "Bearer " + token)
        );
        // then
        getChange.andExpect(status().isOk());
        String json = getChange.andReturn().getResponse().getContentAsString();
        UserChange userChange = parser.java(json, UserChange.class);
        assertThat(userChange.getUser()).isEqualTo(user);
        assertThat(userChange.getField()).isEqualTo(UserField.USERNAME);
        assertThat(userChange.getPreviousValue()).isEqualTo("userUsername");
        assertThat(userChange.getUpdatedValue()).isEqualTo("newUsername");
    }
}

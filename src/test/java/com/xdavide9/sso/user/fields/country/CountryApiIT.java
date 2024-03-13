package com.xdavide9.sso.user.fields.country;

import com.xdavide9.sso.common.util.JsonParserService;
import com.xdavide9.sso.common.util.TestAuthenticator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestAuthenticator.class)
public class CountryApiIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestAuthenticator authenticator;
    @Autowired
    private JsonParserService parser;

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetAllCountries(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/countries")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void itShouldNotGetAllCountriesTokenIsMissing() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/countries")
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetAllCountriesNotEnoughAuthorization() throws Exception {
        // given
        String token = authenticator.login("userUsername", "userPassword");
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/countries")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetCountryById(String loginUsername, String loginPassword) throws Exception {
        // given
        String countryCode = "IT";
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/countries/%s", countryCode))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Country country = parser.java(response, Country.class);
        assertThat(country.getCountryCode()).isEqualTo("IT");
        assertThat(country.getDisplayName()).isEqualTo("Italy");
        assertThat(country.getPhoneNumberCode()).isEqualTo(39);

    }

    @Test
    void itShouldNotGetCountryByIdTokenIsMissing() throws Exception {
        // given
        String countryCode = "IT";
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/countries/" + countryCode)
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetCountryByIdNotEnoughAuthorization() throws Exception {
        // given
        String token = authenticator.login("userUsername", "userPassword");
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/countries/IT")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }
}

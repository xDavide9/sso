package com.xdavide9.sso.user.fields.country;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

// unit tests running against sso_country table in public schema using h2 autoconfigured in memory database for UserRepository
@DataJpaTest
class CountryRepositoryTest {

    @Autowired
    private CountryRepository underTest;

    @Test
    void itShouldSaveCountry() {
        // given
        Country country = new Country("IT", "Italy", 39);
        // when
        underTest.save(country);
        Optional<Country> optional = underTest.findById("IT");
        // then
        assert optional.isPresent();
        AssertionsForClassTypes.assertThat(country).isEqualTo(optional.get());
    }
}
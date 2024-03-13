package com.xdavide9.sso.user.fields.country;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @InjectMocks
    private CountryService underTest;

    @Mock
    private CountryRepository repository;

    @Test
    void itShouldGetAllCountries() {
        // given
        // when
        underTest.getAllCountries();
        // then
        verify(repository).findAll();
    }

    @Test
    void itShouldGetUsersPerCountryCorrectly() {
        // given
        Country country = new Country("IT", "Italy", 39);
        given(repository.findById("IT")).willReturn(Optional.of(country));
        // when
        Country italy = underTest.getCountry("IT");
        // then
        assertThat(italy).isEqualTo(country);
    }
}
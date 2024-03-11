package com.xdavide9.sso.user.fields.country;

import com.xdavide9.sso.exception.user.fields.country.CountryNotFoundException;
import com.xdavide9.sso.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public Country getCountry(String countryCode) {
        Optional<Country> countryOption = countryRepository.findById(countryCode);
        if (countryOption.isEmpty())
            throw new CountryNotFoundException(format("Country with code [%s] not found", countryCode));
        return countryOption.get();
    }
}

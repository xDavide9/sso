package com.xdavide9.sso.user.fields.country;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.xdavide9.sso.exception.user.fields.country.CountryNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

/**
 * Holds business logic {@link CountryController}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final PhoneNumberUtil phoneNumberUtil;

    @Autowired
    public CountryService(CountryRepository countryRepository,
                          PhoneNumberUtil phoneNumberUtil) {
        this.countryRepository = countryRepository;
        this.phoneNumberUtil = phoneNumberUtil;
    }

    @PostConstruct
    public void persistCountries() {
        Set<String> supportedRegions = phoneNumberUtil.getSupportedRegions();
        Locale locale;
        for (String regionCode : supportedRegions) {
            int phoneNumberCode = phoneNumberUtil.getCountryCodeForRegion(regionCode);
            locale = Locale.of("", regionCode);
            String countryName = locale.getDisplayCountry();
            Country country = new Country(regionCode, countryName, phoneNumberCode);
            countryRepository.save(country);
        }
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

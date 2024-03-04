package com.xdavide9.sso.config;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.xdavide9.sso.user.fields.country.Country;
import com.xdavide9.sso.user.fields.country.CountryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Set;

@Configuration
public class CountryConfig {

    private final CountryRepository countryRepository;

    public CountryConfig(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Bean
    PhoneNumberUtil phoneNumberUtil() {
        return PhoneNumberUtil.getInstance();
    }

    @Bean
    CommandLineRunner persistCountries() {
        return args -> {
            Set<String> supportedRegions = phoneNumberUtil().getSupportedRegions();
            Locale locale;
            for (String regionCode : supportedRegions) {
                int phoneNumberCode = phoneNumberUtil().getCountryCodeForRegion(regionCode);
                locale = Locale.of("", regionCode);
                String countryName = locale.getDisplayCountry();
                Country country = new Country(regionCode, countryName, phoneNumberCode);
                countryRepository.save(country);
            }
        };
    }
}

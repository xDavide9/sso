package com.xdavide9.sso.user.fields.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Delegates business logic to {@link CountryService}.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/v0.0.1/countries")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{countryCode}")
    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public Country getCountry(@PathVariable("countryCode") String countryCode) {
        return countryService.getCountry(countryCode);
    }
}

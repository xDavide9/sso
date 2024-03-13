package com.xdavide9.sso.user.fields.country;

import com.xdavide9.sso.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class models countries to be associated to instances of {@link User} via a
 * many-to-one relationship.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "sso_country")
public class Country {
    /**
     * ISO 3166-1 alpha-2 code
     */
    @Id
    @Column(name = "country_code")
    private String countryCode;
    /**
     * The actual country name
     */
    @Column(nullable = false, updatable = false)
    private String displayName;
    /**
     * phone number prefix for given country
     */
    @Column(nullable = false, updatable = false)
    private int phoneNumberCode;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    public Country() {
    }

    public Country(String countryCode,
                   String displayName,
                   int phoneNumberCode) {
        this.countryCode = countryCode;
        this.displayName = displayName;
        this.phoneNumberCode = phoneNumberCode;
    }

    // GETTER

    public String getCountryCode() {
        return countryCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPhoneNumberCode() {
        return phoneNumberCode;
    }

    public Set<User> getUsers() {
        return users;
    }

    // SETTER


    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhoneNumberCode(int phoneNumberCode) {
        this.phoneNumberCode = phoneNumberCode;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // EQUALS, HASHCODE, TOSTRING


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(countryCode, country.countryCode) && Objects.equals(displayName, country.displayName) && Objects.equals(phoneNumberCode, country.phoneNumberCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, displayName, phoneNumberCode);
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryCode='" + countryCode + '\'' +
                ", displayName='" + displayName + '\'' +
                ", phoneNumberCode='" + phoneNumberCode + '\'' +
                '}';
    }
}

package com.xdavide9.sso.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xdavide9.sso.config.SecurityConfig;
import com.xdavide9.sso.user.fields.Gender;
import com.xdavide9.sso.user.fields.country.Country;

import java.time.LocalDate;

/**
 * This class models a http request to be sent to the signup endpoint (see {@link SecurityConfig}.
 * In this process username, email and password must be sent, every one field is optional and can be provided
 * through setters.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class SignupRequest {
    private final String username, email, password;
    private String firstName, lastName, phoneNumber;
    private Gender gender;
    private String countryCode;
    private LocalDate dateOfBirth;

    @JsonCreator
    public SignupRequest(String username,
                         String email,
                         String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @JsonProperty
    public String username() {
        return username;
    }

    @JsonProperty
    public String email() {
        return email;
    }

    @JsonProperty
    public String password() {
        return password;
    }

    @JsonProperty
    public String firstName() {
        return firstName;
    }

    @JsonProperty
    public String lastName() {
        return lastName;
    }

    @JsonProperty
    public String phoneNumber() {
        return phoneNumber;
    }

    @JsonProperty
    public Gender gender() {
        return gender;
    }

    @JsonProperty
    public String country() {
        return countryCode;
    }

    @JsonProperty
    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setCountry(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

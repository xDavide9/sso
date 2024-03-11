package com.xdavide9.sso.exception.user.fields.country;

import com.xdavide9.sso.user.fields.country.Country;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * This class holds {@link ExceptionHandler}s related to the {@link Country} api.
 * @since 0.0.1-SNAPSHOT
 * @author xDavide9
 */
@ControllerAdvice
public class CountryExceptionsHandler {

    @ExceptionHandler(value = CountryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCountryNotFoundException(CountryNotFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Country not found");
        responseBody.put("message", e.getMessage());
        responseBody.put("status", NOT_FOUND.toString());
        return new ResponseEntity<>(responseBody, NOT_FOUND);
    }
}

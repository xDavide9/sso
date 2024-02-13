package com.xdavide9.sso.user;

import com.xdavide9.sso.util.ValidatorService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * This class is a DTO for the password field of {@link User}. It is used
 * to apply validation constraints by {@link ValidatorService}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public class PasswordDTO {
    @NotBlank(message = "Password cannot be blank nor null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()-_=+\\\\|\\[{\\]};:'\",<.>/?]).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    public PasswordDTO(String password) {
        this.password = password;
    }

    public PasswordDTO() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

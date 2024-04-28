package com.xdavide9.sso.authentication.verification;

import com.xdavide9.sso.user.User;

/**
 * Verifying the email is required for every user in the system before proceeding
 * with any other operation. This is achieved via sending an email whose content
 * depend on the implementation
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public interface EmailVerifier {
    /**
     * Sends an email with the procedure required to verify the email
     */
    void sendVerificationEmail(String content);

    /**
     * Assert whether the email was verified, can be mocked in tests
     */
    boolean isEmailVerified(User user);
}

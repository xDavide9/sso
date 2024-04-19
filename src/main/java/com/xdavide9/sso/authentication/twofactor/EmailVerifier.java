package com.xdavide9.sso.authentication.twofactor;

import com.xdavide9.sso.user.User;

/**
 * Verifying the email is required for every user in the system before proceeding
 * with any other operation. The operation is divided in 2 parts:
 * 1) send a verification email with a link inside
 * 2) check if the link was pressed every time the user tries to perform an
 *      action in the system (lazy approach)
 */
public interface EmailVerifier {
    void sendVerificationEmail(String content);
    boolean isEmailVerified(User user);
}

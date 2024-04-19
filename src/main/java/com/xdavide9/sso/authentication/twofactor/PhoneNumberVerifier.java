package com.xdavide9.sso.authentication.twofactor;

import com.xdavide9.sso.user.User;

/**
 * Verifying the phone number is optional. The operation is divided in 2 parts:
 * 1) send a verification sms with a link inside
 * 2) check if the link was pressed every time the user tries to perform an
 *      action in the system (lazy approach)
 */
public interface PhoneNumberVerifier {
    void sendVerificationSms(String content);
    boolean isVerifiedPhoneNumber(User user);
}

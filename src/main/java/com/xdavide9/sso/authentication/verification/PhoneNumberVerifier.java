package com.xdavide9.sso.authentication.verification;

import com.xdavide9.sso.user.User;

/**
 * Verifying the phone number is optional. This is achieved via sending a sms whose content
 * depend on the implementation
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
public interface PhoneNumberVerifier {
    /**
     * Sends a sms with the procedure required to verify the phoneNumber
     */
    void sendVerificationSms(String content);
    /**
     * Assert whether the phoneNumber was verified, can be mocked in tests
     */
    boolean isVerifiedPhoneNumber(User user);
}

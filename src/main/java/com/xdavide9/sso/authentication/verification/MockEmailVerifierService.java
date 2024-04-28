package com.xdavide9.sso.authentication.verification;

import com.xdavide9.sso.user.User;
import org.springframework.stereotype.Service;

/**
 * Mock service, everybody's email is always verified
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class MockEmailVerifierService implements EmailVerifier {
    @Override
    public void sendVerificationEmail(String content) {

    }

    @Override
    public boolean isEmailVerified(User user) {
        return true;
    }
}

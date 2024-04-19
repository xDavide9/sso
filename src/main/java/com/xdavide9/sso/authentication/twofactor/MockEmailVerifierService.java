package com.xdavide9.sso.authentication.twofactor;

import com.xdavide9.sso.user.User;
import org.springframework.stereotype.Service;

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

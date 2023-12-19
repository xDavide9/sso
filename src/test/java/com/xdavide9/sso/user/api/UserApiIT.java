package com.xdavide9.sso.user.api;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserApiIT {
    // Tests the integration between UserController, UserService and UserRepository
    // TODO implement integration testing using mockmvc with concern to security features
}

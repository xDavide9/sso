package com.xdavide9.sso.authentication;

import com.xdavide9.sso.exception.authentication.SubjectNotFoundException;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * This class provides a custom userDetailsService implementation
 * which uses {@link UserRepository} to return {@link User} entities
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public RepositoryUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * This method uses a custom query to find either by username or email (subject) from the database.
     * @param subject This variable holds either the email or username depending on which one the user input in the form
     * @return UserDetails of the matched user
     * @throws SubjectNotFoundException when the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String subject) {
        return repository.findByUsernameOrEmail(subject, subject)
                .orElseThrow(() -> new SubjectNotFoundException(
                        format("User with subject [%s] not found.", subject)
                ));
    }
}

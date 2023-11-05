package com.xdavide9.sso.user;

import com.xdavide9.sso.exception.user.UsernameNorEmailNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class provides a custom userDetailsService implementation
 * which uses {@link UserRepository} to return {@link User} entities
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    /**
     * repository
     * @since 0.0.1-SNAPSHOT
     */
    private final UserRepository repository;

    /**
     * constructor
     * @since 0.0.1-SNAPSHOT
     * @param repository user repository
     */
    @Autowired
    public RepositoryUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNorEmailNotFound("User with username or email [%s] not found."));
    }
}

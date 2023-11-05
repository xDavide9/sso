package com.xdavide9.sso.user;

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

    private final UserRepository repository;

    @Autowired
    public RepositoryUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found."));
    }
}

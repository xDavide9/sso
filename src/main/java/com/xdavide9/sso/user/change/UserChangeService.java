package com.xdavide9.sso.user.change;

import com.xdavide9.sso.exception.user.change.UserChangeNotFoundException;
import com.xdavide9.sso.user.api.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

/**
 * Holds business logic for {@link UserChangeController}.
 * @since 0.0.1-SNAPSHOT
 * @author xdavide9
 */
@Service
public class UserChangeService {

    private final UserChangeRepository repository;
    /**
     * Reusing to get user by id easily
     */
    private final OperatorService operatorService;

    @Autowired
    public UserChangeService(UserChangeRepository repository,
                             OperatorService operatorService) {
        this.repository = repository;
        this.operatorService = operatorService;
    }

    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<UserChange> getAllChanges() {
        return repository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public UserChange getChange(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new UserChangeNotFoundException(format("A specific change with id [%s] made to a user was not found", id)));
    }

    @PreAuthorize("hasAnyAuthority('OPERATOR_GET', 'ADMIN_GET')")
    public List<UserChange> getChangesPerUser(UUID uuid) {
        return repository
                .findAllByUserIs(operatorService.getUserByUuid(uuid));
    }
}

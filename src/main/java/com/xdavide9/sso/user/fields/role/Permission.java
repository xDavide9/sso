package com.xdavide9.sso.user.fields.role;

import com.xdavide9.sso.user.User;

/**
 * This enum represents permissions that are attached to {@link Role}s to create
 * authorities for {@link User}s.
 * Each permission consists of the Role followed by either GET, PUT or DELETE that represent the type of operation
 * allowed by that permission. GET, PUT and DELETE generally  indicate permission to get, modify and delete
 * information respectively. GET and PUT have different weight depending on the role they are attached to and
 * DELETE is reserved to Admins.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
public enum Permission {

    ADMIN_GET,
    ADMIN_PUT,
    ADMIN_DELETE,
    OPERATOR_GET,
    OPERATOR_PUT,
    USER_GET,
    USER_PUT
}

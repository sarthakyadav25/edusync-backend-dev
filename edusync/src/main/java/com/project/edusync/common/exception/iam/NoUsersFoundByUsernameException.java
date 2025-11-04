package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;

public class NoUsersFoundByUsernameException extends EdusyncException {
    public NoUsersFoundByUsernameException(String message) {
        super(message);
    }
}

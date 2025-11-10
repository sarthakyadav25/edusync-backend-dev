package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;

public class NoUsersFoundByUsernameException extends InvalidCredentialsException {

    public NoUsersFoundByUsernameException(String message) {
        super(message);
    }
}

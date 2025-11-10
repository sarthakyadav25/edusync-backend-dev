package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when an authentication attempt fails due to either a bad username
 * or a bad password.
 */
public class InvalidCredentialsException extends EdusyncException {

    public InvalidCredentialsException(String message) {
        // Pass the message and the specific HttpStatus (401) to the parent
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
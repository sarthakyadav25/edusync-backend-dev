package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends EdusyncException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

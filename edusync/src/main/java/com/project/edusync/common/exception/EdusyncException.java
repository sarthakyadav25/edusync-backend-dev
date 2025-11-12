package com.project.edusync.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all custom exceptions in the Edusync application.
 * This allows for a centralized handling mechanism by carrying the
 * intended HTTP status.
 */
public class EdusyncException extends RuntimeException {

    private final HttpStatus httpStatus;

    public EdusyncException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public EdusyncException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
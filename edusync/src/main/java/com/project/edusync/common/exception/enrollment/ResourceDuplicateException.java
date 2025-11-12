package com.project.edusync.common.exception.enrollment;

import org.springframework.http.HttpStatus;

/**
 * Thrown by the import service when a row fails a business logic check
 * for uniqueness (e.g., email, username, or employeeId already exists).
 * This is a row-specific error.
 *
 * We use 409 CONFLICT to differentiate this from a simple 400 BAD_REQUEST.
 * It passes HttpStatus.CONFLICT to the base EdusyncException.
 */
public class ResourceDuplicateException extends BulkImportException {

    public ResourceDuplicateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

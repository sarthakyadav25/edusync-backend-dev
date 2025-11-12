package com.project.edusync.common.exception.enrollment;

import org.springframework.http.HttpStatus;

/**
 * A fatal exception thrown when the CSV header row does not match
 * the expected format. This error should stop the entire import.
 *
 * It passes HttpStatus.BAD_REQUEST to the base EdusyncException.
 */
public class InvalidCsvHeaderException extends BulkImportException {

    public InvalidCsvHeaderException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

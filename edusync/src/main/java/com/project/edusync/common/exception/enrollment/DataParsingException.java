package com.project.edusync.common.exception.enrollment;

import org.springframework.http.HttpStatus;

/**
 * Thrown by CsvValidationHelper when a field in a row is missing,
 * malformed, or contains an invalid value (e.g., bad date, invalid enum).
 * This is a row-specific error.
 *
 * It passes HttpStatus.BAD_REQUEST to the base EdusyncException.
 */
public class DataParsingException extends BulkImportException {

    public DataParsingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
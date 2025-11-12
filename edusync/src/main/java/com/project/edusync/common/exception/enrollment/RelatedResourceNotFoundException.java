package com.project.edusync.common.exception.enrollment;

import org.springframework.http.HttpStatus;

/**
 * Thrown by the import service when a row references a related entity
 * that does not exist in the database (e.g., a non-existent Role,
 * or a Section/Class that wasn't found in the cache).
 * This is a row-specific error.
 *
 * It passes HttpStatus.BAD_REQUEST to the base EdusyncException.
 */
public class RelatedResourceNotFoundException extends BulkImportException {

    public RelatedResourceNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

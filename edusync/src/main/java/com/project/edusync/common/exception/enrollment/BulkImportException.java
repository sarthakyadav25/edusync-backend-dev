package com.project.edusync.common.exception.enrollment;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all errors related to the bulk import process.
 * Extends the application-wide EdusyncException to ensure
 * consistent error handling and HTTP status propagation.
 */
public class BulkImportException extends EdusyncException {

  /**
   * Constructor for bulk import exceptions.
   * @param message The error message.
   * @param httpStatus The HTTP status code this exception should trigger.
   */
  public BulkImportException(String message, HttpStatus httpStatus) {
    super(message, httpStatus);
  }

  /**
   * Constructor for wrapping a root cause.
   * @param message The error message.
   * @param httpStatus The HTTP status code.
   * @param cause The original exception.
   */
  public BulkImportException(String message, HttpStatus httpStatus, Throwable cause) {
    super(message, httpStatus, cause);
  }
}
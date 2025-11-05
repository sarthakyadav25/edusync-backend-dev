package com.project.edusync.enrollment.util;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Validation utility component for parsing CSV data.
 * Extracted from the service layer to adhere to Single Responsibility Principle.
 * Marked as @Component to be injectable in other Spring beans.
 */
@Component
public class CsvValidationHelper {

    /**
     * Validates that a string is not null or blank.
     * @param value the string to check
     * @param fieldName the name of the field for error messages
     * @return the trimmed, validated string
     * @throws IllegalArgumentException if validation fails
     */
    public String validateString(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required and cannot be blank.");
        }
        return value.trim();
    }

    /**
     * Validates a string is a properly formatted email.
     * @param email the string to check
     * @return the validated email
     * @throws IllegalArgumentException if validation fails
     */
    public String validateEmail(String email) {
        String validatedEmail = validateString(email, "email");
        // A simple regex. A more robust one could be used.
        if (!validatedEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Email '" + validatedEmail + "' is not valid.");
        }
        return validatedEmail;
    }

    /**
     * Parses a string into a LocalDate.
     * @param value the string to parse (e.g., "2024-10-20")
     * @param fieldName the name of the field for error messages
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if validation fails
     */
    public LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(validateString(value, fieldName)); // Assumes YYYY-MM-DD
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " '" + value + "' is not a valid date. Expected format YYYY-MM-DD.");
        }
    }

    /**
     * Parses a string into a given Enum type.
     * @param enumClass the .class of the Enum
     * @param value the string value to parse
     * @param fieldName the name of the field for error messages
     * @return the corresponding Enum constant
     * @throws IllegalArgumentException if validation fails
     */
    public <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, String fieldName) {
        try {
            return Enum.valueOf(enumClass, validateString(value, fieldName).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " '" + value + "' is not a valid value for " + enumClass.getSimpleName() + ".");
        }
    }
}
package com.project.edusync.enrollment.util;

import com.project.edusync.uis.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for CsvValidationHelper.
 * This test does not require Spring context and runs as a plain JUnit test.
 * * --- V3: Final fix for enum error message ---
 */
class CsvValidationHelperTest {

    private CsvValidationHelper validationHelper;

    @BeforeEach
    void setUp() {
        // Instantiate the class directly
        validationHelper = new CsvValidationHelper();
    }

    // --- validateString ---

    @Test
    void validateString_success() throws Exception {
        assertEquals("Test", validationHelper.validateString("Test", "fieldName"));
        assertEquals("Another Test", validationHelper.validateString("  Another Test  ", "fieldName")); // Should trim
    }

    @Test
    void validateString_throwsException_whenNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            validationHelper.validateString(null, "fieldName");
        });
        assertEquals("fieldName is required and cannot be blank.", e.getMessage());
    }

    @Test
    void validateString_throwsException_whenEmpty() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            validationHelper.validateString("   ", "fieldName"); // Should trim and see empty
        });
        assertEquals("fieldName is required and cannot be blank.", e.getMessage());
    }

    // --- validateEmail ---

    @Test
    void validateEmail_success() throws Exception {
        assertEquals("test@example.com", validationHelper.validateEmail("test@example.com"));
    }

    @Test
    void validateEmail_throwsException_whenInvalid() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            validationHelper.validateEmail("invalid-email.com");
        });
        assertEquals("Email 'invalid-email.com' is not valid.", e.getMessage());
    }

    // --- parseDate ---

    @Test
    void parseDate_success() throws Exception {
        assertEquals(LocalDate.of(2023, 1, 31), validationHelper.parseDate("2023-01-31", "dateField"));
    }

    @Test
    void parseDate_throwsException_whenInvalidFormat() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            validationHelper.parseDate("31/01/2023", "dateField"); // Invalid format
        });
        assertEquals("dateField '31/01/2023' is not a valid date. Expected format YYYY-MM-DD.", e.getMessage());
    }

    // --- parseEnum ---

    @Test
    void parseEnum_success() throws Exception {
        assertEquals(Gender.MALE, validationHelper.parseEnum(Gender.class, "MALE", "gender"));
        assertEquals(Gender.FEMALE, validationHelper.parseEnum(Gender.class, "female", "gender")); // Case-insensitivity
    }

    @Test
    void parseEnum_throwsException_whenInvalidValue() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            validationHelper.parseEnum(Gender.class, "INVALID", "gender");
        });

        String errorMessage = e.getMessage();
        assertNotNull(errorMessage, "Error message should not be null");
        assertTrue(errorMessage.contains("gender"), "Error message should mention 'gender'");
        assertTrue(errorMessage.contains("'INVALID'"), "Error message should mention the invalid value");
        assertTrue(errorMessage.contains("not a valid value"), "Error message should indicate an invalid value");
    }
}
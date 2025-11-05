package com.project.edusync.enrollment.controller;

import com.project.edusync.enrollment.service.BulkImportService;
import com.project.edusync.enrollment.model.dto.BulkImportReportDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller to handle bulk user registration via CSV upload.
 * This endpoint is secured and accessible only to administrative roles.
 */
@RestController
@AllArgsConstructor
@RequestMapping("${api.url}/auth/bulk-import")
public class BulkImportController {

    private final BulkImportService bulkImportService;

    /**
     * Handles the CSV file upload for bulk user registration.
     *
     * @param userType "students" or "staff", indicating the type of users to import.
     * @param file     The multipart CSV file containing user data.
     * @return A ResponseEntity containing a BulkImportReportDTO with the results.
     */
    @PostMapping(value = "/{userType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import users from CSV")
    public ResponseEntity<BulkImportReportDTO> handleBulkImport(
            @PathVariable String userType,
            @RequestParam("file") MultipartFile file) {

        // Basic file validation
        if (file.isEmpty()) {
            // A more robust DTO could be used, but for now, this is a clean error
            return ResponseEntity.badRequest().body(
                    new BulkImportReportDTO("FAILED", "File is empty.")
            );
        }

        if (!"text/csv".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body(
                    new BulkImportReportDTO("FAILED", "Invalid file type. Please upload a CSV.")
            );
        }

        // Delegate all logic to the service layer
        try {
            BulkImportReportDTO report = bulkImportService.importUsers(file, userType);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            // This catches errors like invalid userType
            return ResponseEntity.badRequest().body(
                    new BulkImportReportDTO("FAILED", e.getMessage())
            );
        } catch (IOException e) {
            // This catches issues with reading the file stream
            return ResponseEntity.internalServerError().body(
                    new BulkImportReportDTO("FAILED", "Error processing file: " + e.getMessage())
            );
        }
    }
}
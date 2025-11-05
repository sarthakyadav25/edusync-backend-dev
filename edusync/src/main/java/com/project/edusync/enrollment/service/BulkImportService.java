package com.project.edusync.enrollment.service;

import com.project.edusync.enrollment.model.dto.BulkImportReportDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BulkImportService {

    /**
     * Parses a CSV file and imports users based on the userType.
     *
     * @param file     The CSV file.
     * @param userType "students" or "staff".
     * @return A DTO containing the import results.
     * @throws IOException if there is an issue reading the file.
     * @throws IllegalArgumentException if userType is invalid.
     */
    BulkImportReportDTO importUsers(MultipartFile file, String userType) throws IOException, IllegalArgumentException;
}
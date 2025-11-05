package com.project.edusync.enrollment.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkImportReportDTO {
    private String status; // e.g., "COMPLETED", "FAILED"
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errorMessages = new ArrayList<>();

    // Constructor for simple failure cases
    public BulkImportReportDTO(String status, String errorMessage) {
        this.status = status;
        this.errorMessages.add(errorMessage);
    }

    // You can add a more detailed error DTO later if needed
    // e.g., List<RowErrorDTO> errors
}
package com.project.edusync.em.model.dto.ResponseDTO;

import com.project.edusync.em.model.enums.PastExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PastPaperResponseDTO {

    private UUID uuid;
    // Rich data for UI
    private UUID classId;
    private String className;
    private UUID subjectId;
    private String subjectName;

    private String title;
    private Integer examYear;
    private PastExamType examType;
    private String fileUrl;      // Publicly accessible URL
    private String fileMimeType;
    private Integer fileSizeKb;

    private LocalDateTime uploadedAt;
    private String uploadedBy;
}
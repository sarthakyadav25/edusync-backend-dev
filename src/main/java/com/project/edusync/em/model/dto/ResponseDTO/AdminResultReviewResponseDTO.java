package com.project.edusync.em.model.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResultReviewResponseDTO {
    private Long resultId;
    private Long answerSheetId;
    private Long scheduleId;
    private UUID studentId;
    private String studentName;
    private String enrollmentNumber;
    private UUID examId;
    private String examName;
    private UUID classId;
    private String className;
    private String subjectName;
    private BigDecimal totalMarks;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime publishedAt;
    private String approvedBy;
}


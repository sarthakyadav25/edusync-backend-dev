package com.project.edusync.em.model.dto.ResponseDTO;

import com.project.edusync.em.model.enums.EvaluationAssignmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EvaluationAssignmentResponseDTO {
    private Long assignmentId;
    private Long examScheduleId;
    private UUID examUuid;
    private String examName;
    private String subjectName;
    private LocalDate examDate;
    private UUID teacherId;
    private String teacherName;
    private String role;           // "UPLOADER" or "EVALUATOR"
    private EvaluationAssignmentStatus status;
    private String uploadStatus;   // null, "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    private LocalDateTime assignedAt;
    private LocalDate dueDate;
}

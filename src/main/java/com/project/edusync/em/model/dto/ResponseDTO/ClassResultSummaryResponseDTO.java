package com.project.edusync.em.model.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ClassResultSummaryResponseDTO {
    private UUID classId;
    private String className;
    private UUID examId;
    private String examName;
    private long totalStudents;
    private long evaluatedStudents;
    private long absentStudents;
    private long pendingStudents;
    private String status;
}

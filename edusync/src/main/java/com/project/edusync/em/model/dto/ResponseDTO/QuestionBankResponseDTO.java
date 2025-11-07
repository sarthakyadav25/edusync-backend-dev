package com.project.edusync.em.model.dto.ResponseDTO;

import com.project.edusync.em.model.enums.DifficultyLevel;
import com.project.edusync.em.model.enums.QuestionType;
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
public class QuestionBankResponseDTO {

    private UUID uuid;
    // Rich data for UI
    private UUID subjectId;
    private String subjectName;
    private UUID classId;
    private String className;

    private String topic;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private BigDecimal marks;

    private LocalDateTime createdAt;
    private String createdBy;
}
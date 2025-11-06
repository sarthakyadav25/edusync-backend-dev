package com.project.edusync.em.model.dto.RequestDTO;

import com.project.edusync.em.model.enums.DifficultyLevel;
import com.project.edusync.em.model.enums.QuestionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankRequestDTO {

    @NotNull(message = "Subject UUID is required")
    private UUID subjectId;

    @NotNull(message = "Class UUID is required")
    private UUID classId;

    @Size(max = 255, message = "Topic must be less than 255 characters")
    private String topic;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;

    @NotBlank(message = "Question text is required")
    private String questionText;

    // Options for MCQs (can be null for other types)
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer;

    @NotNull(message = "Marks are required")
    @DecimalMin(value = "0.5", message = "Marks must be at least 0.5")
    private BigDecimal marks;
}
package com.project.edusync.em.model.dto.RequestDTO;

import com.project.edusync.em.model.enums.PastExamType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PastPaperRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotNull(message = "Class UUID is required")
    private UUID classId;

    @NotNull(message = "Subject UUID is required")
    private UUID subjectId;

    @NotNull(message = "Exam year is required")
    @Min(value = 2000, message = "Exam year must be valid (e.g., 2000 or later)")
    private Integer examYear;

    private PastExamType examType; // Optional
}
package com.project.edusync.em.model.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeScaleResponseDTO {
    private Long gradeScaleId;
    // Useful to know which system this belongs to when fetching individually
    private String systemName;
    private String gradeName;
    private BigDecimal minPercentage;
    private BigDecimal maxPercentage;
    private BigDecimal gradePoints;
}
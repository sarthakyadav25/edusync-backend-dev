package com.project.edusync.adm.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating or updating an AcademicClass.
 * Contains only the fields a client is allowed to provide.
 */
@Data
public class AcademicClassRequestDto {

    @NotBlank(message = "Class name cannot be blank")
    private String name;

}
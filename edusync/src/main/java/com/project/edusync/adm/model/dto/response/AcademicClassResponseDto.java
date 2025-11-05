package com.project.edusync.adm.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/*
    Dto for sending response for academic class entity
    contains only data that can be sent to a client
 */

@Data
@Builder
public class AcademicClassResponseDto {
    private UUID classId;
    private String name;
    private Set<SectionResponseDto> sections;
}

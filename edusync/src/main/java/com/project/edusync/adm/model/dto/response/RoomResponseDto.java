package com.project.edusync.adm.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

/**
 * DTO for responding with Room information.
 */
@Data
@Builder
public class RoomResponseDto {

    private UUID uuid;
    private String name;
    private String roomType;

}
package com.project.edusync.adm.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating or updating a Room.
 */
@Data
public class RoomRequestDto {

    @NotBlank(message = "Room name cannot be blank")
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String roomType; // e.g., "Standard", "Lab", "Auditorium"
}
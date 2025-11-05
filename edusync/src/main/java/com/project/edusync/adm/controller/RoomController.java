package com.project.edusync.adm.controller;

import com.project.edusync.adm.model.dto.request.RoomRequestDto;
import com.project.edusync.adm.model.dto.response.RoomResponseDto;
import com.project.edusync.adm.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing Rooms.
 * All responses are wrapped in ResponseEntity for full control over the HTTP response.
 */
@RestController
@RequestMapping("${api.url}/auth") // Following your existing request mapping
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * Creates a new room (e.g., "Room 101").
     * HTTP 201 Created on success.
     */
    @PostMapping("/rooms")
    public ResponseEntity<RoomResponseDto> createRoom(
            @Valid @RequestBody RoomRequestDto requestDto) {

        RoomResponseDto createdRoom = roomService.addRoom(requestDto);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of all active rooms.
     * HTTP 200 OK on success.
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<RoomResponseDto> response = roomService.getAllRooms();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves details for a single room by its UUID.
     * HTTP 200 OK on success.
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoomById(
            @PathVariable UUID roomId) {

        RoomResponseDto response = roomService.getRoomById(roomId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates a room's details by its UUID.
     * HTTP 200 OK on success.
     */
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoomById(
            @PathVariable UUID roomId,
            @Valid @RequestBody RoomRequestDto roomRequestDto) {

        RoomResponseDto response = roomService.updateRoom(roomId, roomRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Soft deletes a room by its UUID.
     * HTTP 204 No Content on success.
     */
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
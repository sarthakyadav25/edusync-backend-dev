package com.project.edusync.adm.service.impl;

import com.project.edusync.adm.model.dto.request.RoomRequestDto;
import com.project.edusync.adm.model.dto.response.RoomResponseDto;
import com.project.edusync.adm.model.entity.Room;
import com.project.edusync.adm.repository.RoomRepository;
import com.project.edusync.adm.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public RoomResponseDto addRoom(RoomRequestDto roomRequestDto) {
        log.info("Attempting to create a new room with name: {}", roomRequestDto.getName());

        // Best Practice: Validate for uniqueness
        if (roomRepository.existsByName(roomRequestDto.getName())) {
            log.warn("Room creation failed. Name '{}' already exists.", roomRequestDto.getName());
            throw new RuntimeException("Room with name " + roomRequestDto.getName() + " already exists.");
        }

        Room newRoom = new Room();
        newRoom.setName(roomRequestDto.getName());
        newRoom.setRoomType(roomRequestDto.getRoomType());
        newRoom.setIsActive(true); // Explicitly set as active

        Room savedRoom = roomRepository.save(newRoom);
        log.info("Room '{}' created successfully with id {}", savedRoom.getName(), savedRoom.getUuid());

        return toRoomResponseDto(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> getAllRooms() {
        log.info("Fetching all active rooms");
        return roomRepository.findAll().stream()
                .filter(Room::getIsActive) // Only return active rooms
                .map(this::toRoomResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto getRoomById(UUID roomId) {
        log.info("Fetching room with id: {}", roomId);
        Room room = roomRepository.findActiveById(roomId)
                .orElseThrow(() -> {
                    log.warn("No active room with id {} found", roomId);
                    return new RuntimeException("No resource found with id: " + roomId);
                });
        return toRoomResponseDto(room);
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(UUID roomId, RoomRequestDto roomRequestDto) {
        log.info("Attempting to update room with id: {}", roomId);
        Room existingRoom = roomRepository.findActiveById(roomId)
                .orElseThrow(() -> {
                    log.warn("No active room with id {} to update", roomId);
                    return new RuntimeException("No resource found to update with id: " + roomId);
                });

        // Best Practice: Check uniqueness of name only if it's being changed
        if (!existingRoom.getName().equals(roomRequestDto.getName())) {
            if (roomRepository.existsByNameAndUuidNot(roomRequestDto.getName(), roomId)) {
                log.warn("Room update failed. Name '{}' already exists for another room.", roomRequestDto.getName());
                throw new RuntimeException("Room with name " + roomRequestDto.getName() + " already exists.");
            }
        }

        existingRoom.setName(roomRequestDto.getName());
        existingRoom.setRoomType(roomRequestDto.getRoomType());

        Room updatedRoom = roomRepository.save(existingRoom);
        log.info("Room with id {} updated successfully", updatedRoom.getUuid());

        return toRoomResponseDto(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(UUID roomId) {
        log.info("Attempting to soft delete room with id: {}", roomId);
        if (!roomRepository.existsActiveById(roomId)) {
            log.warn("Failed to delete. Room not found with id: {}", roomId);
            throw new RuntimeException("Room id: " + roomId + " not found.");
        }

        roomRepository.softDeleteById(roomId);
        log.info("Room with id {} marked as inactive successfully", roomId);
    }

    /**
     * Private helper to convert Room Entity to Response DTO using builder.
     */
    private RoomResponseDto toRoomResponseDto(Room entity) {
        if (entity == null) return null;
        return RoomResponseDto.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .roomType(entity.getRoomType())
                .build();
    }
}
package com.project.edusync.adm.service;

import com.project.edusync.adm.model.dto.request.RoomRequestDto;
import com.project.edusync.adm.model.dto.response.RoomResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomResponseDto addRoom(RoomRequestDto roomRequestDto);

    List<RoomResponseDto> getAllRooms();

    RoomResponseDto getRoomById(UUID roomId);

    RoomResponseDto updateRoom(UUID roomId, RoomRequestDto roomRequestDto);

    void deleteRoom(UUID roomId);
}
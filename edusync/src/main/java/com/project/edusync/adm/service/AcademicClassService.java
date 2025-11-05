package com.project.edusync.adm.service;

import com.project.edusync.adm.model.dto.request.AcademicClassRequestDto;
import com.project.edusync.adm.model.dto.response.AcademicClassResponseDto;
import com.project.edusync.adm.model.entity.Section;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface AcademicClassService {

    AcademicClassResponseDto addClass(AcademicClassRequestDto academicClassRequestDto);

    List<AcademicClassResponseDto> getAllClasses();

    AcademicClassResponseDto getClassById(UUID classId);

    AcademicClassResponseDto updateClass(UUID classId, AcademicClassRequestDto academicClassRequestDto);

    void deleteClass(UUID classId);
}

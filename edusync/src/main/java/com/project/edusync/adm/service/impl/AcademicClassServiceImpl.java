package com.project.edusync.adm.service.impl;

import com.project.edusync.adm.model.dto.request.AcademicClassRequestDto;
import com.project.edusync.adm.model.dto.response.AcademicClassResponseDto;
import com.project.edusync.adm.model.entity.AcademicClass;
import com.project.edusync.adm.repository.AcademicClassRepository;
import com.project.edusync.adm.service.AcademicClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcademicClassServiceImpl implements AcademicClassService {

    private final AcademicClassRepository academicClassRepository;


    @Override
    public AcademicClassResponseDto addClass(AcademicClassRequestDto academicClassRequestDto){
        log.info("Attempting to create a new class with name: {}", academicClassRequestDto.getName());
        AcademicClass newClass = new AcademicClass();
        newClass.setName(academicClassRequestDto.getName());
        AcademicClass savedClass = academicClassRepository.save(newClass);
        log.info("Class {} created successfully",savedClass.getName());

        return AcademicClassResponseDto.builder()
                .name(savedClass.getName())
                .build();
    }
}

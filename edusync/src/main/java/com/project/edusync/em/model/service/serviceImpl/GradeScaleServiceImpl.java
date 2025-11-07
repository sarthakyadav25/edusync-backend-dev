package com.project.edusync.em.model.service.serviceImpl;

import com.project.edusync.common.exception.emException.EdusyncException;
import com.project.edusync.em.model.dto.RequestDTO.GradeScaleRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.GradeScaleResponseDTO;
import com.project.edusync.em.model.entity.GradeScale;
import com.project.edusync.em.model.entity.GradeSystem;
import com.project.edusync.em.model.repository.GradeScaleRepository;
import com.project.edusync.em.model.repository.GradeSystemRepository;
import com.project.edusync.em.model.service.GradeScaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GradeScaleServiceImpl implements GradeScaleService {

    private final GradeScaleRepository gradeScaleRepository;
    private final GradeSystemRepository gradeSystemRepository;

    @Override
    public GradeScaleResponseDTO addScaleToSystem(UUID systemUuid, GradeScaleRequestDTO requestDTO) {
        log.info("Adding new grade scale '{}' to system UUID: {}", requestDTO.getGradeName(), systemUuid);

        GradeSystem system = gradeSystemRepository.findByUuid(systemUuid)
                .orElseThrow(() -> new EdusyncException("EM-404", "Grade System not found", HttpStatus.NOT_FOUND));

        // Validate against ALL existing scales in the system
        validateOverlap(requestDTO, system.getGradeScales());

        GradeScale scale = new GradeScale();
        mapDtoToEntity(requestDTO, scale);
        scale.setGradeSystem(system); // Link to parent

        GradeScale savedScale = gradeScaleRepository.save(scale);
        log.info("Grade scale added successfully with ID: {}", savedScale.getGradeScaleId());
        return toResponseDTO(savedScale);
    }

    @Override
    public GradeScaleResponseDTO updateGradeScale(Long scaleId, GradeScaleRequestDTO requestDTO) {
        log.info("Updating grade scale ID: {}", scaleId);

        GradeScale existingScale = gradeScaleRepository.findById(scaleId)
                .orElseThrow(() -> new EdusyncException("EM-404", "Grade Scale not found", HttpStatus.NOT_FOUND));

        // Get all sibling scales (excluding the one we are currently updating) to check for overlaps
        Set<GradeScale> siblingScales = existingScale.getGradeSystem().getGradeScales().stream()
                .filter(s -> !s.getGradeScaleId().equals(scaleId))
                .collect(Collectors.toSet());

        validateOverlap(requestDTO, siblingScales);

        mapDtoToEntity(requestDTO, existingScale);
        GradeScale updatedScale = gradeScaleRepository.save(existingScale);
        log.info("Grade scale ID {} updated successfully", scaleId);

        return toResponseDTO(updatedScale);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeScaleResponseDTO getGradeScaleById(Long scaleId) {
        log.info("Fetching grade scale ID: {}", scaleId);
        GradeScale scale = gradeScaleRepository.findById(scaleId)
                .orElseThrow(() -> new EdusyncException("EM-404", "Grade Scale not found", HttpStatus.NOT_FOUND));
        return toResponseDTO(scale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeScaleResponseDTO> getScalesBySystemUuid(UUID systemUuid) {
        log.info("Fetching all scales for system UUID: {}", systemUuid);
        GradeSystem system = gradeSystemRepository.findByUuid(systemUuid)
                .orElseThrow(() -> new EdusyncException("EM-404", "Grade System not found", HttpStatus.NOT_FOUND));

        return system.getGradeScales().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteGradeScale(Long scaleId) {
        log.info("Deleting grade scale ID: {}", scaleId);
        if (!gradeScaleRepository.existsById(scaleId)) {
            throw new EdusyncException("EM-404", "Grade Scale not found", HttpStatus.NOT_FOUND);
        }
        gradeScaleRepository.deleteById(scaleId);
    }

    // --- Helper Methods ---

    private void validateOverlap(GradeScaleRequestDTO newScale, Set<GradeScale> existingScales) {
        // Ensure min < max for the new scale itself
        if (newScale.getMinPercentage().compareTo(newScale.getMaxPercentage()) > 0) {
            log.error("Validation failed: Min percentage greater than max for {}", newScale.getGradeName());
            throw new EdusyncException("EM-400", "Minimum percentage cannot be greater than maximum percentage", HttpStatus.BAD_REQUEST);
        }

        for (GradeScale existing : existingScales) {
            // Overlap condition: (StartA <= EndB) and (EndA >= StartB)
            if (newScale.getMinPercentage().compareTo(existing.getMaxPercentage()) <= 0 &&
                    newScale.getMaxPercentage().compareTo(existing.getMinPercentage()) >= 0) {

                log.warn("Overlap detected: New [{}, {}] overlaps with existing '{}' [{}, {}]",
                        newScale.getMinPercentage(), newScale.getMaxPercentage(),
                        existing.getGradeName(), existing.getMinPercentage(), existing.getMaxPercentage());

                throw new EdusyncException("EM-409",
                        "Percentage range overlaps with existing grade: " + existing.getGradeName(),
                        HttpStatus.CONFLICT);
            }
        }
    }

    private void mapDtoToEntity(GradeScaleRequestDTO dto, GradeScale entity) {
        entity.setGradeName(dto.getGradeName());
        entity.setMinPercentage(dto.getMinPercentage());
        entity.setMaxPercentage(dto.getMaxPercentage());
        entity.setGradePoints(dto.getGradePoints());
    }

    private GradeScaleResponseDTO toResponseDTO(GradeScale entity) {
        return GradeScaleResponseDTO.builder()
                .gradeScaleId(entity.getGradeScaleId())
                .systemName(entity.getGradeSystem().getSystemName())
                .gradeName(entity.getGradeName())
                .minPercentage(entity.getMinPercentage())
                .maxPercentage(entity.getMaxPercentage())
                .gradePoints(entity.getGradePoints())
                .build();
    }
}
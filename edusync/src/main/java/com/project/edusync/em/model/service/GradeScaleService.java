package com.project.edusync.em.model.service;

import com.project.edusync.em.model.dto.RequestDTO.GradeScaleRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.GradeScaleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface GradeScaleService {

    /**
     * Adds a new scale rule to an existing grade system.
     */
    GradeScaleResponseDTO addScaleToSystem(UUID systemUuid, GradeScaleRequestDTO requestDTO);

    /**
     * Updates a specific grade scale rule.
     * Must validate that the new percentages do not overlap with sibling scales.
     */
    GradeScaleResponseDTO updateGradeScale(Long scaleId, GradeScaleRequestDTO requestDTO);

    /**
     * Fetches a single grade scale by ID.
     */
    GradeScaleResponseDTO getGradeScaleById(Long scaleId);

    /**
     * Fetches all scales for a given system UUID.
     */
    List<GradeScaleResponseDTO> getScalesBySystemUuid(UUID systemUuid);

    /**
     * Deletes a specific grade scale rule.
     */
    void deleteGradeScale(Long scaleId);
}
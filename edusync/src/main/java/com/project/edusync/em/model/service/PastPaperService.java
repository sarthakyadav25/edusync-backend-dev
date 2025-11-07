package com.project.edusync.em.model.service;

import com.project.edusync.em.model.dto.RequestDTO.PastPaperRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.PastPaperResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PastPaperService {

    /**
     * Uploads a new past paper file and saves its metadata.
     */
    PastPaperResponseDTO uploadPastPaper(PastPaperRequestDTO requestDTO, MultipartFile file);

    /**
     * Fetches a specific past paper by UUID.
     */
    PastPaperResponseDTO getPastPaperByUuid(UUID uuid);

    /**
     * Fetches all past papers, optionally filtered.
     */
    List<PastPaperResponseDTO> getAllPastPapers(UUID classId, UUID subjectId, Integer year);

    /**
     * Deletes a past paper (and should ideally delete the actual file too).
     */
    void deletePastPaper(UUID uuid);
}
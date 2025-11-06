package com.project.edusync.em.model.service;

import com.project.edusync.em.model.dto.RequestDTO.QuestionBankRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.QuestionBankResponseDTO;
import com.project.edusync.em.model.enums.DifficultyLevel;
import com.project.edusync.em.model.enums.QuestionType;

import java.util.List;
import java.util.UUID;

public interface QuestionBankService {

    QuestionBankResponseDTO createQuestion(QuestionBankRequestDTO requestDTO);

    QuestionBankResponseDTO updateQuestion(UUID uuid, QuestionBankRequestDTO requestDTO);

    QuestionBankResponseDTO getQuestionByUuid(UUID uuid);

    List<QuestionBankResponseDTO> getAllQuestions(
            UUID subjectId, UUID classId, String topic, QuestionType type, DifficultyLevel difficulty);

    void deleteQuestion(UUID uuid);
}
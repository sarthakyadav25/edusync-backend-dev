package com.project.edusync.em.model.service.serviceImpl;

import com.project.edusync.common.exception.emException.EdusyncException;
import com.project.edusync.em.model.dto.RequestDTO.PaperQuestionMapRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.PaperQuestionMapResponseDTO;
import com.project.edusync.em.model.entity.PaperQuestionMap;
import com.project.edusync.em.model.entity.QuestionBank;
import com.project.edusync.em.model.entity.QuestionPaper;
import com.project.edusync.em.model.repository.PaperQuestionMapRepository;
import com.project.edusync.em.model.repository.QuestionBankRepository;
import com.project.edusync.em.model.repository.QuestionPaperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaperQuestionMapServiceImpl {

    private final PaperQuestionMapRepository paperQuestionMapRepository;
    private final QuestionPaperRepository questionPaperRepository;
    private final QuestionBankRepository questionBankRepository;

    /**
     * Adds a single question to an existing paper.
     * Uses paperId from the URL and question details from the DTO.
     */
    public PaperQuestionMapResponseDTO addMapping(Long paperId, PaperQuestionMapRequestDTO requestDTO) {
        log.info("Adding question UUID {} to paper ID {}", requestDTO.getQuestionUuid(), paperId);

        QuestionPaper paper = questionPaperRepository.findById(paperId)
                .orElseThrow(() -> new EdusyncException("EM-404", "Question Paper not found", HttpStatus.NOT_FOUND));

        QuestionBank question = questionBankRepository.findByUuid(requestDTO.getQuestionUuid())
                .orElseThrow(() -> new EdusyncException("EM-404", "Question not found", HttpStatus.NOT_FOUND));

        PaperQuestionMap mapping = new PaperQuestionMap();
        mapping.setQuestionPaper(paper);
        mapping.setQuestion(question);
        mapping.setQuestionNumber(requestDTO.getQuestionNumber());
        mapping.setMarksForQuestion(requestDTO.getMarksForQuestion());

        // Note: In a real production scenario, you might want to trigger a recalculation
        // of the parent paper's 'totalMarks' here to keep it in sync.

        PaperQuestionMap savedMapping = paperQuestionMapRepository.save(mapping);
        log.info("Mapping created with ID: {}", savedMapping.getPaperQuestionId());
        return toResponseDTO(savedMapping);
    }

    /**
     * Updates an existing mapping (e.g., changing marks or swapping the question).
     */
    public PaperQuestionMapResponseDTO updateMapping(Long mappingId, PaperQuestionMapRequestDTO requestDTO) {
        log.info("Updating paper-question mapping ID: {}", mappingId);

        PaperQuestionMap mapping = paperQuestionMapRepository.findById(mappingId)
                .orElseThrow(() -> new EdusyncException("EM-404", "Mapping not found", HttpStatus.NOT_FOUND));

        // Detect if the user is swapping the question for a different one
        if (!mapping.getQuestion().getUuid().equals(requestDTO.getQuestionUuid())) {
            log.info("Swapping question in mapping {} from {} to {}",
                    mappingId, mapping.getQuestion().getUuid(), requestDTO.getQuestionUuid());

            QuestionBank newQuestion = questionBankRepository.findByUuid(requestDTO.getQuestionUuid())
                    .orElseThrow(() -> new EdusyncException("EM-404", "New question not found", HttpStatus.NOT_FOUND));
            mapping.setQuestion(newQuestion);
        }

        mapping.setQuestionNumber(requestDTO.getQuestionNumber());
        mapping.setMarksForQuestion(requestDTO.getMarksForQuestion());

        PaperQuestionMap updatedMapping = paperQuestionMapRepository.save(mapping);
        log.info("Mapping ID {} updated successfully", mappingId);
        return toResponseDTO(updatedMapping);
    }

    /**
     * Removes a question from a paper.
     */
    public void deleteMapping(Long mappingId) {
        log.info("Deleting paper-question mapping ID: {}", mappingId);
        if (!paperQuestionMapRepository.existsById(mappingId)) {
            throw new EdusyncException("EM-404", "Mapping not found", HttpStatus.NOT_FOUND);
        }
        paperQuestionMapRepository.deleteById(mappingId);
    }

    private PaperQuestionMapResponseDTO toResponseDTO(PaperQuestionMap entity) {
        // Provide a short snippet of the question text for UI convenience
        String snippet = entity.getQuestion().getQuestionText();
        if (snippet != null && snippet.length() > 50) {
            snippet = snippet.substring(0, 50) + "...";
        }

        return PaperQuestionMapResponseDTO.builder()
                .paperQuestionId(entity.getPaperQuestionId())
                //.paperId(entity.getQuestionPaper().getId()) // Optional: if you need parent ID in response
                .questionUuid(entity.getQuestion().getUuid())
                .questionText(snippet) // Mapped to 'questionSnippet' or 'questionText' in your DTO
                .questionNumber(entity.getQuestionNumber())
                .marksForQuestion(entity.getMarksForQuestion())
                .build();
    }
}
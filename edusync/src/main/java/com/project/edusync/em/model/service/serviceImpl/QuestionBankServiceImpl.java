package com.project.edusync.em.model.service.serviceImpl;

import com.project.edusync.adm.model.entity.AcademicClass;
import com.project.edusync.adm.model.entity.Subject;
import com.project.edusync.adm.repository.AcademicClassRepository;
import com.project.edusync.adm.repository.SubjectRepository;
import com.project.edusync.common.exception.emException.EdusyncException;
import com.project.edusync.em.model.dto.RequestDTO.QuestionBankRequestDTO;
import com.project.edusync.em.model.dto.ResponseDTO.QuestionBankResponseDTO;
import com.project.edusync.em.model.entity.QuestionBank;
import com.project.edusync.em.model.enums.DifficultyLevel;
import com.project.edusync.em.model.enums.QuestionType;
import com.project.edusync.em.model.repository.QuestionBankRepository;
import com.project.edusync.em.model.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicClassRepository academicClassRepository;

    @Override
    public QuestionBankResponseDTO createQuestion(QuestionBankRequestDTO requestDTO) {
        log.info("Creating new question for subject UUID: {}", requestDTO.getSubjectId());

        validateMcqOptions(requestDTO);

        QuestionBank question = new QuestionBank();
        mapDtoToEntity(requestDTO, question);

        QuestionBank savedQuestion = questionBankRepository.save(question);
        log.info("Question created successfully with UUID: {}", savedQuestion.getUuid());
        return toResponseDTO(savedQuestion);
    }

    @Override
    public QuestionBankResponseDTO updateQuestion(UUID uuid, QuestionBankRequestDTO requestDTO) {
        log.info("Updating question UUID: {}", uuid);

        QuestionBank existingQuestion = questionBankRepository.findByUuid(uuid)
                .orElseThrow(() -> new EdusyncException("EM-404", "Question not found", HttpStatus.NOT_FOUND));

        validateMcqOptions(requestDTO);
        mapDtoToEntity(requestDTO, existingQuestion);

        QuestionBank updatedQuestion = questionBankRepository.save(existingQuestion);
        return toResponseDTO(updatedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionBankResponseDTO getQuestionByUuid(UUID uuid) {
        QuestionBank question = questionBankRepository.findByUuid(uuid)
                .orElseThrow(() -> new EdusyncException("EM-404", "Question not found", HttpStatus.NOT_FOUND));
        return toResponseDTO(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionBankResponseDTO> getAllQuestions(
            UUID subjectId, UUID classId, String topic, QuestionType type, DifficultyLevel difficulty) {

        log.info("Fetching questions with filters - Subject: {}, Class: {}, Topic: {}", subjectId, classId, topic);

        return questionBankRepository.findAllByFilters(subjectId, classId, topic, type, difficulty)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuestion(UUID uuid) {
        log.info("Deleting question UUID: {}", uuid);
        QuestionBank question = questionBankRepository.findByUuid(uuid)
                .orElseThrow(() -> new EdusyncException("EM-404", "Question not found", HttpStatus.NOT_FOUND));

        // Optional: Check if question is already used in any papers before hard deleting.
        // If cascade is set, it will delete mappings automatically, which might be desired or dangerous depending on policy.

        questionBankRepository.delete(question);
    }

    // --- Helper Methods ---

    private void validateMcqOptions(QuestionBankRequestDTO dto) {
        if (dto.getQuestionType() == QuestionType.MCQ) {
            if (dto.getOptionA() == null || dto.getOptionB() == null) {
                throw new EdusyncException("EM-400", "MCQs must have at least Option A and Option B", HttpStatus.BAD_REQUEST);
            }
            if (dto.getCorrectAnswer() == null || dto.getCorrectAnswer().isBlank()) {
                throw new EdusyncException("EM-400", "MCQs must have a correct answer specified", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void mapDtoToEntity(QuestionBankRequestDTO dto, QuestionBank entity) {
        Subject subject = subjectRepository.findActiveById(dto.getSubjectId())
                .orElseThrow(() -> new EdusyncException("ADM-404", "Subject not found", HttpStatus.NOT_FOUND));
        entity.setSubject(subject);

        AcademicClass academicClass = academicClassRepository.findById(dto.getClassId())
                .orElseThrow(() -> new EdusyncException("ADM-404", "Class not found", HttpStatus.NOT_FOUND));
        entity.setAcademicClass(academicClass);

        entity.setTopic(dto.getTopic());
        entity.setQuestionType(dto.getQuestionType());
        entity.setDifficultyLevel(dto.getDifficultyLevel());
        entity.setQuestionText(dto.getQuestionText());
        entity.setMarks(dto.getMarks());
        entity.setCorrectAnswer(dto.getCorrectAnswer());

        // Only set options if it's an MCQ to keep DB clean, or always set them if you prefer.
        if (dto.getQuestionType() == QuestionType.MCQ) {
            entity.setOptionA(dto.getOptionA());
            entity.setOptionB(dto.getOptionB());
            entity.setOptionC(dto.getOptionC());
            entity.setOptionD(dto.getOptionD());
        } else {
            entity.setOptionA(null);
            entity.setOptionB(null);
            entity.setOptionC(null);
            entity.setOptionD(null);
        }
    }

    private QuestionBankResponseDTO toResponseDTO(QuestionBank entity) {
        return QuestionBankResponseDTO.builder()
                .uuid(entity.getUuid())
                .subjectId(entity.getSubject().getUuid())
                .subjectName(entity.getSubject().getName())
                .classId(entity.getAcademicClass().getUuid())
                .className(entity.getAcademicClass().getName())
                .topic(entity.getTopic())
                .questionType(entity.getQuestionType())
                .difficultyLevel(entity.getDifficultyLevel())
                .questionText(entity.getQuestionText())
                .optionA(entity.getOptionA())
                .optionB(entity.getOptionB())
                .optionC(entity.getOptionC())
                .optionD(entity.getOptionD())
                .correctAnswer(entity.getCorrectAnswer())
                .marks(entity.getMarks())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
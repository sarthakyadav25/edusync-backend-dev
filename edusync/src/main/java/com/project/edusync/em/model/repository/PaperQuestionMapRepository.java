package com.project.edusync.em.model.repository;

import com.project.edusync.em.model.entity.PaperQuestionMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperQuestionMapRepository extends JpaRepository<PaperQuestionMap, Long> {
    List<PaperQuestionMap> findByQuestionPaper_Id(Long paperId);
}

package com.project.edusync.adm.repository;

import com.project.edusync.adm.model.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT s FROM Section s JOIN s.academicClass ac WHERE ac.name = :className AND s.sectionName = :sectionName")
    Optional<Section> findByAcademicClass_NameAndSectionName(String className, String sectionName);
}

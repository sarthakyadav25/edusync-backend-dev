package com.project.edusync.em.model.repository;

import com.project.edusync.em.model.entity.StudentExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentExamStatusRepository extends JpaRepository<StudentExamStatus, Long> {

    Optional<StudentExamStatus> findByStudentIdAndExamScheduleId(Long studentId, Long scheduleId);

    @Query("""
            SELECT count(s.id) FROM StudentExamStatus s
            JOIN s.examSchedule es
            WHERE es.academicClass.uuid = :classId
              AND es.exam.uuid = :examId
              AND s.isAbsent = true
            """)
    long countAbsentStudentsByClassAndExam(@Param("classId") UUID classId, @Param("examId") UUID examId);
}

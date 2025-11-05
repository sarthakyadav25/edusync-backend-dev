package com.project.edusync.uis.repository.medical;

import com.project.edusync.uis.model.entity.medical.StudentMedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentMedicalRecordRepository extends JpaRepository<StudentMedicalRecord, Integer> {
}

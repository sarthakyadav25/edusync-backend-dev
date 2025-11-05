package com.project.edusync.uis.repository.medical;

import com.project.edusync.uis.model.entity.medical.StudentMedicalAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentMedicalMedicationRepository extends JpaRepository<StudentMedicalAllergy, Integer> {
}

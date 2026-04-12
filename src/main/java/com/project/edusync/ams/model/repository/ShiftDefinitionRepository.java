package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.ShiftDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftDefinitionRepository extends JpaRepository<ShiftDefinition, Long> {
    Optional<ShiftDefinition> findByUuid(UUID uuid);

    Optional<ShiftDefinition> findFirstByIsDefaultTrueAndActiveTrueOrderByIdAsc();

    List<ShiftDefinition> findByActiveTrueOrderByShiftNameAsc();

    boolean existsByIsDefaultTrueAndActiveTrueAndIdNot(Long id);

    List<ShiftDefinition> findByIsDefaultTrueAndActiveTrueAndIdNot(Long id);
}


package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.StaffShiftMapping;
import com.project.edusync.uis.model.enums.StaffCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffShiftMappingRepository extends JpaRepository<StaffShiftMapping, Long> {

    Optional<StaffShiftMapping> findByUuid(UUID uuid);

    boolean existsByShift_IdAndEffectiveToIsNull(Long shiftId);

    Optional<StaffShiftMapping> findTopByStaff_IdAndEffectiveToOrderByEffectiveFromDesc(Long staffId, LocalDate effectiveTo);

    @Query("""
            SELECT m FROM StaffShiftMapping m
            WHERE m.staff.id = :staffId
              AND m.effectiveFrom <= :onDate
              AND (m.effectiveTo IS NULL OR m.effectiveTo >= :onDate)
            ORDER BY m.effectiveFrom DESC
            """)
    List<StaffShiftMapping> findCurrentMappingsByStaffId(@Param("staffId") Long staffId, @Param("onDate") LocalDate onDate);

    @Query("""
            SELECT m FROM StaffShiftMapping m
            JOIN FETCH m.shift s
            WHERE m.staff.id IN :staffIds
              AND m.effectiveFrom <= :onDate
              AND (m.effectiveTo IS NULL OR m.effectiveTo >= :onDate)
            ORDER BY m.staff.id ASC, m.effectiveFrom DESC
            """)
    List<StaffShiftMapping> findCurrentMappingsByStaffIds(@Param("staffIds") List<Long> staffIds, @Param("onDate") LocalDate onDate);

    @Query("""
            SELECT m FROM StaffShiftMapping m
            JOIN FETCH m.shift s
            WHERE m.staff.id IN :staffIds
              AND m.effectiveFrom <= :toDate
              AND (m.effectiveTo IS NULL OR m.effectiveTo >= :fromDate)
            ORDER BY m.staff.id ASC, m.effectiveFrom DESC
            """)
    List<StaffShiftMapping> findMappingsOverlappingDateRange(
            @Param("staffIds") List<Long> staffIds,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT m FROM StaffShiftMapping m
            WHERE (:shiftUuid IS NULL OR m.shift.uuid = :shiftUuid)
              AND (:category IS NULL OR m.staff.category = :category)
              AND m.effectiveTo IS NULL
            """)
    Page<StaffShiftMapping> findCurrentMappings(
            @Param("shiftUuid") UUID shiftUuid,
            @Param("category") StaffCategory category,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(m) > 0 FROM StaffShiftMapping m
            WHERE m.staff.id = :staffId
              AND m.shift.id = :shiftId
              AND m.effectiveFrom = :effectiveFrom
            """)
    boolean existsForSameStartDate(
            @Param("staffId") Long staffId,
            @Param("shiftId") Long shiftId,
            @Param("effectiveFrom") LocalDate effectiveFrom
    );
}


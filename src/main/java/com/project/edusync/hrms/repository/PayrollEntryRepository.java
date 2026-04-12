package com.project.edusync.hrms.repository;

import com.project.edusync.hrms.model.entity.PayrollEntry;
import com.project.edusync.hrms.model.enums.PayrollRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, Long> {

    List<PayrollEntry> findByPayrollRun_IdAndActiveTrue(Long runId);

    List<PayrollEntry> findByPayrollRun_IdAndActiveTrueOrderByStaff_IdAsc(Long runId);

    @Query("""
            SELECT COUNT(e) > 0
            FROM PayrollEntry e
            WHERE e.active = true
              AND e.staff.id = :staffId
              AND e.payrollRun.active = true
              AND e.payrollRun.status IN :statuses
              AND ((e.payrollRun.payYear * 100) + e.payrollRun.payMonth) BETWEEN :fromYearMonth AND :toYearMonth
            """)
    boolean existsLockedPayrollForStaffAndPeriod(
            @Param("staffId") Long staffId,
            @Param("fromYearMonth") int fromYearMonth,
            @Param("toYearMonth") int toYearMonth,
            @Param("statuses") Collection<PayrollRunStatus> statuses
    );
}



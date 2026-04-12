package com.project.edusync.ams.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "shift_definition")
@Getter
@Setter
@NoArgsConstructor
public class ShiftDefinition extends AuditableEntity {

    @Column(name = "shift_name", nullable = false, length = 100)
    private String shiftName;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "grace_minutes", nullable = false)
    private Integer graceMinutes = 0;

    @Column(name = "applicable_days", nullable = false, columnDefinition = "TEXT")
    private String applicableDays;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}


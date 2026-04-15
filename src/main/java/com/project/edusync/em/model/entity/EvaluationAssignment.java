package com.project.edusync.em.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.em.model.enums.EvaluationAssignmentRole;
import com.project.edusync.em.model.enums.EvaluationAssignmentStatus;
import com.project.edusync.em.model.enums.UploadStatus;
import com.project.edusync.uis.model.entity.Staff;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_assignments", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_eval_assign_schedule_teacher_role",
                columnNames = {"schedule_id", "teacher_id", "role"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "assignment_id"))
public class EvaluationAssignment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ExamSchedule examSchedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Staff teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EvaluationAssignmentRole role = EvaluationAssignmentRole.EVALUATOR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EvaluationAssignmentStatus status = EvaluationAssignmentStatus.ASSIGNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", length = 20)
    private UploadStatus uploadStatus;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @PrePersist
    public void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}

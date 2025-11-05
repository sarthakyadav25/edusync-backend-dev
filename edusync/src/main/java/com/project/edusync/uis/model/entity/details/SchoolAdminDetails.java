package com.project.edusync.uis.model.entity.details;

import com.project.edusync.uis.model.entity.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "school_admin_details")
public class SchoolAdminDetails {

    @Id
    private Long id; // This is both PK and FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "staff_id")
    private Staff staff;

    // Example field specific to a School Admin
    @Column(name = "management_scope", length = 100)
    private String managementScope; // e.g., "Full School", "Academics", "Operations"
}
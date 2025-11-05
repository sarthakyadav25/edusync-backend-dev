package com.project.edusync.uis.model.entity;

import com.project.edusync.iam.model.entity.User;
import com.project.edusync.uis.model.enums.PlatformAccessLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "super_admin_details")
public class SuperAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This is both PK and FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // Field specific to a Super Admin
    @Enumerated(EnumType.STRING)
    @Column(name = "platform_access_level", length = 50)
    private PlatformAccessLevel platformAccessLevel; // e.g., "GLOBAL_ADMIN", "SCHOOL_SUPPORT"
}
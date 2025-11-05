package com.project.edusync.adm.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a physical room where classes are held.
 *
 * This entity extends AuditableEntity to gain ID (Long), UUID,
 * and audit timestamp fields.
 *
 * Relationships will be joined using the inherited 'id' (Long) primary key.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"schedules"}) // Exclude relationships
@ToString(callSuper = true, exclude = {"schedules"}) // Exclude lazy relationships
@Entity
@Table(name = "rooms")
public class Room extends AuditableEntity {

    // The @Id (Long id) and 'uuid' are inherited from AuditableEntity.

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "room_type", length = 100)
    private String roomType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Relationships ---

    /**
     * All schedule entries that are assigned to this room.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();

}

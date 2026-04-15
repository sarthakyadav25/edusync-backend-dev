package com.project.edusync.em.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed information about a single occupied position on a seat.
 * Used within SeatAvailabilityDTO to show who occupies each slot.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupiedSlotDTO {

    /** Zero-based position index on the seat (0=LEFT, 1=MIDDLE, 2=RIGHT) */
    private int positionIndex;

    /** Human-readable label: LEFT, MIDDLE, RIGHT, etc. */
    private String positionLabel;

    /** Subject name for this allocation */
    private String subjectName;

    /** Class name for this allocation */
    private String className;

    /** Full name of the student occupying this slot */
    private String studentName;
}

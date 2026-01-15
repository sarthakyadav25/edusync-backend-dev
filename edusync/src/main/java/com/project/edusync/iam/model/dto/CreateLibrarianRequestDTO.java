package com.project.edusync.iam.model.dto;

import com.project.edusync.uis.model.enums.StaffType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateLibrarianRequestDTO extends BaseStaffRequestDTO {

    private List<String> librarySystemPermissions;
    private boolean hasMlisDegree;

    @Override
    public StaffType getStaffType() {
        return StaffType.LIBRARIAN;
    }
}
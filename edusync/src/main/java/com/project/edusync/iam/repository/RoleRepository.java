package com.project.edusync.iam.repository;

import com.project.edusync.iam.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Finds a Role entity by its unique name (e.g., "SCHOOL_ADMIN", "STUDENT").
     * Spring Data JPA automatically generates the SQL query:
     * SELECT * FROM roles WHERE name = ?
     */
    Optional<Role> findByName(String name);
}

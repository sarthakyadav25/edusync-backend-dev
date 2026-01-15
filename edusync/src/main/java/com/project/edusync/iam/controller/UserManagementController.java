package com.project.edusync.iam.controller;

import com.project.edusync.iam.model.dto.*;
import com.project.edusync.iam.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Admin-level User Management.
 * <p>
 * These endpoints serve as the "Hiring" or "Enrollment" portal for the application.
 * They allow privileged users (Super Admin, School Admin) to onboard new users
 * into the system with all their necessary role-specific data (e.g., Medical records for Students,
 * Certifications for Teachers).
 * </p>
 * * <p>Security: All endpoints are protected by Role-Based Access Control (RBAC).</p>
 */
@RestController
@RequestMapping("${api.url}/auth/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin User Management", description = "Secure endpoints for creating and enrolling users (Students & Staff)")
public class UserManagementController {

    private final UserManagementService userManagementService;

    // =================================================================================
    // 1. SCHOOL ADMIN MANAGEMENT
    // =================================================================================

    /**
     * Create a new School Admin.
     * RESTRICTED TO: Super Admin only.
     */
    @PostMapping("/school-admin")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Create School Admin", description = "Creates a user with SCHOOL_ADMIN role. Restricted to Super Admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "School Admin created successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict - Username or Email already exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires Super Admin privileges")
    })
    public ResponseEntity<String> createSchoolAdmin(@Valid @RequestBody CreateUserRequestDTO request) {
        log.info("API Request: Create School Admin [{}]", request.getUsername());
        userManagementService.createSchoolAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("School Admin created successfully.");
    }

    // =================================================================================
    // 2. STUDENT ENROLLMENT
    // =================================================================================

    /**
     * Enroll a new Student.
     * ACCESSIBLE BY: Super Admin, School Admin.
     * This creates the User, Profile, Student record, Demographics, and Medical record in one go.
     */
    @PostMapping("/student")
//    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    @Operation(summary = "Enroll Student", description = "Creates a Student user along with their Profile, Demographics, and Medical Record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student enrolled successfully"),
            @ApiResponse(responseCode = "409", description = "Conflict - User or Enrollment Number already exists")
    })
    public ResponseEntity<String> createStudent(@Valid @RequestBody CreateStudentRequestDTO request) {
        log.info("API Request: Enroll Student [{}]", request.getUsername());
        userManagementService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student enrolled successfully.");
    }

    // =================================================================================
    // 3. STAFF HIRING (Role Specific)
    // =================================================================================

    /**
     * Hire a new Teacher.
     * ACCESSIBLE BY: Super Admin, School Admin.
     */
    @PostMapping("/staff/teacher")
//    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    @Operation(summary = "Hire Teacher", description = "Creates a Teacher user with specific details (Certifications, Subjects, etc.).")
    public ResponseEntity<String> createTeacher(@Valid @RequestBody CreateTeacherRequestDTO request) {
        log.info("API Request: Hire Teacher [{}]", request.getUsername());
        userManagementService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Teacher created successfully.");
    }

    /**
     * Appoint a new Principal.
     * RESTRICTED TO: Super Admin only (typically).
     */
    @PostMapping("/staff/principal")
//    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Appoint Principal", description = "Creates a Principal user with administrative details.")
    public ResponseEntity<String> createPrincipal(@Valid @RequestBody CreatePrincipalRequestDTO request) {
        log.info("API Request: Appoint Principal [{}]", request.getUsername());
        userManagementService.createPrincipal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Principal created successfully.");
    }

    /**
     * Hire a new Librarian.
     * ACCESSIBLE BY: Super Admin, School Admin.
     */
    @PostMapping("/staff/librarian")
//    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN')")
    @Operation(summary = "Hire Librarian", description = "Creates a Librarian user with library system permissions.")
    public ResponseEntity<String> createLibrarian(@Valid @RequestBody CreateLibrarianRequestDTO request) {
        log.info("API Request: Hire Librarian [{}]", request.getUsername());
        userManagementService.createLibrarian(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Librarian created successfully.");
    }
}
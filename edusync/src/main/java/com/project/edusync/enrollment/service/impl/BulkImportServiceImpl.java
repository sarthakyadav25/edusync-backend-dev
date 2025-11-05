package com.project.edusync.enrollment.service.impl;

import com.project.edusync.adm.model.entity.Section;
import com.project.edusync.adm.repository.SectionRepository;
import com.project.edusync.enrollment.model.dto.BulkImportReportDTO;
import com.project.edusync.enrollment.service.BulkImportService;
import com.project.edusync.enrollment.util.CsvValidationHelper; // <-- IMPORTED
import com.project.edusync.iam.model.entity.Role;
import com.project.edusync.iam.model.entity.User;
import com.project.edusync.iam.repository.RoleRepository;
import com.project.edusync.iam.repository.UserRepository;
import com.project.edusync.uis.model.entity.Staff;
import com.project.edusync.uis.model.entity.Student;
import com.project.edusync.uis.model.entity.UserProfile;
import com.project.edusync.uis.model.enums.Department;
import com.project.edusync.uis.model.enums.Gender;
import com.project.edusync.uis.model.enums.StaffType;
import com.project.edusync.uis.repository.StaffRepository;
import com.project.edusync.uis.repository.StudentRepository;
import com.project.edusync.uis.repository.UserProfileRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Collections;

/**
 * Service implementation for bulk user import via CSV.
 * Orchestrates parsing, validation, and transactional saving of users.
 * Delegates low-level parsing validation to CsvValidationHelper.
 */
@Service
@Slf4j
@RequiredArgsConstructor // <-- THIS is the Lombok annotation for constructor injection
public class BulkImportServiceImpl implements BulkImportService {

    // --- Constants ---
    private static final String USER_TYPE_STUDENTS = "students";
    private static final String USER_TYPE_STAFF = "staff";
    private static final String ROLE_STUDENT = "ROLE_STUDENT";

    @Value("${edusync.bulk-import.default-password:Welcome@123}")
    private String DEFAULT_PASSWORD;

    // --- Repositories & Services (all final) ---
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final CsvValidationHelper validationHelper;

    @Override
    public BulkImportReportDTO importUsers(MultipartFile file, String userType) throws IOException {
        // ... (this orchestration method remains unchanged)
        BulkImportReportDTO report = new BulkImportReportDTO();
        report.setStatus("PROCESSING");

        int rowNumber = 1; // Start at 1 for the header
        int successCount = 0;
        int failureCount = 0;

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext(); // Read and skip header
            if (header == null) {
                throw new IllegalArgumentException("File is empty or header is missing.");
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    if (USER_TYPE_STUDENTS.equalsIgnoreCase(userType)) {
                        processStudentRow(row);
                        successCount++;
                    } else if (USER_TYPE_STAFF.equalsIgnoreCase(userType)) {
                        processStaffRow(row);
                        successCount++;
                    } else {
                        throw new IllegalArgumentException("Invalid userType: " + userType);
                    }
                } catch (Exception e) {
                    failureCount++;
                    String error = String.format("Row %d: %s", rowNumber, e.getMessage());
                    report.getErrorMessages().add(error);
                    log.warn("Failed to process row {}: {}", rowNumber, e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            report.setStatus("FAILED");
            report.getErrorMessages().add("File is not a valid CSV: " + e.getMessage());
            return report;
        }

        report.setStatus("COMPLETED");
        report.setTotalRows(rowNumber - 1);
        report.setSuccessCount(successCount);
        report.setFailureCount(failureCount);
        return report;
    }


    @Transactional(rollbackFor = Exception.class)
    public void processStudentRow(String[] row) throws Exception {
        // 1. --- Parse & Validate Data (per students.csv spec) ---
        // All calls are now delegated to the helper
        String firstName = validationHelper.validateString(row[0], "firstName");
        String lastName = validationHelper.validateString(row[1], "lastName");
        String email = validationHelper.validateEmail(row[3]);
        LocalDate dob = validationHelper.parseDate(row[4], "dateOfBirth");
        Gender gender = validationHelper.parseEnum(Gender.class, row[5], "gender");
        String enrollmentNumber = validationHelper.validateString(row[6], "enrollmentNumber");
        LocalDate enrollmentDate = validationHelper.parseDate(row[7], "enrollmentDate");
        String className = validationHelper.validateString(row[8], "className");
        String sectionName = validationHelper.validateString(row[9], "sectionName");

        // 2. --- Validate Business Logic & Foreign Keys ---
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists.");
        }
        if (studentRepository.existsByEnrollmentNumber(enrollmentNumber)) {
            throw new IllegalArgumentException("Student with enrollment number '" + enrollmentNumber + "' already exists.");
        }

        Section section = sectionRepository.findByAcademicClass_NameAndSectionName(className, sectionName)
                .orElseThrow(() -> new IllegalArgumentException("Section not found for class '" + className + "' and section '" + sectionName + "'."));

        Role studentRole = roleRepository.findByName(ROLE_STUDENT);
        if(studentRole == null) {
                throw new RuntimeException("CRITICAL: " + ROLE_STUDENT + " not found in database.");
        }

        // 3. --- Create Entities ---
        User user = new User();
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setActive(true);
        user.setRoles(Collections.singleton(studentRole));

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setMiddleName(row[2]); // Optional field
        userProfile.setDateOfBirth(dob);
        userProfile.setGender(gender);
        userProfile.setUser(user);

        Student student = new Student();
        student.setEnrollmentNumber(enrollmentNumber);
        student.setEnrollmentDate(enrollmentDate);
        student.setActive(true);
        student.setUserProfile(userProfile);
        student.setSection(section);

        // 4. --- Save Entities ---
        userRepository.save(user);
        userProfileRepository.save(userProfile);
        studentRepository.save(student);

        log.info("Successfully created student: {}", email);
    }


    @Transactional(rollbackFor = Exception.class)
    public void processStaffRow(String[] row) throws Exception {
        // 1. --- Parse & Validate Data (per staff.csv spec) ---
        // All calls are now delegated to the helper
        String firstName = validationHelper.validateString(row[0], "firstName");
        String lastName = validationHelper.validateString(row[1], "lastName");
        String email = validationHelper.validateEmail(row[3]);
        LocalDate dob = validationHelper.parseDate(row[4], "dateOfBirth");
        Gender gender = validationHelper.parseEnum(Gender.class, row[5], "gender");
        String employeeId = validationHelper.validateString(row[6], "employeeId");
        LocalDate joiningDate = validationHelper.parseDate(row[7], "joiningDate");
        String jobTitle = validationHelper.validateString(row[8], "jobTitle");
        String department = validationHelper.validateString(row[9], "department");
        StaffType staffType = validationHelper.parseEnum(StaffType.class, row[10], "staffType");

        // 2. --- Validate Business Logic & Foreign Keys ---
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists.");
        }
        if (staffRepository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("Staff with employee ID '" + employeeId + "' already exists.");
        }

        String roleName = "ROLE_" + staffType.name();
        Role staffRole = roleRepository.findByName(roleName);
        if(staffRole == null) {
            throw new RuntimeException("CRITICAL: " + ROLE_STUDENT + " not found in database.");
        }

        // 3. --- Create Entities ---
        User user = new User();
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setActive(true);
        user.setRoles(Collections.singleton(staffRole));

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setMiddleName(row[2]); // Optional
        userProfile.setDateOfBirth(dob);
        userProfile.setGender(gender);
        userProfile.setUser(user);

        Staff staff = new Staff();
        staff.setEmployeeId(employeeId);
        staff.setHireDate(joiningDate);
        staff.setJobTitle(jobTitle);
        staff.setDepartment(Department.valueOf(department));
        staff.setStaffType(staffType);
        staff.setActive(true);
        staff.setUserProfile(userProfile);

        // 4. --- Save Entities ---
        userRepository.save(user);
        userProfileRepository.save(userProfile);
        staffRepository.save(staff);

        log.info("Successfully created staff: {}", email);
    }

    // --- All private helper methods have been moved to CsvValidationHelper ---
}
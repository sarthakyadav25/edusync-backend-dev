package com.project.edusync.enrollment.service.impl;

import com.project.edusync.adm.model.entity.Section;
import com.project.edusync.adm.repository.SectionRepository;
import com.project.edusync.enrollment.util.CsvValidationHelper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Service-layer unit test for BulkImportServiceImpl.
 * This test isolates the service and mocks all external dependencies (repositories, helpers).
 */
@ExtendWith(MockitoExtension.class)
class BulkImportServiceImplTest {

    // --- Mock all dependencies ---
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CsvValidationHelper validationHelper;

    // --- Automatically inject mocks into this service instance ---
    @InjectMocks
    private BulkImportServiceImpl bulkImportService;

    // --- ArgumentCaptors to capture saved entities ---
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<UserProfile> userProfileCaptor;
    @Captor
    private ArgumentCaptor<Student> studentCaptor;
    @Captor
    private ArgumentCaptor<Staff> staffCaptor;

    // --- Test Data ---
    private String[] validStudentRow;
    private String[] validStaffRow;
    private Role mockStudentRole;
    private Role mockStaffRole;
    private Section mockSection;

    @BeforeEach
    void setUp() {
        // A standard valid row for student tests
        validStudentRow = new String[]{
                "John", "Doe", "M", "john.doe@example.com", "2005-01-15", "MALE",
                "S12345", "2023-09-01", "Class 10", "A"
        };

        // A standard valid row for staff tests
        validStaffRow = new String[]{
                "Jane", "Smith", "", "jane.smith@example.com", "1990-05-20", "FEMALE",
                "T98765", "2015-08-15", "Math Teacher", "ACADEMICS", "TEACHER"
        };

        // Mock entities that are *fetched*
        mockStudentRole = new Role();
        mockStudentRole.setName("ROLE_STUDENT");

        mockStaffRole = new Role();
        mockStaffRole.setName("ROLE_TEACHER");

        mockSection = new Section();
        mockSection.setSectionName("A");
    }

    /**
     * Mocks all CsvValidationHelper calls to successfully parse the validStudentRow.
     */
    private void mockStudentHelperSuccess() throws Exception {
        when(validationHelper.validateString(validStudentRow[0], "firstName")).thenReturn("John");
        when(validationHelper.validateString(validStudentRow[1], "lastName")).thenReturn("Doe");
        when(validationHelper.validateEmail(validStudentRow[3])).thenReturn("john.doe@example.com");
        when(validationHelper.parseDate(validStudentRow[4], "dateOfBirth")).thenReturn(LocalDate.of(2005, 1, 15));
        when(validationHelper.parseEnum(Gender.class, validStudentRow[5], "gender")).thenReturn(Gender.MALE);
        when(validationHelper.validateString(validStudentRow[6], "enrollmentNumber")).thenReturn("S12345");
        when(validationHelper.parseDate(validStudentRow[7], "enrollmentDate")).thenReturn(LocalDate.of(2023, 9, 1));
        when(validationHelper.validateString(validStudentRow[8], "className")).thenReturn("Class 10");
        when(validationHelper.validateString(validStudentRow[9], "sectionName")).thenReturn("A");
    }

    @Test
    void processStudentRow_success() throws Exception {
        // --- 1. Arrange (Mock all external calls) ---

        // Mock helper validation
        mockStudentHelperSuccess();

        // Mock business logic checks (no duplicates)
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(studentRepository.existsByEnrollmentNumber("S12345")).thenReturn(false);

        // Mock FK fetches
        when(sectionRepository.findByAcademicClass_NameAndSectionName("Class 10", "A"))
                .thenReturn(Optional.of(mockSection));
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(mockStudentRole);

        // Mock password encoding
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");

        // --- 2. Act (Call the method) ---
        bulkImportService.processStudentRow(validStudentRow);

        // --- 3. Assert (Verify interactions and captured values) ---

        // Verify save methods were called once each
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userProfileRepository, times(1)).save(userProfileCaptor.capture());
        verify(studentRepository, times(1)).save(studentCaptor.capture());

        // Get the captured entities
        User savedUser = userCaptor.getValue();
        UserProfile savedProfile = userProfileCaptor.getValue();
        Student savedStudent = studentCaptor.getValue();

        // Assert content of captured entities
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals("hashed_password", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(mockStudentRole));

        assertEquals("John", savedProfile.getFirstName());
        assertEquals("Doe", savedProfile.getLastName());
        assertEquals(savedUser, savedProfile.getUser()); // Check linkage

        assertEquals("S12345", savedStudent.getEnrollmentNumber());
        assertEquals(savedProfile, savedStudent.getUserProfile()); // Check linkage
        assertEquals(mockSection, savedStudent.getSection()); // Check linkage
    }

    @Test
    void processStudentRow_throwsException_whenEmailExists() throws Exception {
        // --- 1. Arrange ---
        mockStudentHelperSuccess();

        // Mock the business rule violation
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // --- 2. Act & 3. Assert ---
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bulkImportService.processStudentRow(validStudentRow);
        });

        assertEquals("User with email 'john.doe@example.com' already exists.", e.getMessage());

        // Verify no saves were attempted
        verify(userRepository, never()).save(any());
        verify(userProfileRepository, never()).save(any());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void processStudentRow_throwsException_whenSectionNotFound() throws Exception {
        // --- 1. Arrange ---
        mockStudentHelperSuccess();

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(studentRepository.existsByEnrollmentNumber("S12345")).thenReturn(false);

        // Mock the FK fetch failure
        when(sectionRepository.findByAcademicClass_NameAndSectionName("Class 10", "A"))
                .thenReturn(Optional.empty());

        // --- 2. Act & 3. Assert ---
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bulkImportService.processStudentRow(validStudentRow);
        });

        assertEquals("Section not found for class 'Class 10' and section 'A'.", e.getMessage());
        verify(userRepository, never()).save(any()); // Verify rollback
    }

    @Test
    void processStaffRow_success() throws Exception {
        // --- 1. Arrange ---

        // Mock helper validation
        when(validationHelper.validateString(validStaffRow[0], "firstName")).thenReturn("Jane");
        when(validationHelper.validateString(validStaffRow[1], "lastName")).thenReturn("Smith");
        when(validationHelper.validateEmail(validStaffRow[3])).thenReturn("jane.smith@example.com");
        when(validationHelper.parseDate(validStaffRow[4], "dateOfBirth")).thenReturn(LocalDate.of(1990, 5, 20));
        when(validationHelper.parseEnum(Gender.class, validStaffRow[5], "gender")).thenReturn(Gender.FEMALE);
        when(validationHelper.validateString(validStaffRow[6], "employeeId")).thenReturn("T98765");
        when(validationHelper.parseDate(validStaffRow[7], "joiningDate")).thenReturn(LocalDate.of(2015, 8, 15));
        when(validationHelper.validateString(validStaffRow[8], "jobTitle")).thenReturn("Math Teacher");
        when(validationHelper.validateString(validStaffRow[9], "department")).thenReturn("ACADEMICS");
        when(validationHelper.parseEnum(StaffType.class, validStaffRow[10], "staffType")).thenReturn(StaffType.TEACHER);

        // Mock business logic checks
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(staffRepository.existsByEmployeeId("T98765")).thenReturn(false);

        // Mock FK fetches
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(mockStaffRole);

        // Mock password encoding
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");

        // --- 2. Act ---
        bulkImportService.processStaffRow(validStaffRow);

        // --- 3. Assert ---
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userProfileRepository, times(1)).save(userProfileCaptor.capture());
        verify(staffRepository, times(1)).save(staffCaptor.capture());

        User savedUser = userCaptor.getValue();
        Staff savedStaff = staffCaptor.getValue();

        assertEquals("jane.smith@example.com", savedUser.getEmail());
        assertTrue(savedUser.getRoles().contains(mockStaffRole));

        assertEquals("T98765", savedStaff.getEmployeeId());
        assertEquals(StaffType.TEACHER, savedStaff.getStaffType());
        assertEquals(Department.ACADEMICS, savedStaff.getDepartment());
    }

    /**
     * NOTE on testing importUsers():
     * The main importUsers() method is difficult to unit test because it creates
     * its own CSVReader, which we cannot mock.
     *
     * This method is best tested with a full @SpringBootTest (Integration Test),
     * where we pass a real MockMultipartFile containing CSV string data and
     * verify the final state of an in-memory (H2) database.
     *
     * Since 99% of the complex logic is in the process...Row() methods,
     * unit-testing them (as we have done) gives us high confidence in the service.
     */
}
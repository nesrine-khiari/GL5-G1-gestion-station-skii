package com.example.gestionstationskii.contextSpring;

import com.example.gestionstationskii.services.InstructorServicesImpl;

import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Instructor;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.repositories.IInstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InstructorServicesImplTest {

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    private Instructor instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instructor = new Instructor();
        instructor.setNumInstructor(1L);
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructor.setDateOfHire(LocalDate.of(2022, 1, 1));

        course = new Course();
        course.setNumCourse(100L);
        course.setLevel(1);
        course.setTypeCourse(null);
        course.setSupport(null);
        course.setPrice(120.0f);
        course.setTimeSlot(3);
    }

    @Test
    void testAddInstructor() {
        // Arrange
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor savedInstructor = instructorServices.addInstructor(instructor);

        // Assert
        assertThat(savedInstructor).isNotNull();
        assertThat(savedInstructor.getFirstName()).isEqualTo("John");
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveAllInstructors() {
        // Arrange
        List<Instructor> instructors = List.of(instructor);
        when(instructorRepository.findAll()).thenReturn(instructors);

        // Act
        List<Instructor> result = instructorServices.retrieveAllInstructors();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Doe");
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testUpdateInstructor() {
        // Arrange
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor updated = instructorServices.updateInstructor(instructor);

        // Assert
        assertThat(updated).isNotNull();
        assertThat(updated.getNumInstructor()).isEqualTo(1L);
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveInstructor_Found() {
        // Arrange
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        // Act
        Instructor found = instructorServices.retrieveInstructor(1L);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("John");
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveInstructor_NotFound() {
        // Arrange
        when(instructorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Instructor result = instructorServices.retrieveInstructor(999L);

        // Assert
        assertThat(result).isNull();
        verify(instructorRepository, times(1)).findById(999L);
    }

    @Test
    void testAddInstructorAndAssignToCourse_CourseFound() {
        // Arrange
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, 100L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCourses()).isNotEmpty();
        assertThat(result.getCourses().iterator().next().getNumCourse()).isEqualTo(100L);
        verify(courseRepository, times(1)).findById(100L);
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testAddInstructorAndAssignToCourse_CourseNotFound() {
        // Arrange
        when(courseRepository.findById(200L)).thenReturn(Optional.empty());

        // Act
        Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, 200L);

        // Assert
        assertThat(result).isNull();
        verify(courseRepository, times(1)).findById(200L);
        verify(instructorRepository, never()).save(any());
    }
}
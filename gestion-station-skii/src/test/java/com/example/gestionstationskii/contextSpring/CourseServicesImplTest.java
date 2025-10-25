package com.example.gestionstationskii.contextSpring;
import com.example.gestionstationskii.entities.Course;
import com.example.gestionstationskii.entities.Support;
import com.example.gestionstationskii.entities.TypeCourse;
import com.example.gestionstationskii.repositories.ICourseRepository;
import com.example.gestionstationskii.services.CourseServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCourse() {
        // GIVEN
        Course course = new Course();
        course.setNumCourse(1L);
        course.setLevel(2);
        course.setTypeCourse(TypeCourse.INDIVIDUAL);
        course.setSupport(Support.SKI);
        course.setPrice(150.0f);
        course.setTimeSlot(10);

        when(courseRepository.save(course)).thenReturn(course);

        // WHEN
        Course saved = courseServices.addCourse(course);

        // THEN
        assertNotNull(saved);
        assertEquals(TypeCourse.INDIVIDUAL, saved.getTypeCourse());
        assertEquals(Support.SKI, saved.getSupport());
        verify(courseRepository, times(1)).save(course);
        System.out.println("✅ testAddCourse OK");
    }

    @Test
    void testRetrieveAllCourses() {
        // GIVEN
        List<Course> mockCourses = Arrays.asList(
                new Course(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100.0f, 9, null),
                new Course(2L, 3, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 200.0f, 14, null)
        );
        when(courseRepository.findAll()).thenReturn(mockCourses);

        // WHEN
        List<Course> result = courseServices.retrieveAllCourses();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
        System.out.println("✅ testRetrieveAllCourses OK");
    }

    @Test
    void testUpdateCourse() {
        // GIVEN
        Course course = new Course(1L, 1, TypeCourse.COLLECTIVE_ADULT, Support.SKI, 120.0f, 8, null);
        when(courseRepository.save(course)).thenReturn(course);

        // WHEN
        course.setPrice(250.0f);
        course.setLevel(3);
        Course updated = courseServices.updateCourse(course);

        // THEN
        assertEquals(250.0f, updated.getPrice());
        assertEquals(3, updated.getLevel());
        verify(courseRepository, times(1)).save(course);
        System.out.println("✅ testUpdateCourse OK");
    }

    @Test
    void testRetrieveCourse() {
        // GIVEN
        Course course = new Course(1L, 2, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 90.0f, 12, null);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // WHEN
        Course found = courseServices.retrieveCourse(1L);

        // THEN
        assertNotNull(found);
        assertEquals(2, found.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, found.getTypeCourse());
        verify(courseRepository, times(1)).findById(1L);
        System.out.println("✅ testRetrieveCourse OK");
    }
}

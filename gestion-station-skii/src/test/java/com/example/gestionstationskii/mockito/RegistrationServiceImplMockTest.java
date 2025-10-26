package com.example.gestionstationskii.mockito;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.RegistrationServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationServiceImplMockTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_Success() {
        // GIVEN
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("Mariem");
        skier.setLastName("Askri");
        skier.setDateOfBirth(LocalDate.of(2010, 5, 15));

        Course course = new Course();
        course.setNumCourse(10L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setSupport(Support.SKI);
        course.setPrice(100.0f);

        Registration registration = new Registration();
        registration.setNumWeek(1);

        // Mock behavior
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(1, 1L, 10L)).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(course, 1)).thenReturn(2L);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 10L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getSkier()).isEqualTo(skier);
        assertThat(result.getCourse()).isEqualTo(course);

        // Verify interactions
        verify(skierRepository).findById(1L);
        verify(courseRepository).findById(10L);
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_AlreadyRegistered() {
        // GIVEN
        Skier skier = new Skier();
        skier.setNumSkier(2L);
        skier.setDateOfBirth(LocalDate.of(2005, 2, 10));

        Course course = new Course();
        course.setNumCourse(20L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        course.setSupport(Support.SNOWBOARD);

        Registration registration = new Registration();
        registration.setNumWeek(3);

        when(skierRepository.findById(2L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(3, 2L, 20L)).thenReturn(1L);

        // WHEN
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 2L, 20L);

        // THEN
        assertThat(result).isNull();
        verify(registrationRepository, never()).save(any(Registration.class));
    }
}

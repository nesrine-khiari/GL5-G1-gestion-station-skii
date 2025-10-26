package com.example.gestionstationskii.contextSpring;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.IRegistrationServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RegistrationServiceImplTest {

    @Autowired
    private IRegistrationServices registrationServices;

    @Autowired
    private ISkierRepository skierRepository;

    @Autowired
    private ICourseRepository courseRepository;

    @Autowired
    private IRegistrationRepository registrationRepository;

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse() {
        // Given
        Skier skier = new Skier();
        skier.setFirstName("Mariem");
        skier.setLastName("Askri");
        skier.setDateOfBirth(LocalDate.of(2010, 5, 15));
        skier = skierRepository.save(skier);

        Course course = new Course();
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setSupport(Support.SKI);
        course.setPrice(100.0f);
        course = courseRepository.save(course); //

        Registration registration = new Registration();
        registration.setNumWeek(1);

        // When
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(
                registration, skier.getNumSkier(), course.getNumCourse());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSkier()).isNotNull();
        assertThat(result.getCourse()).isNotNull();


        assertThat(result.getSkier().getNumSkier()).isEqualTo(skier.getNumSkier());
        assertThat(result.getCourse().getNumCourse()).isEqualTo(course.getNumCourse());

        // check it’s persisted in DB
        assertThat(registrationRepository.findById(result.getNumRegistration())).isPresent();
    }
}

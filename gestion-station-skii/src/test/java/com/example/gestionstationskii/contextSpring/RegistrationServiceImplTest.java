package com.example.gestionstationskii.contextSpring;

import com.example.gestionstationskii.entities.Registration;
import com.example.gestionstationskii.entities.Skier;
import com.example.gestionstationskii.repositories.IRegistrationRepository;
import com.example.gestionstationskii.repositories.ISkierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RegistrationServiceImplTest {

    @Autowired
    private IRegistrationRepository registrationRepository;

    @Autowired
    private ISkierRepository skierRepository;

    // 🟢 CREATE
    @Test
    void testCreateRegistration() {
        Skier skier = new Skier();
        skier.setFirstName("Mariem");
        skier.setLastName("Askri");
        skier.setDateOfBirth(LocalDate.of(2010, 5, 15));
        skier = skierRepository.save(skier);

        Registration registration = new Registration();
        registration.setNumWeek(1);
        registration.setSkier(skier);

        Registration saved = registrationRepository.save(registration);

        assertThat(saved.getNumRegistration()).isNotNull();
        assertThat(saved.getSkier()).isEqualTo(skier);
    }

    // 🔵 READ
    @Test
    void testGetRegistrationById() {
        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        assertThat(registrations).isNotEmpty();

        Registration registration = registrations.get(0);
        Optional<Registration> found = registrationRepository.findById(registration.getNumRegistration());

        assertThat(found).isPresent();
        assertThat(found.get().getNumWeek()).isEqualTo(registration.getNumWeek());
    }

    // 🟡 UPDATE
    @Test
    void testUpdateRegistration() {
        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        assertThat(registrations).isNotEmpty();

        Registration registration = registrations.get(0);
        registration.setNumWeek(5);

        Registration updated = registrationRepository.save(registration);
        assertThat(updated.getNumWeek()).isEqualTo(5);
    }

    // 🔴 DELETE
    @Test
    void testDeleteRegistration() {
        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        assertThat(registrations).isNotEmpty();

        Registration registration = registrations.get(0);
        Long id = registration.getNumRegistration();

        registrationRepository.deleteById(id);

        Optional<Registration> deleted = registrationRepository.findById(id);
        assertThat(deleted).isEmpty();
    }
}
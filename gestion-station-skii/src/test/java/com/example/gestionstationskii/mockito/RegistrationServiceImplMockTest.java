package com.example.gestionstationskii.mockito;

import com.example.gestionstationskii.entities.Registration;
import com.example.gestionstationskii.repositories.IRegistrationRepository;
import com.example.gestionstationskii.services.RegistrationServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceImplMockTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    private Registration registration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(10);
    }

    //  CREATE
    @Test
    void testCreateRegistration() {
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration saved = registrationRepository.save(registration);

        assertNotNull(saved);
        assertEquals(1L, saved.getNumRegistration());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    //  READ
    @Test
    void testGetRegistrationById() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        Optional<Registration> found = registrationRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(10, found.get().getNumWeek());
        verify(registrationRepository, times(1)).findById(1L);
    }

    //  UPDATE
    @Test
    void testUpdateRegistration() {
        registration.setNumWeek(10);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration existing = registrationRepository.findById(1L).get();
        existing.setNumWeek(15);
        Registration updated = registrationRepository.save(existing);

        assertEquals(15, updated.getNumWeek());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    //  DELETE
    @Test
    void testDeleteRegistration() {
        doNothing().when(registrationRepository).deleteById(1L);

        registrationRepository.deleteById(1L);

        verify(registrationRepository, times(1)).deleteById(1L);
    }
}
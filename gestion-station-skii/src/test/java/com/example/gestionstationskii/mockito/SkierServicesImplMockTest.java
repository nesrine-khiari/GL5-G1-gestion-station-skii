package com.example.gestionstationskii.mockito;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.SkierServicesImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkierServicesImplMockTest {

    @Mock
    private ISkierRepository skierRepository;
    @Mock
    private IPisteRepository pisteRepository;
    @Mock
    private ICourseRepository courseRepository;
    @Mock
    private IRegistrationRepository registrationRepository;
    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SkierServicesImpl skierServices;

    private Skier skier;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);

        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setSubscription(subscription);
    }

    @Test
    void testRetrieveAllSkiers() {
        when(skierRepository.findAll()).thenReturn(List.of(skier));

        List<Skier> result = skierServices.retrieveAllSkiers();

        assertEquals(1, result.size());
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void testAddSkierWithSubscription() {
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier saved = skierServices.addSkier(skier);

        assertNotNull(saved);
        assertNotNull(saved.getSubscription().getEndDate());
        assertEquals(saved.getSubscription().getEndDate(),
                saved.getSubscription().getStartDate().plusYears(1));

        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierServices.assignSkierToSubscription(1L, 1L);

        assertNotNull(result);
        assertEquals(subscription, result.getSubscription());
        verify(skierRepository).save(skier);
    }

    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        Skier result = skierServices.assignSkierToPiste(1L, 1L);

        assertNotNull(result);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL))
                .thenReturn(List.of(skier));

        List<Skier> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        assertEquals(1, result.size());
        verify(skierRepository).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }
}

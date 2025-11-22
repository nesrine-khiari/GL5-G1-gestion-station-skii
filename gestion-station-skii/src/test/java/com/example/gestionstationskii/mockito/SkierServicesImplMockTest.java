package com.example.gestionstationskii.mockito;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.SkierServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        skier.setLastName("Doe");
        skier.setCity("Sfax");
        skier.setSubscription(subscription);
    }

    @Test
    void testRetrieveAllSkiers() {
        when(skierRepository.findAll()).thenReturn(List.of(skier));

        List<SkierDTO> result = skierServices.retrieveAllSkiers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void testAddSkierWithSubscription() {
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        SkierDTO dto = new SkierDTO();
        dto.setFirstName(skier.getFirstName());
        dto.setLastName(skier.getLastName());
        dto.setCity(skier.getCity());
        dto.setSubscriptionId(subscription.getNumSub());

        SkierDTO saved = skierServices.addSkier(dto);

        assertNotNull(saved);
        assertEquals(subscription.getNumSub(), saved.getSubscriptionId());
        verify(skierRepository, times(1)).save(any(Skier.class));
    }

    @Test
    void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        SkierDTO result = skierServices.assignSkierToSubscription(1L, 1L);

        assertNotNull(result);
        assertEquals(subscription.getNumSub(), result.getSubscriptionId());
        verify(skierRepository).save(any(Skier.class));
    }

    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNumPiste(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);

        SkierDTO result = skierServices.assignSkierToPiste(1L, 1L);

        assertNotNull(result);
        verify(skierRepository, times(1)).save(any(Skier.class));
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL))
                .thenReturn(List.of(skier));

        List<SkierDTO> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals(subscription.getNumSub(), result.get(0).getSubscriptionId());
        verify(skierRepository).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }
}

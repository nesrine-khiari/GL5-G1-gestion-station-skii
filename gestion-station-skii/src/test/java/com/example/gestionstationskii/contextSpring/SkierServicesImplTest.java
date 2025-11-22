package com.example.gestionstationskii.contextSpring;

import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;
import com.example.gestionstationskii.services.SkierServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(SkierServicesImpl.class)
class SkierServicesImplTest {

    @Autowired
    private SkierServicesImpl skierServices;

    @Autowired
    private ISkierRepository skierRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPisteRepository pisteRepository;

    private Skier skier;

    @BeforeEach
    void setUp() {
        skier = new Skier();
        skier.setFirstName("Houcem");
        skier.setLastName("Hbiri");
        skier.setCity("Sfax");
    }

    @Test
    void testAddSkierWithSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.MONTHLY);

        subscriptionRepository.save(subscription);

        SkierDTO dto = new SkierDTO();
        dto.setFirstName(skier.getFirstName());
        dto.setLastName(skier.getLastName());
        dto.setCity(skier.getCity());
        dto.setSubscriptionId(subscription.getNumSub());

        SkierDTO saved = skierServices.addSkier(dto);

        assertNotNull(saved.getNumSkier());
        assertEquals(subscription.getNumSub(), saved.getSubscriptionId());
        assertEquals(1, skierRepository.findAll().size());
    }

    @Test
    void testAssignSkierToSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscriptionRepository.save(subscription);

        Skier savedSkier = skierRepository.save(skier);

        SkierDTO updated = skierServices.assignSkierToSubscription(savedSkier.getNumSkier(), subscription.getNumSub());

        assertNotNull(updated);
        assertEquals(subscription.getNumSub(), updated.getSubscriptionId());
    }

    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNamePiste("Blue Trail");
        pisteRepository.save(piste);

        Skier savedSkier = skierRepository.save(skier);

        SkierDTO updated = skierServices.assignSkierToPiste(savedSkier.getNumSkier(), piste.getNumPiste());

        assertNotNull(updated);
        Skier skierEntity = skierRepository.findById(savedSkier.getNumSkier()).orElse(null);
        assertNotNull(skierEntity);
        assertNotNull(skierEntity.getPistes());
        assertEquals(1, skierEntity.getPistes().size());
    }

    @Test
    void testRemoveSkier() {
        Skier saved = skierRepository.save(skier);

        skierServices.removeSkier(saved.getNumSkier());

        assertTrue(skierRepository.findAll().isEmpty());
    }

    @Test
    void testRetrieveSkiersBySubscriptionType() {
        Subscription sub1 = new Subscription();
        sub1.setStartDate(LocalDate.now());
        sub1.setTypeSub(TypeSubscription.SEMESTRIEL);
        subscriptionRepository.save(sub1);

        skier.setSubscription(sub1);
        skierRepository.save(skier);

        List<SkierDTO> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.SEMESTRIEL);
        assertEquals(1, result.size());
        assertEquals("Houcem", result.get(0).getFirstName());
        assertEquals(sub1.getNumSub(), result.get(0).getSubscriptionId());
    }
}

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
@Import(SkierServicesImpl.class) // on importe le vrai service, pas un mock
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
        // Créer un skieur avant chaque test
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

        skier.setSubscription(subscription);

        Skier saved = skierServices.addSkier(skier);

        assertNotNull(saved.getNumSkier());
        assertNotNull(saved.getSubscription());
        assertEquals(saved.getSubscription().getEndDate(),
                subscription.getStartDate().plusMonths(1));
        assertEquals(1, skierRepository.findAll().size());
    }

    @Test
    void testAssignSkierToSubscription() {
        // créer et sauvegarder un abonnement
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscriptionRepository.save(subscription);

        // enregistrer le skieur
        Skier savedSkier = skierRepository.save(skier);

        // exécuter le service
        skierServices.assignSkierToSubscription(savedSkier.getNumSkier(), subscription.getNumSub());

        Skier updated = skierRepository.findById(savedSkier.getNumSkier()).orElse(null);
        assertNotNull(updated);
        assertEquals(subscription.getNumSub(), updated.getSubscription().getNumSub());
    }

    @Test
    void testAssignSkierToPiste() {
        Piste piste = new Piste();
        piste.setNamePiste("Blue Trail");
        pisteRepository.save(piste);

        Skier savedSkier = skierRepository.save(skier);

        skierServices.assignSkierToPiste(savedSkier.getNumSkier(), piste.getNumPiste());

        Skier updated = skierRepository.findById(savedSkier.getNumSkier()).orElse(null);
        assertNotNull(updated);
        assertNotNull(updated.getPistes());
        assertEquals(1, updated.getPistes().size());
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

        List<Skier> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.SEMESTRIEL);
        assertEquals(1, result.size());
        assertEquals("Houcem", result.get(0).getFirstName());
    }
}

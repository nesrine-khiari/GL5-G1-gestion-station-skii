package com.example.gestionstationskii.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private final ISkierRepository skierRepository;
    private final IPisteRepository pisteRepository;
    private final ICourseRepository courseRepository;
    private final IRegistrationRepository registrationRepository;
    private final ISubscriptionRepository subscriptionRepository;

    @Override
    public List<Skier> retrieveAllSkiers() {
        log.info("Retrieving all skiers...");
        List<Skier> skiers = skierRepository.findAll();
        log.info("Found {} skiers", skiers.size());
        return skiers;
    }

    @Override
    public Skier addSkier(Skier skier) {
        log.info("Adding new skier: {}", skier.getFirstName());
        if (skier.getSubscription() != null) {
            switch (skier.getSubscription().getTypeSub()) {
                case ANNUAL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                    break;
                case SEMESTRIEL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                    break;
                case MONTHLY:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                    break;
            }
            log.debug("Subscription end date set to {}", skier.getSubscription().getEndDate());
        } else {
            log.warn("Skier has no subscription assigned!");
        }
        Skier saved = skierRepository.save(skier);
        log.info("Skier {} added successfully with ID {}", saved.getFirstName(), saved.getNumSkier());
        return saved;
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        log.info("Assigning skier {} to subscription {}", numSkier, numSubscription);
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElse(null);

        if (skier == null || subscription == null) {
            log.error("Skier or subscription not found (skierId={}, subscriptionId={})", numSkier, numSubscription);
            return null;
        }

        skier.setSubscription(subscription);
        Skier saved = skierRepository.save(skier);
        log.info("Skier {} successfully assigned to subscription {}", numSkier, numSubscription);
        return saved;
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        log.info("Adding skier and assigning to course {}", numCourse);
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.findById(numCourse).orElse(null);

        if (course == null) {
            log.error("Course not found with ID {}", numCourse);
            return savedSkier;
        }

        Set<Registration> registrations = savedSkier.getRegistrations();
        if (registrations == null || registrations.isEmpty()) {
            log.warn("Skier {} has no registrations to assign to course {}", savedSkier.getNumSkier(), numCourse);
        } else {
            for (Registration r : registrations) {
                r.setSkier(savedSkier);
                r.setCourse(course);
                registrationRepository.save(r);
                log.debug("Saved registration for skier {} in course {}", savedSkier.getNumSkier(), numCourse);
            }
        }
        log.info("Skier {} added and assigned to course {}", savedSkier.getNumSkier(), numCourse);
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        log.info("Removing skier with ID {}", numSkier);
        skierRepository.deleteById(numSkier);
        log.info("Skier {} removed successfully", numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        log.info("Retrieving skier with ID {}", numSkier);
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        if (skier == null)
            log.warn("No skier found with ID {}", numSkier);
        else
            log.info("Skier found: {}", skier.getFirstName());
        return skier;
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        log.info("Assigning skier {} to piste {}", numSkieur, numPiste);
        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);

        if (skier == null || piste == null) {
            log.error("Skier or piste not found (skierId={}, pisteId={})", numSkieur, numPiste);
            return null;
        }

        try {
            skier.getPistes().add(piste);
            log.debug("Piste {} added to skier {}", numPiste, numSkieur);
        } catch (NullPointerException e) {
            log.warn("Skier {} had no pistes list initialized. Creating one.", numSkieur);
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }

        Skier saved = skierRepository.save(skier);
        log.info("Skier {} successfully assigned to piste {}", numSkieur, numPiste);
        return saved;
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        log.info("Retrieving skiers with subscription type {}", typeSubscription);
        List<Skier> skiers = skierRepository.findBySubscription_TypeSub(typeSubscription);
        log.info("Found {} skiers with type {}", skiers.size(), typeSubscription);
        return skiers;
    }
}

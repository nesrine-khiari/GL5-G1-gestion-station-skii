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

    private SkierDTO toDto(Skier skier) {
        Long subId = skier.getSubscription() != null ? skier.getSubscription().getNumSub() : null;
        return new SkierDTO(
                skier.getNumSkier(),
                skier.getFirstName(),
                skier.getLastName(),
                skier.getDateOfBirth(),
                skier.getCity(),
                subId);
    }

    private Skier fromDto(SkierDTO dto) {
        Skier skier = new Skier();
        skier.setNumSkier(dto.getNumSkier());
        skier.setFirstName(dto.getFirstName());
        skier.setLastName(dto.getLastName());
        skier.setDateOfBirth(dto.getDateOfBirth());
        skier.setCity(dto.getCity());

        if (dto.getSubscriptionId() != null) {
            subscriptionRepository.findById(dto.getSubscriptionId())
                    .ifPresent(skier::setSubscription);
        }
        return skier;
    }

    @Override
    public List<SkierDTO> retrieveAllSkiers() {
        log.info("Retrieving all skiers...");
        List<Skier> skiers = skierRepository.findAll();
        log.info("Found {} skiers", skiers.size());
        return skiers.stream().map(this::toDto).toList();
    }

    @Override
    public SkierDTO addSkier(SkierDTO dto) {
        log.info("Adding new skier: {}", dto.getFirstName());
        Skier skier = fromDto(dto);

        if (skier.getSubscription() != null) {
            switch (skier.getSubscription().getTypeSub()) {
                case ANNUAL -> skier.getSubscription().setEndDate(
                        skier.getSubscription().getStartDate().plusYears(1));
                case SEMESTRIEL -> skier.getSubscription().setEndDate(
                        skier.getSubscription().getStartDate().plusMonths(6));
                case MONTHLY -> skier.getSubscription().setEndDate(
                        skier.getSubscription().getStartDate().plusMonths(1));
            }
            log.debug("Subscription end date set to {}", skier.getSubscription().getEndDate());
        } else {
            log.warn("Skier has no subscription assigned!");
        }

        Skier saved = skierRepository.save(skier);
        log.info("Skier {} added successfully with ID {}", saved.getFirstName(), saved.getNumSkier());
        return toDto(saved);
    }

    @Override
    public SkierDTO assignSkierToSubscription(Long numSkier, Long numSubscription) {
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
        return toDto(saved);
    }

    @Override
    public SkierDTO addSkierAndAssignToCourse(SkierDTO dto, Long numCourse) {
        log.info("Adding skier and assigning to course {}", numCourse);
        Skier skier = fromDto(dto);
        Skier savedSkier = skierRepository.save(skier);

        var course = courseRepository.findById(numCourse).orElse(null);
        if (course == null) {
            log.error("Course not found with ID {}", numCourse);
            return toDto(savedSkier);
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
        return toDto(savedSkier);
    }

    @Override
    public void removeSkier(Long numSkier) {
        log.info("Removing skier with ID {}", numSkier);
        skierRepository.deleteById(numSkier);
        log.info("Skier {} removed successfully", numSkier);
    }

    @Override
    public SkierDTO retrieveSkier(Long numSkier) {
        log.info("Retrieving skier with ID {}", numSkier);
        var skier = skierRepository.findById(numSkier).orElse(null);
        if (skier == null)
            log.warn("No skier found with ID {}", numSkier);
        else
            log.info("Skier found: {}", skier.getFirstName());
        return skier != null ? toDto(skier) : null;
    }

    @Override
    public SkierDTO assignSkierToPiste(Long numSkier, Long numPiste) {
        log.info("Assigning skier {} to piste {}", numSkier, numPiste);
        Skier skier = skierRepository.findById(numSkier).orElse(null);
        Piste piste = pisteRepository.findById(numPiste).orElse(null);

        if (skier == null || piste == null) {
            log.error("Skier or piste not found (skierId={}, pisteId={})", numSkier, numPiste);
            return null;
        }

        try {
            skier.getPistes().add(piste);
            log.debug("Piste {} added to skier {}", numPiste, numSkier);
        } catch (NullPointerException e) {
            log.warn("Skier {} had no pistes list initialized. Creating one.", numSkier);
            Set<Piste> pisteList = new HashSet<>();
            pisteList.add(piste);
            skier.setPistes(pisteList);
        }

        Skier saved = skierRepository.save(skier);
        log.info("Skier {} successfully assigned to piste {}", numSkier, numPiste);
        return toDto(saved);
    }

    @Override
    public List<SkierDTO> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        log.info("Retrieving skiers with subscription type {}", typeSubscription);
        List<Skier> skiers = skierRepository.findBySubscription_TypeSub(typeSubscription);
        log.info("Found {} skiers with type {}", skiers.size(), typeSubscription);
        return skiers.stream().map(this::toDto).toList();
    }

}

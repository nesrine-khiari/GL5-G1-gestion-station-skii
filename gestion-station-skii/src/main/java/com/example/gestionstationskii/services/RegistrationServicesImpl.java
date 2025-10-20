package com.example.gestionstationskii.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.gestionstationskii.entities.*;
import com.example.gestionstationskii.repositories.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@AllArgsConstructor
@Service
public class RegistrationServicesImpl implements IRegistrationServices {

    private IRegistrationRepository registrationRepository;
    private ISkierRepository skierRepository;
    private ICourseRepository courseRepository;

    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        System.out.println("️ Ajout d'une inscription et assignation au skieur avec ID : " + numSkier);
        Skier skier = skierRepository.findById(numSkier).orElse(null);

        if (skier == null) {
            System.out.println("️ Skieur avec ID " + numSkier + " introuvable !");
            return null;
        }

        registration.setSkier(skier);
        Registration saved = registrationRepository.save(registration);
        System.out.println("Inscription " + saved.getNumRegistration() + " assignée avec succès au skieur " + skier.getFirstName());
        return saved;
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        System.out.println("️ Assignation de l'inscription " + numRegistration + " au cours " + numCourse);
        Registration registration = registrationRepository.findById(numRegistration).orElse(null);
        Course course = courseRepository.findById(numCourse).orElse(null);

        if (registration == null || course == null) {
            System.out.println("️ Inscription ou cours introuvable !");
            return null;
        }

        registration.setCourse(course);
        Registration saved = registrationRepository.save(registration);
        System.out.println("Inscription " + saved.getNumRegistration() + " assignée au cours " + course.getTypeCourse());
        return saved;
    }

    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        System.out.println("️ Début du processus d'inscription pour skieur " + numSkieur + " et cours " + numCours);

        Skier skier = skierRepository.findById(numSkieur).orElse(null);
        Course course = courseRepository.findById(numCours).orElse(null);

        if (skier == null || course == null) {
            System.out.println(" Skieur ou cours introuvable !");
            return null;
        }

        if (registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1) {
            System.out.println("️ Déjà inscrit à ce cours pour la semaine " + registration.getNumWeek());
            return null;
        }

        int ageSkieur = Period.between(skier.getDateOfBirth(), LocalDate.now()).getYears();
        System.out.println("👤 Âge du skieur : " + ageSkieur);

        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                System.out.println(" Cours individuel détecté — ajout direct");
                return assignRegistration(registration, skier, course);

            case COLLECTIVE_CHILDREN:
                if (ageSkieur < 16) {
                    System.out.println(" Cours enfant — âge valide");
                    if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                        System.out.println(" Place disponible, inscription enregistrée !");
                        return assignRegistration(registration, skier, course);
                    } else {
                        System.out.println(" Cours complet pour la semaine " + registration.getNumWeek());
                        return null;
                    }
                } else {
                    System.out.println(" Âge non valide pour un cours enfant !");
                }
                break;

            default:
                if (ageSkieur >= 16) {
                    System.out.println(" Cours adulte — âge valide");
                    if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                        System.out.println(" Place disponible, inscription enregistrée !");
                        return assignRegistration(registration, skier, course);
                    } else {
                        System.out.println(" Cours complet pour la semaine " + registration.getNumWeek());
                        return null;
                    }
                }
                System.out.println(" Âge non valide pour un cours adulte !");
        }

        System.out.println("Fin du processus sans enregistrement.");
        return registration;
    }

    private Registration assignRegistration(Registration registration, Skier skier, Course course) {
        System.out.println("🔗 Liaison du skieur " + skier.getNumSkier() + " avec le cours " + course.getNumCourse() +
                " pour la semaine " + registration.getNumWeek());
        registration.setSkier(skier);
        registration.setCourse(course);
        Registration saved = registrationRepository.save(registration);
        System.out.println(" Inscription " + saved.getNumRegistration() + " sauvegardée avec succès.");
        return saved;
    }

    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        System.out.println("Récupération des semaines pour l'instructeur " + numInstructor + " avec support " + support);
        List<Integer> result = registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
        System.out.println(" Nombre de semaines trouvées : " + result.size());
        return result;
    }
}

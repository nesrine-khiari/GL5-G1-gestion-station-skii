package com.example.gestionstationskii.services;

import com.example.gestionstationskii.entities.SkierDTO;
import com.example.gestionstationskii.entities.TypeSubscription;

import java.util.List;

public interface ISkierServices {

	List<SkierDTO> retrieveAllSkiers();

	SkierDTO addSkier(SkierDTO dto);

	SkierDTO assignSkierToSubscription(Long numSkier, Long numSubscription);

	SkierDTO addSkierAndAssignToCourse(SkierDTO dto, Long numCourse);

	void removeSkier(Long numSkier);

	SkierDTO retrieveSkier(Long numSkier);

	SkierDTO assignSkierToPiste(Long numSkieur, Long numPiste);

	List<SkierDTO> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);
}

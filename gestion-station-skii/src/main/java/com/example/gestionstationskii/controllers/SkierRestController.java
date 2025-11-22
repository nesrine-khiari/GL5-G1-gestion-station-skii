package com.example.gestionstationskii.controllers;

import com.example.gestionstationskii.entities.SkierDTO;
import com.example.gestionstationskii.entities.TypeSubscription;
import com.example.gestionstationskii.services.ISkierServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skier")
@RequiredArgsConstructor
public class SkierRestController {

    private final ISkierServices skierServices;

    @PostMapping("/add")
    public SkierDTO addSkier(@RequestBody SkierDTO dto) {
        return skierServices.addSkier(dto);
    }

    @PostMapping("/addAndAssign/{numCourse}")
    public SkierDTO addSkierAndAssignToCourse(@RequestBody SkierDTO dto,
            @PathVariable Long numCourse) {
        return skierServices.addSkierAndAssignToCourse(dto, numCourse);
    }

    @PutMapping("/assignToSub/{numSkier}/{numSub}")
    public SkierDTO assignToSubscription(@PathVariable Long numSkier,
            @PathVariable Long numSub) {
        return skierServices.assignSkierToSubscription(numSkier, numSub);
    }

    @PutMapping("/assignToPiste/{numSkier}/{numPiste}")
    public SkierDTO assignToPiste(@PathVariable Long numSkier,
            @PathVariable Long numPiste) {
        return skierServices.assignSkierToPiste(numSkier, numPiste);
    }

    @GetMapping("/getSkiersBySubscription")
    public List<SkierDTO> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierServices.retrieveSkiersBySubscriptionType(typeSubscription);
    }

    @GetMapping("/get/{id-skier}")
    public SkierDTO getById(@PathVariable("id-skier") Long numSkier) {
        return skierServices.retrieveSkier(numSkier);
    }

    @DeleteMapping("/delete/{id-skier}")
    public void deleteById(@PathVariable("id-skier") Long numSkier) {
        skierServices.removeSkier(numSkier);
    }

    @GetMapping("/all")
    public List<SkierDTO> getAllSkiers() {
        return skierServices.retrieveAllSkiers();
    }
}

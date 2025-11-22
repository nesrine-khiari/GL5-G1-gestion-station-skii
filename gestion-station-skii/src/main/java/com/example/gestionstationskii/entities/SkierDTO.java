package com.example.gestionstationskii.entities;

import java.time.LocalDate;

public class SkierDTO {
  private Long numSkier;
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private String city;
  private Long subscriptionId; // only reference

  // Constructors
  public SkierDTO() {
  }

  public SkierDTO(Long numSkier, String firstName, String lastName,
      LocalDate dateOfBirth, String city, Long subscriptionId) {
    this.numSkier = numSkier;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.city = city;
    this.subscriptionId = subscriptionId;
  }

  // Getters and setters
  public Long getNumSkier() {
    return numSkier;
  }

  public void setNumSkier(Long numSkier) {
    this.numSkier = numSkier;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(Long subscriptionId) {
    this.subscriptionId = subscriptionId;
  }
}

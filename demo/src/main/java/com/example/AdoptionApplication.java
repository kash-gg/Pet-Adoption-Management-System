package com.example;

import java.time.LocalDate;

public class AdoptionApplication {
    private int id;
    private String petName;
    private String applicantName;
    private String shelterName;
    private String status;
    private LocalDate applicationDate;

    public AdoptionApplication(int id, String petName, String applicantName, String shelterName, 
                             String status, LocalDate applicationDate) {
        this.id = id;
        this.petName = petName;
        this.applicantName = applicantName;
        this.shelterName = shelterName;
        this.status = status;
        this.applicationDate = applicationDate;
    }

    // Getters
    public int getId() { return id; }
    public String getPetName() { return petName; }
    public String getApplicantName() { return applicantName; }
    public String getShelterName() { return shelterName; }
    public String getStatus() { return status; }
    public LocalDate getApplicationDate() { return applicationDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }
    public void setStatus(String status) { this.status = status; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
} 
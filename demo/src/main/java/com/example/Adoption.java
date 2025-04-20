package com.example;

import java.time.LocalDate;

public class Adoption {
    private int id;
    private String petName;
    private String adopterName;
    private LocalDate adoptionDate;
    private String shelterName;

    public Adoption(int id, String petName, String adopterName, LocalDate adoptionDate, String shelterName) {
        this.id = id;
        this.petName = petName;
        this.adopterName = adopterName;
        this.adoptionDate = adoptionDate;
        this.shelterName = shelterName;
    }

    public int getId() {
        return id;
    }

    public String getPetName() {
        return petName;
    }

    public String getAdopterName() {
        return adopterName;
    }

    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }

    public String getShelterName() {
        return shelterName;
    }
} 
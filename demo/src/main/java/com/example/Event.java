package com.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private int id;
    private String title;
    private String location;
    private String type;
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String imageUrl;

    public Event(int id, String title, String location, String type, LocalDate date, LocalTime time, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.type = type;
        this.date = date;
        this.time = time;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 
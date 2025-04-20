package com.example;

public class Volunteer {
    private int id;
    private String volunteerId;
    private String name;
    private String role;
    private String phoneNumber;
    private String email;
    private int eventId;
    private String eventName; // For display purposes

    public Volunteer(int id, String volunteerId, String name, String role, String phoneNumber, String email, int eventId, String eventName) {
        this.id = id;
        this.volunteerId = volunteerId;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.eventId = eventId;
        this.eventName = eventName;
    }

    // Getters
    public int getId() { return id; }
    public String getVolunteerId() { return volunteerId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setVolunteerId(String volunteerId) { this.volunteerId = volunteerId; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setEventName(String eventName) { this.eventName = eventName; }
} 
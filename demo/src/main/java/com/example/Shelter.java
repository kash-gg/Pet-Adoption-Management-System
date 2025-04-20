package com.example;

public class Shelter {
    private int id;
    private String name;
    private String location;
    private String type;
    private double rating;
    private int capacity;
    private String description;
    private String phone;
    private String email;
    private String address;
    private String imageUrl;

    public Shelter(int id, String name, String location, String type, double rating,
                  int capacity, String description, String phone, String email,
                  String address) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
        this.rating = rating;
        this.capacity = capacity;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.imageUrl = name.toLowerCase().replace(" ", "-") + ".jpg";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public double getRating() { return rating; }
    public int getCapacity() { return capacity; }
    public String getDescription() { return description; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDescription(String description) { this.description = description; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return name;
    }
} 
package com.example;

public class Shelter {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private double rating;
    private int capacity;

    public Shelter(int id, String name, String address, String phone, String email,
                  double rating, int capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.rating = rating;
        this.capacity = capacity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getRating() { return rating; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return address; } // For backward compatibility

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return name;
    }
} 
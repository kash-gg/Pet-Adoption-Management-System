package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petpassion";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sh1v@m2003";
    
    private static int currentUserId = -1; // Default to -1 for no user logged in

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        System.out.println("Getting current user ID: " + currentUserId);
        return currentUserId;
    }
    
    public static boolean validateUser(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    setCurrentUserId(userId);
                    System.out.println("User logged in successfully. User ID: " + userId);
                    return true;
                }
                System.out.println("Login failed: Invalid credentials");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean registerUser(String name, String email, String username, 
                                     String password, String phone) {
        String query = "INSERT INTO users (name, email, username, password, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.setString(5, phone);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public static int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }

    private static void createDonationsTableIfNotExists() throws SQLException {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS donations (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "amount DOUBLE NOT NULL, " +
            "payment_method VARCHAR(50) NOT NULL, " +
            "donation_type VARCHAR(50) NOT NULL, " +
            "purpose VARCHAR(100) NOT NULL, " +
            "message TEXT, " +
            "date DATE NOT NULL, " +
            "status VARCHAR(20) NOT NULL, " +
            "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")";
            
        try (Connection conn = getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
        }
    }

    public static boolean addDonation(int userId, double amount, String paymentMethod, String donationType, 
                                    String purpose, String message) {
        System.out.println("Adding donation to database:");
        System.out.println("User ID: " + userId);
        System.out.println("Amount: " + amount);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Donation Type: " + donationType);
        System.out.println("Purpose: " + purpose);
        
        try {
            // Ensure the donations table exists
            createDonationsTableIfNotExists();
            
            // Validate inputs
            if (userId <= 0) {
                System.err.println("Invalid user ID: " + userId);
                return false;
            }
            
            if (amount <= 0) {
                System.err.println("Invalid donation amount: " + amount);
                return false;
            }
            
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                System.err.println("Payment method is required");
                return false;
            }
            
            if (donationType == null || donationType.trim().isEmpty()) {
                System.err.println("Donation type is required");
                return false;
            }
            
            if (purpose == null || purpose.trim().isEmpty()) {
                System.err.println("Purpose is required");
                return false;
            }
            
            String query = "INSERT INTO donations (user_id, amount, payment_method, donation_type, purpose, message, date, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, 'Completed')";
                          
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                stmt.setDouble(2, amount);
                stmt.setString(3, paymentMethod);
                stmt.setString(4, donationType);
                stmt.setString(5, purpose);
                stmt.setString(6, message != null ? message : "");
                
                // Execute the query
                System.out.println("Executing SQL query...");
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Donation added successfully");
                    return true;
                } else {
                    System.err.println("No rows affected when adding donation");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding donation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static ObservableList<Donation> getUserDonations(int userId) {
        ObservableList<Donation> donations = FXCollections.observableArrayList();
        String query = "SELECT * FROM donations WHERE user_id = ? ORDER BY date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Donation donation = new Donation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("donation_type"),
                        rs.getString("purpose"),
                        rs.getString("message"),
                        rs.getTimestamp("date"),
                        rs.getString("status")
                    );
                    donations.add(donation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user donations: " + e.getMessage());
        }
        
        return donations;
    }
} 
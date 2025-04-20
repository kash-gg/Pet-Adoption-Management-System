package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petpassion?createDatabaseIfNotExists=true&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sh1v@m2003";
    private static final int MAX_POOL_SIZE = 10;
    private static final int CONNECTION_TIMEOUT = 5; // seconds
    
    private static BlockingQueue<Connection> connectionPool;
    private static int currentUserId = -1;

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Initialize the connection pool
            initializeConnectionPool();
            
            // Execute schema.sql to create all tables
            executeSchema();
            
            // Create tables if they don't exist
            createUsersTableIfNotExists();
            createDonationsTableIfNotExists();
            createAdoptionApplicationsTableIfNotExists();
            createVolunteersTableIfNotExists();
            
            System.out.println("DatabaseUtil initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing DatabaseUtil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeConnectionPool() {
        connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            try {
                connectionPool.offer(createConnection());
            } catch (SQLException e) {
                System.err.println("Failed to create connection pool: " + e.getMessage());
            }
        }
    }

    private static Connection createConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        props.setProperty("useSSL", "false");
        props.setProperty("allowPublicKeyRetrieval", "true");
        props.setProperty("createDatabaseIfNotExists", "true");
        props.setProperty("autoReconnect", "true");
        try {
            Connection conn = DriverManager.getConnection(DB_URL, props);
            System.out.println("Successfully connected to database");
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to create database connection: " + e.getMessage());
            System.err.println("Connection URL: " + DB_URL);
            System.err.println("Username: " + DB_USER);
            e.printStackTrace();
            throw e;
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            if (conn == null || conn.isClosed()) {
                conn = createConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            throw new SQLException("Timeout waiting for database connection", e);
        }
    }

    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                }
            } catch (SQLException e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
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
        System.out.println("Attempting to validate user: " + username);
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("Database connection established");
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                System.out.println("Executing query with username: " + username);
                
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
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            releaseConnection(conn);
        }
    }
    
    public static boolean registerUser(String name, String email, String username, 
                                     String password, String phone) {
        String query = "INSERT INTO users (name, email, username, password, phone, role) VALUES (?, ?, ?, ?, ?, 'adopter')";
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("Attempting to register user: " + username);
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, username);
                stmt.setString(4, password);
                stmt.setString(5, phone);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User registered successfully: " + username);
                    return true;
                } else {
                    System.out.println("User registration failed: No rows affected");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            releaseConnection(conn);
        }
    }
    
    public static int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        } finally {
            releaseConnection(conn);
        }
        return -1;
    }

    private static void createDonationsTableIfNotExists() throws SQLException {
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS donations (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "donor_name VARCHAR(100) NOT NULL, " +
            "amount DOUBLE NOT NULL, " +
            "payment_method VARCHAR(50) NOT NULL, " +
            "donation_type VARCHAR(50) NOT NULL, " +
            "purpose VARCHAR(100) NOT NULL, " +
            "message TEXT, " +
            "date DATE NOT NULL, " +
            "status VARCHAR(20) NOT NULL, " +
            "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")";
            
        Connection conn = null;
        try {
            conn = getConnection();
            try (var stmt = conn.createStatement()) {
                stmt.execute(createTableQuery);
            }
        } catch (SQLException e) {
            System.err.println("Error creating donations table: " + e.getMessage());
        } finally {
            releaseConnection(conn);
        }
    }

    public static boolean addDonation(int userId, double amount, String paymentMethod, String donationType, String purpose, String message, String donorName) {
        // First validate that the user exists
        try {
            if (!userExists(userId)) {
                System.err.println("Failed to add donation: User ID " + userId + " does not exist");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }

        // Validate inputs
        if (donorName == null || donorName.trim().isEmpty()) {
            System.err.println("Failed to add donation: Donor name cannot be empty");
            return false;
        }
        if (amount <= 0) {
            System.err.println("Failed to add donation: Amount must be greater than 0");
            return false;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            System.err.println("Failed to add donation: Payment method cannot be empty");
            return false;
        }
        if (donationType == null || donationType.trim().isEmpty()) {
            System.err.println("Failed to add donation: Donation type cannot be empty");
            return false;
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            System.err.println("Failed to add donation: Purpose cannot be empty");
            return false;
        }

        Connection conn = null;
        try {
            // Ensure donations table exists
            createDonationsTableIfNotExists();
            
            conn = getConnection();
            if (conn == null) {
                System.err.println("Failed to add donation: Could not obtain database connection");
                return false;
            }

            String sql = "INSERT INTO donations (user_id, donor_name, amount, payment_method, donation_type, purpose, message, date, status) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 'PENDING')";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, donorName);
                pstmt.setDouble(3, amount);
                pstmt.setString(4, paymentMethod);
                pstmt.setString(5, donationType);
                pstmt.setString(6, purpose);
                pstmt.setString(7, message != null ? message : "");
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Successfully added donation for user " + userId);
                    return true;
                } else {
                    System.err.println("Failed to add donation: No rows were affected");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while adding donation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    releaseConnection(conn);
                } catch (Exception e) {
                    System.err.println("Error releasing connection: " + e.getMessage());
                }
            }
        }
    }

    private static boolean userExists(int userId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn == null) {
                throw new SQLException("Could not obtain database connection");
            }
            
            String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
            return false;
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }
    
    public static ObservableList<Donation> getUserDonations(int userId) {
        ObservableList<Donation> donations = FXCollections.observableArrayList();
        String query = "SELECT * FROM donations WHERE user_id = ? ORDER BY date DESC";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Donation donation = new Donation(
                            rs.getInt("id"),
                            rs.getString("donor_name"),
                            rs.getDouble("amount"),
                            rs.getString("payment_method"),
                            rs.getString("donation_type"),
                            rs.getString("purpose"),
                            rs.getString("message"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("status"),
                            rs.getInt("user_id")
                        );
                        donations.add(donation);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user donations: " + e.getMessage());
        } finally {
            releaseConnection(conn);
        }
        
        return donations;
    }

    public static ObservableList<Donation> getRecentDonations(int limit) throws SQLException {
        ObservableList<Donation> donations = FXCollections.observableArrayList();
        String query = "SELECT d.*, u.name as donor_name FROM donations d " +
                      "JOIN users u ON d.user_id = u.id " +
                      "ORDER BY d.date DESC LIMIT ?";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, limit);
                var rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Donation donation = new Donation(
                        rs.getInt("id"),
                        rs.getString("donor_name"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("donation_type"),
                        rs.getString("purpose"),
                        rs.getString("message"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("status"),
                        rs.getInt("user_id")
                    );
                    donations.add(donation);
                }
            }
        } finally {
            releaseConnection(conn);
        }
        
        return donations;
    }

    public static ObservableList<Pet> getRecentAdoptions(int limit) throws SQLException {
        ObservableList<Pet> pets = FXCollections.observableArrayList();
        String query = "SELECT p.*, a.applicant_name, a.application_date FROM pets p " +
                      "JOIN adoptions a ON p.id = a.pet_id " +
                      "WHERE a.status = 'approved' " +
                      "ORDER BY a.application_date DESC LIMIT ?";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, limit);
                var rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Pet pet = new Pet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getString("breed"),
                        rs.getInt("age"),
                        rs.getString("size"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getString("image_url")
                    );
                    pets.add(pet);
                }
            }
        } finally {
            releaseConnection(conn);
        }
        
        return pets;
    }

    private static void createUsersTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            + "id INT PRIMARY KEY AUTO_INCREMENT,"
            + "name VARCHAR(100) NOT NULL,"
            + "email VARCHAR(100) NOT NULL,"
            + "username VARCHAR(50) NOT NULL,"
            + "password VARCHAR(100) NOT NULL,"
            + "phone VARCHAR(20),"
            + "role VARCHAR(20) NOT NULL DEFAULT 'adopter',"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Users table created or verified successfully");
                
                // Add test user if not exists
                String checkUserSQL = "SELECT COUNT(*) FROM users WHERE username = 'testuser'";
                try (ResultSet rs = stmt.executeQuery(checkUserSQL)) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        String insertUserSQL = "INSERT INTO users (name, email, username, password, role) "
                            + "VALUES ('Test User', 'test@example.com', 'testuser', 'password123', 'adopter')";
                        stmt.execute(insertUserSQL);
                        System.out.println("Test user created successfully");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
    }

    private static void createAdoptionApplicationsTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS adoption_applications ("
            + "id INT PRIMARY KEY AUTO_INCREMENT,"
            + "pet_id INT NOT NULL,"
            + "applicant_name VARCHAR(100) NOT NULL,"
            + "email VARCHAR(100) NOT NULL,"
            + "phone VARCHAR(20) NOT NULL,"
            + "address TEXT NOT NULL,"
            + "experience TEXT,"
            + "reason TEXT,"
            + "application_date DATE NOT NULL,"
            + "status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',"
            + "shelter_id INT NOT NULL"
            + ")";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Adoption applications table created or verified successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error creating adoption applications table: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
    }

    private static void createVolunteersTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS volunteers ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "volunteer_id VARCHAR(20) NOT NULL,"
            + "name VARCHAR(100) NOT NULL,"
            + "role VARCHAR(50) NOT NULL,"
            + "phone_number VARCHAR(20) NOT NULL,"
            + "email VARCHAR(100) NOT NULL,"
            + "event_id INT NOT NULL,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (event_id) REFERENCES events(id)"
            + ")";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Volunteers table created or verified successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error creating volunteers table: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
    }

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events ORDER BY event_date DESC";
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getString("location"),
                        rs.getInt("max_participants"),
                        rs.getInt("created_by")
                    );
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
        } finally {
            releaseConnection(conn);
        }
        return events;
    }

    private static void executeSchema() {
        Connection conn = null;
        try {
            conn = getConnection();
            // Read schema.sql file
            String schemaPath = "/schema.sql";
            try (var inputStream = DatabaseUtil.class.getResourceAsStream(schemaPath)) {
                if (inputStream == null) {
                    throw new RuntimeException("Could not find schema.sql in resources");
                }
                String schema = new String(inputStream.readAllBytes());
                
                // Split the schema into individual statements
                String[] statements = schema.split(";");
                
                // Execute each statement
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(statement);
                        }
                    }
                }
                System.out.println("Schema executed successfully");
            }
        } catch (Exception e) {
            System.err.println("Error executing schema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
    }

    public static boolean removePetByName(String petName) {
        String sql = "DELETE FROM pets WHERE name = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, petName);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error removing pet: " + e.getMessage());
            return false;
        } finally {
            releaseConnection(conn);
        }
    }
}
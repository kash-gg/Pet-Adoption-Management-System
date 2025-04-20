package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import java.sql.SQLException;

public class App extends Application {
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;
    
    private static boolean isDarkMode = false;
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }
    
    @Override
    public void start(Stage stage) {
        start(stage, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
    
    public void start(Stage stage, double width, double height) {
        showLoginScreen(stage);
    }
    
    private void showLoginScreen(Stage stage) {
        // Create login form
        VBox loginForm = createLoginForm(stage);
        
        // Create scene
        Scene scene = new Scene(loginForm, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Pet Passion - Login");
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createLoginForm(Stage stage) {
        VBox form = new VBox(20);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40));
        form.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Pet Passion");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.rgb(44, 62, 80));
        
        // Login container
        VBox loginContainer = new VBox(15);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setPadding(new Insets(30));
        loginContainer.setMaxWidth(400);
        loginContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label subtitleLabel = new Label("Welcome back!");
        subtitleLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 18));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleTextField(usernameField);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleTextField(passwordField);
        
        Button loginButton = new Button("Login");
        styleButton(loginButton, true);
        
        // Register link
        Button registerButton = new Button("Create Account");
        styleButton(registerButton, false);
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            
            try {
                if (DatabaseUtil.validateUser(username, password)) {
                    showDashboard(stage);
                } else {
                    showAlert("Invalid username or password");
                }
            } catch (Exception ex) {
                showAlert("Error during login: " + ex.getMessage());
            }
        });
        
        registerButton.setOnAction(e -> {
            try {
                new SecondaryController().showRegisterScreen(stage, WINDOW_WIDTH, WINDOW_HEIGHT);
            } catch (Exception ex) {
                showAlert("Error opening registration screen: " + ex.getMessage());
            }
        });
        
        loginContainer.getChildren().addAll(
            subtitleLabel,
            usernameField,
            passwordField,
            loginButton,
            new Separator(),
            registerButton
        );
        
        form.getChildren().addAll(titleLabel, loginContainer);
        return form;
    }
    
    private void showDashboard(Stage stage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Create sidebar
        VBox sidebar = createSidebar(stage);
        mainLayout.setLeft(sidebar);
        
        // Create content area
        VBox content = createContent(stage);
        mainLayout.setCenter(content);
        
        // Create scene
        Scene scene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Pet Passion - Dashboard");
        stage.setScene(scene);
    }
    
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(250);
        updateSidebarStyle(sidebar);
        
        // Logo
        Label logo = new Label("Pet Passion");
        logo.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        logo.setTextFill(Color.WHITE);
        
        // Navigation buttons
        Button dashboardBtn = createNavButton("Dashboard", true);
        Button petsBtn = createNavButton("Pets", false);
        Button adoptionsBtn = createNavButton("Adoptions", false);
        Button donationsBtn = createNavButton("Donations", false);
        
        // Dark Mode Toggle
        HBox darkModeBox = new HBox(10);
        darkModeBox.setAlignment(Pos.CENTER_LEFT);
        Label darkModeLabel = new Label("Dark Mode");
        darkModeLabel.setTextFill(Color.WHITE);
        ToggleButton darkModeToggle = new ToggleButton();
        darkModeToggle.setSelected(isDarkMode);
        darkModeToggle.setStyle(
            "-fx-background-radius: 12;" +
            "-fx-pref-width: 40;" +
            "-fx-pref-height: 20;" +
            "-fx-background-color: " + (isDarkMode ? "#2ecc71" : "#95a5a6") + ";"
        );
        
        darkModeToggle.setOnAction(e -> {
            isDarkMode = darkModeToggle.isSelected();
            updateSidebarStyle(sidebar);
            updateMainStyle(stage.getScene().getRoot());
            darkModeToggle.setStyle(
                "-fx-background-radius: 12;" +
                "-fx-pref-width: 40;" +
                "-fx-pref-height: 20;" +
                "-fx-background-color: " + (isDarkMode ? "#2ecc71" : "#95a5a6") + ";"
            );
        });
        
        darkModeBox.getChildren().addAll(darkModeLabel, darkModeToggle);
        
        // Sign out button
        Button signOutBtn = new Button("Sign Out");
        signOutBtn.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        // Add button actions
        dashboardBtn.setOnAction(e -> showDashboard(stage));
        petsBtn.setOnAction(e -> new PetController().showPetsTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT));
        adoptionsBtn.setOnAction(e -> new AdoptionController().showAdoptionTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT));
        donationsBtn.setOnAction(e -> new DonationController().showDonationTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT));
        signOutBtn.setOnAction(e -> {
            DatabaseUtil.setCurrentUserId(-1); // Reset user session
            showLoginScreen(stage);
        });
        
        // Add spacing before sign out button
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(
            logo,
            new Separator(),
            dashboardBtn,
            petsBtn,
            adoptionsBtn,
            donationsBtn,
            darkModeBox,
            spacer,
            signOutBtn
        );
        
        return sidebar;
    }
    
    private void updateSidebarStyle(VBox sidebar) {
        sidebar.setStyle(
            "-fx-background-color: " + (isDarkMode ? "#1a1a1a" : "#2b3467") + ";" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 5, 0);"
        );
    }
    
    private void updateMainStyle(Parent root) {
        if (root instanceof BorderPane) {
            BorderPane mainLayout = (BorderPane) root;
            mainLayout.setStyle("-fx-background-color: " + (isDarkMode ? "#2c2c2c" : "#f5f5f5") + ";");
            
            // Update content area if it exists
            Node center = mainLayout.getCenter();
            if (center instanceof VBox) {
                VBox content = (VBox) center;
                for (Node node : content.getChildren()) {
                    if (node instanceof Label) {
                        ((Label) node).setTextFill(isDarkMode ? Color.WHITE : Color.rgb(44, 62, 80));
                    }
                }
            }
        }
    }
    
    private VBox createContent(Stage stage) {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome to Pet Passion");
        welcomeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.rgb(44, 62, 80));
        
        Label subtitleLabel = new Label("Choose an action to get started");
        subtitleLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.rgb(108, 117, 125));
        
        // Action cards
        GridPane actionCards = createActionCards(stage);
        
        // Recent Activities Section
        Label recentActivitiesLabel = new Label("Recent Activities");
        recentActivitiesLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        recentActivitiesLabel.setTextFill(Color.rgb(44, 62, 80));
        
        // Create HBox for recent activities tables
        HBox recentActivities = new HBox(20);
        recentActivities.setAlignment(Pos.CENTER);
        
        // Recent Adoptions Table
        VBox adoptionsBox = createRecentAdoptionsBox();
        HBox.setHgrow(adoptionsBox, Priority.ALWAYS);
        
        // Recent Donations Table
        VBox donationsBox = createRecentDonationsBox();
        HBox.setHgrow(donationsBox, Priority.ALWAYS);
        
        recentActivities.getChildren().addAll(adoptionsBox, donationsBox);
        
        content.getChildren().addAll(welcomeLabel, subtitleLabel, actionCards, recentActivitiesLabel, recentActivities);
        return content;
    }
    
    private GridPane createActionCards(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        // Create action cards
        VBox findPetCard = createActionCard(
            "Find a Pet",
            "Browse our available pets and find your perfect companion",
            e -> new PetController().showPetsTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT)
        );
        
        VBox managePetsCard = createActionCard(
            "Manage Pets",
            "View and manage all pets in a table format",
            e -> new PetTableController().showPetTable(stage, WINDOW_WIDTH, WINDOW_HEIGHT)
        );
        
        VBox donateCard = createActionCard(
            "Make a Donation",
            "Support our mission by making a donation",
            e -> new DonationController().showDonationTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT)
        );
        
        VBox adoptionProcessCard = createActionCard(
            "Adoption Process",
            "Learn about our adoption process and requirements",
            e -> new AdoptionController().showAdoptionTab(stage, WINDOW_WIDTH, WINDOW_HEIGHT)
        );
        
        VBox viewAdoptionsCard = createActionCard(
            "View Adoptions",
            "See all adoption applications and their status",
            e -> showAlert("Adoptions table view coming soon!")
        );
        
        VBox viewDonationsCard = createActionCard(
            "View Donations",
            "See all donations and their details",
            e -> showAlert("Donations table view coming soon!")
        );
        
        // Add cards to grid
        grid.add(findPetCard, 0, 0);
        grid.add(managePetsCard, 1, 0);
        grid.add(donateCard, 0, 1);
        grid.add(viewDonationsCard, 1, 1);
        grid.add(adoptionProcessCard, 0, 2);
        grid.add(viewAdoptionsCard, 1, 2);
        
        return grid;
    }
    
    private VBox createActionCard(String title, String description, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(400);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.rgb(44, 62, 80));
        
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.rgb(108, 117, 125));
        
        Button learnMoreBtn = new Button("Learn More");
        styleButton(learnMoreBtn, false);
        learnMoreBtn.setOnAction(action);
        
        card.getChildren().addAll(titleLabel, descLabel, learnMoreBtn);
        return card;
    }
    
    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        updateNavButtonStyle(button, isActive);
        return button;
    }
    
    private void updateNavButtonStyle(Button button, boolean isActive) {
        button.setStyle(
            "-fx-background-color: " + (isActive ? "#3498db" : "transparent") + ";" +
            "-fx-text-fill: " + (isDarkMode ? "#ffffff" : "#ffffff") + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER-LEFT;"
        );
    }
    
    private void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 5;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;"
        );
        field.setPrefHeight(35);
    }
    
    private void styleButton(Button button, boolean isPrimary) {
        button.setStyle(
            "-fx-background-color: " + (isPrimary ? "#2ecc71" : "#3498db") + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        button.setMaxWidth(Double.MAX_VALUE);
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private VBox createRecentAdoptionsBox() {
        VBox box = new VBox(10);
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
            "-fx-padding: 20;"
        );
        
        Label title = new Label("Recent Adoptions");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        title.setTextFill(Color.rgb(44, 62, 80));
        
        VBox tableContent = new VBox(10);
        
        try (var conn = DatabaseUtil.getConnection()) {
            String query = "SELECT a.*, p.name as pet_name, u.name as adopter_name " +
                          "FROM adoption_applications a " +
                          "JOIN pets p ON a.pet_id = p.id " +
                          "JOIN users u ON a.applicant_name = u.name " +
                          "WHERE a.status = 'Approved' " +
                          "ORDER BY a.application_date DESC LIMIT 5";
                          
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(10));
                    row.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                    
                    Label petName = new Label(rs.getString("pet_name"));
                    petName.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
                    petName.setTextFill(Color.rgb(44, 62, 80));
                    
                    Label adopter = new Label("adopted by " + rs.getString("adopter_name"));
                    adopter.setFont(Font.font("Verdana", 14));
                    adopter.setTextFill(Color.rgb(108, 117, 125));
                    
                    Label date = new Label(rs.getString("application_date"));
                    date.setFont(Font.font("Verdana", 12));
                    date.setTextFill(Color.rgb(108, 117, 125));
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    row.getChildren().addAll(petName, adopter, spacer, date);
                    tableContent.getChildren().add(row);
                }
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Could not load recent adoptions");
            errorLabel.setTextFill(Color.RED);
            tableContent.getChildren().add(errorLabel);
        }
        
        if (tableContent.getChildren().isEmpty()) {
            Label noDataLabel = new Label("No recent adoptions");
            noDataLabel.setFont(Font.font("Verdana", 14));
            noDataLabel.setTextFill(Color.rgb(108, 117, 125));
            tableContent.getChildren().add(noDataLabel);
        }
        
        box.getChildren().addAll(title, tableContent);
        return box;
    }
    
    private VBox createRecentDonationsBox() {
        VBox box = new VBox(10);
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
            "-fx-padding: 20;"
        );
        
        Label title = new Label("Recent Donations");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        title.setTextFill(Color.rgb(44, 62, 80));
        
        VBox tableContent = new VBox(10);
        
        try (var conn = DatabaseUtil.getConnection()) {
            String query = "SELECT d.*, u.name as donor_name " +
                          "FROM donations d " +
                          "JOIN users u ON d.user_id = u.id " +
                          "ORDER BY d.date DESC LIMIT 5";
                          
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(10));
                    row.setStyle("-fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
                    
                    Label amount = new Label(String.format("$%.2f", rs.getDouble("amount")));
                    amount.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
                    amount.setTextFill(Color.rgb(46, 204, 113));
                    
                    Label donor = new Label("donated by " + rs.getString("donor_name"));
                    donor.setFont(Font.font("Verdana", 14));
                    donor.setTextFill(Color.rgb(108, 117, 125));
                    
                    Label date = new Label(rs.getString("date"));
                    date.setFont(Font.font("Verdana", 12));
                    date.setTextFill(Color.rgb(108, 117, 125));
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    row.getChildren().addAll(amount, donor, spacer, date);
                    tableContent.getChildren().add(row);
                }
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Could not load recent donations");
            errorLabel.setTextFill(Color.RED);
            tableContent.getChildren().add(errorLabel);
        }
        
        if (tableContent.getChildren().isEmpty()) {
            Label noDataLabel = new Label("No recent donations");
            noDataLabel.setFont(Font.font("Verdana", 14));
            noDataLabel.setTextFill(Color.rgb(108, 117, 125));
            tableContent.getChildren().add(noDataLabel);
        }
        
        box.getChildren().addAll(title, tableContent);
        return box;
    }
    
    public static void main(String[] args) {
        launch();
    }
}


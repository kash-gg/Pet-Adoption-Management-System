package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public class DashboardController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    private Stage stage;
    private double width;
    private double height;
    private int userId;
    private BorderPane mainLayout;
    private VBox sidebar;
    private VBox content;

    public void showDashboard(Stage stage, double width, double height, int userId) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.userId = userId;

        mainLayout = new BorderPane();
        updateMainStyle();
        
        // Create sidebar
        sidebar = createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Create content area
        content = createContent();
        mainLayout.setCenter(content);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Dashboard");
        stage.setScene(scene);
    }

    private VBox createSidebar() {
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
        Button eventsBtn = createNavButton("Events", false);
        
        // Dark Mode Toggle
        HBox darkModeBox = new HBox(10);
        darkModeBox.setAlignment(Pos.CENTER_LEFT);
        Label darkModeLabel = new Label("Dark Mode");
        darkModeLabel.setTextFill(Color.WHITE);
        ToggleButton darkModeToggle = new ToggleButton();
        darkModeToggle.setSelected(App.isDarkMode());
        updateDarkModeToggleStyle(darkModeToggle);
        
        darkModeToggle.setOnAction(e -> {
            App.setDarkMode(darkModeToggle.isSelected());
            updateSidebarStyle(sidebar);
            updateMainStyle();
            updateDarkModeToggleStyle(darkModeToggle);
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
        dashboardBtn.setOnAction(e -> showDashboard(stage, width, height, userId));
        petsBtn.setOnAction(e -> new PetController().showPetsTab(stage, width, height));
        adoptionsBtn.setOnAction(e -> new AdoptionController().showAdoptionTab(stage, width, height));
        donationsBtn.setOnAction(e -> new DonationController().showDonationTab(stage, width, height));
        eventsBtn.setOnAction(e -> showEventsTab(stage, width, height));
        signOutBtn.setOnAction(e -> {
            DatabaseUtil.setCurrentUserId(-1); // Reset user session
            new App().start(stage, width, height);
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
            eventsBtn,
            darkModeBox,
            spacer,
            signOutBtn
        );
        
        return sidebar;
    }
    
    private void updateSidebarStyle(VBox sidebar) {
        sidebar.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#1a1a1a" : "#2b3467") + ";" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 5, 0);"
        );
    }
    
    private void updateMainStyle() {
        mainLayout.setStyle("-fx-background-color: " + (App.isDarkMode() ? "#2c2c2c" : "#f5f5f5") + ";");
    }
    
    private void updateDarkModeToggleStyle(ToggleButton toggle) {
        toggle.setStyle(
            "-fx-background-radius: 12;" +
            "-fx-pref-width: 40;" +
            "-fx-pref-height: 20;" +
            "-fx-background-color: " + (App.isDarkMode() ? "#2ecc71" : "#95a5a6") + ";"
        );
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
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-alignment: CENTER-LEFT;"
        );
    }
    
    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        
        // Welcome section
        Label welcomeLabel = new Label("Welcome to Pet Passion");
        welcomeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        
        Label subtitleLabel = new Label("Choose an action to get started");
        subtitleLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(App.isDarkMode() ? Color.rgb(200, 200, 200) : Color.rgb(108, 117, 125));
        
        // Action cards in a ScrollPane
        ScrollPane actionScrollPane = new ScrollPane();
        actionScrollPane.setFitToWidth(true);
        actionScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        GridPane actionCards = createActionCards();
        actionScrollPane.setContent(actionCards);
        
        // Add all components to main content
        content.getChildren().addAll(
            welcomeLabel,
            subtitleLabel,
            actionScrollPane
        );
        
        return content;
    }
    
    private GridPane createActionCards() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        // Create action cards
        VBox findPetCard = createActionCard(
            "Find a Pet",
            "Browse our available pets and find your perfect companion",
            e -> new PetController().showPetsTab(stage, width, height)
        );
        
        VBox addPetCard = createActionCard(
            "Add a Pet for Adoption",
            "Add a new pet to our adoption program",
            e -> new PetController().showAddPetForm(stage, width, height)
        );
        
        VBox donateCard = createActionCard(
            "Make a Donation",
            "Support our mission by making a donation",
            e -> new DonationController().showDonationTab(stage, width, height)
        );
        
        VBox adoptionCard = createActionCard(
            "Adoption Process",
            "Learn about our adoption process and requirements",
            e -> new AdoptionController().showAdoptionTab(stage, width, height)
        );
        
        VBox storiesCard = createActionCard(
            "Success Stories",
            "Read heartwarming stories of successful adoptions",
            e -> showAlert("Coming soon!")
        );
        
        // Add cards to grid
        grid.add(findPetCard, 0, 0);
        grid.add(addPetCard, 1, 0);
        grid.add(donateCard, 0, 1);
        grid.add(adoptionCard, 1, 1);
        grid.add(storiesCard, 0, 2);
        
        return grid;
    }
    
    private VBox createActionCard(String title, String description, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(400);
        card.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setTextFill(App.isDarkMode() ? Color.rgb(200, 200, 200) : Color.rgb(108, 117, 125));
        
        Button learnMoreBtn = new Button("Learn More");
        learnMoreBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        learnMoreBtn.setMaxWidth(Double.MAX_VALUE);
        learnMoreBtn.setOnAction(action);
        
        card.getChildren().addAll(titleLabel, descLabel, learnMoreBtn);
        return card;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showEventsTab(Stage stage, double width, double height) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + (App.isDarkMode() ? "#2c2c2c" : "#f5f5f5") + ";");
        
        // Create sidebar
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Create events content
        new EventController().showEventsTab(stage, width, height);
    }
}
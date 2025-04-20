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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShelterController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    public void showShelterTab(Stage stage, double width, double height) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + (App.isDarkMode() ? "#2c2c2c" : "#f5f5f5") + ";");
        
        // Create back button
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "#2c3e50") + ";" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> {
            try {
                new DashboardController().showDashboard(stage, width, height, DatabaseUtil.getCurrentUserId());
            } catch (Exception ex) {
                showAlert("Error returning to dashboard: " + ex.getMessage());
            }
        });
        
        // Create search and filter section
        VBox searchBox = createSearchBox();
        
        // Add back button and search box to top container
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, searchBox);
        mainLayout.setTop(topContainer);
        
        // Create shelters grid
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        FlowPane sheltersGrid = createSheltersGrid();
        scrollPane.setContent(sheltersGrid);
        mainLayout.setCenter(scrollPane);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Shelters");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSearchBox() {
        VBox searchBox = new VBox(20);
        searchBox.setPadding(new Insets(20));
        searchBox.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Title
        Label titleLabel = new Label("Find a Shelter");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(59, 66, 82));
        
        // Search field with icon
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#444444" : "#f0f0f0") + ";" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10;"
        );
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search shelters...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        searchField.setPrefWidth(300);
        
        searchContainer.getChildren().add(searchField);
        
        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> locationFilter = createStyledComboBox("Location", "All", "North", "South", "East", "West");
        ComboBox<String> typeFilter = createStyledComboBox("Type", "All", "Public", "Private", "Non-Profit");
        ComboBox<String> ratingFilter = createStyledComboBox("Rating", "All", "5 Stars", "4 Stars", "3 Stars");
        
        filterBox.getChildren().addAll(locationFilter, typeFilter, ratingFilter);
        
        searchBox.getChildren().addAll(titleLabel, searchContainer, filterBox);
        return searchBox;
    }

    private ComboBox<String> createStyledComboBox(String prompt, String... items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        comboBox.getItems().addAll(items);
        comboBox.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#444444" : "white") + ";" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "black") + ";" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 5 10;" +
            "-fx-font-size: 14px;"
        );
        return comboBox;
    }

    private FlowPane createSheltersGrid() {
        FlowPane grid = new FlowPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        
        try {
            List<Shelter> shelters = loadSheltersFromDatabase();
            for (Shelter shelter : shelters) {
                VBox shelterCard = createShelterCard(shelter);
                grid.getChildren().add(shelterCard);
            }
        } catch (SQLException e) {
            showAlert("Error loading shelters: " + e.getMessage());
        }
        
        return grid;
    }

    private VBox createShelterCard(Shelter shelter) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setPrefWidth(300);
        
        // Shelter Image
        ImageView shelterImage = createShelterImageView(shelter.getImageUrl());
        shelterImage.setFitWidth(270);
        shelterImage.setFitHeight(180);
        
        // Clip image to rounded corners
        Rectangle clip = new Rectangle(270, 180);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        shelterImage.setClip(clip);
        
        // Shelter Info
        Label nameLabel = new Label(shelter.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(59, 66, 82));
        
        Label detailsLabel = new Label(
            shelter.getLocation() + "\n" +
            "Type: " + shelter.getType() + "\n" +
            "Rating: " + shelter.getRating() + " ★\n" +
            "Capacity: " + shelter.getCapacity() + " pets"
        );
        detailsLabel.setFont(Font.font("Arial", 14));
        detailsLabel.setTextFill(App.isDarkMode() ? Color.rgb(200, 200, 200) : Color.rgb(76, 86, 106));
        
        // Visit Button
        Button visitButton = new Button("Visit Shelter");
        visitButton.setStyle(
            "-fx-background-color: #5e81ac;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20;"
        );
        visitButton.setOnAction(e -> showShelterDetails(shelter));
        
        card.getChildren().addAll(shelterImage, nameLabel, detailsLabel, visitButton);
        card.setAlignment(Pos.CENTER);
        
        return card;
    }

    private ImageView createShelterImageView(String imageUrl) {
        ImageView imageView = new ImageView();
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String resourcePath = "/images/shelters/" + imageUrl;
                var imageStream = getClass().getResourceAsStream(resourcePath);
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-shelter.jpg")));
                }
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-shelter.jpg")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-shelter.jpg")));
            } catch (Exception ex) {
                System.err.println("Failed to load default image: " + ex.getMessage());
            }
        }
        return imageView;
    }

    private void showShelterDetails(Shelter shelter) {
        Stage detailsStage = new Stage();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + (App.isDarkMode() ? "#2c2c2c" : "white") + ";");
        
        // Image section
        ImageView shelterImage = createShelterImageView(shelter.getImageUrl());
        shelterImage.setFitWidth(400);
        shelterImage.setFitHeight(300);
        
        VBox imageBox = new VBox(shelterImage);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(20));
        mainLayout.setLeft(imageBox);
        
        // Details section
        VBox detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setAlignment(Pos.TOP_LEFT);
        
        Label nameLabel = new Label(shelter.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(59, 66, 82));
        
        Label detailsLabel = new Label(
            "Location: " + shelter.getLocation() + "\n" +
            "Type: " + shelter.getType() + "\n" +
            "Rating: " + shelter.getRating() + " ★\n" +
            "Capacity: " + shelter.getCapacity() + " pets\n\n" +
            "Description:\n" + shelter.getDescription() + "\n\n" +
            "Contact:\n" +
            "Phone: " + shelter.getPhone() + "\n" +
            "Email: " + shelter.getEmail() + "\n" +
            "Address: " + shelter.getAddress()
        );
        detailsLabel.setFont(Font.font("Arial", 16));
        detailsLabel.setTextFill(App.isDarkMode() ? Color.rgb(200, 200, 200) : Color.rgb(76, 86, 106));
        detailsLabel.setWrapText(true);
        
        Button contactButton = new Button("Contact Shelter");
        contactButton.setStyle(
            "-fx-background-color: #5e81ac;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 30;"
        );
        contactButton.setOnAction(e -> showContactForm(shelter));
        
        detailsBox.getChildren().addAll(nameLabel, detailsLabel, contactButton);
        mainLayout.setCenter(detailsBox);
        
        Scene scene = new Scene(mainLayout, 800, 500);
        detailsStage.setTitle("Shelter Details - " + shelter.getName());
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void showContactForm(Shelter shelter) {
        Stage formStage = new Stage();
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(30));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label("Contact " + shelter.getName());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(59, 66, 82));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Your Email");
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Your Message");
        messageArea.setPrefRowCount(4);
        
        Button submitButton = new Button("Send Message");
        submitButton.setStyle(
            "-fx-background-color: #5e81ac;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 30;"
        );
        submitButton.setOnAction(e -> {
            showAlert("Message sent successfully!");
            formStage.close();
        });
        
        formBox.getChildren().addAll(titleLabel, nameField, emailField, messageArea, submitButton);
        
        Scene scene = new Scene(formBox, 400, 400);
        formStage.setTitle("Contact Shelter");
        formStage.setScene(scene);
        formStage.show();
    }

    private List<Shelter> loadSheltersFromDatabase() throws SQLException {
        List<Shelter> shelters = new ArrayList<>();
        // This is a placeholder - you would implement actual database loading here
        shelters.add(new Shelter(
            1, "Happy Paws Shelter", "North", "Non-Profit", 4.5,
            50, "A loving home for pets in need", "123-456-7890",
            "info@happypaws.org", "123 Pet Street, North City"
        ));
        shelters.add(new Shelter(
            2, "Safe Haven Animal Rescue", "South", "Public", 4.8,
            75, "Dedicated to rescuing and rehabilitating animals", "987-654-3210",
            "contact@safehaven.org", "456 Rescue Road, South City"
        ));
        return shelters;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
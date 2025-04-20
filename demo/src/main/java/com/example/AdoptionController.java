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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdoptionController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    public void showAdoptionTab(Stage stage, double width, double height) {
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
        
        // Create pets grid
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        FlowPane petsGrid = createPetsGrid();
        scrollPane.setContent(petsGrid);
        mainLayout.setCenter(scrollPane);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - View Pets for Adoption");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSearchBox() {
        VBox searchBox = new VBox(20);
        searchBox.setPadding(new Insets(20));
        searchBox.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Title
        Label titleLabel = new Label("Find Your Perfect Pet");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.rgb(59, 66, 82));
        
        // Search field with icon
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 20; -fx-padding: 10;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search pets...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        searchField.setPrefWidth(300);
        
        searchContainer.getChildren().add(searchField);
        
        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> speciesFilter = createStyledComboBox("Species", "All", "Dog", "Cat", "Bird", "Other");
        ComboBox<String> ageFilter = createStyledComboBox("Age", "All", "Puppy/Kitten", "Young", "Adult", "Senior");
        ComboBox<String> sizeFilter = createStyledComboBox("Size", "All", "Small", "Medium", "Large");
        
        filterBox.getChildren().addAll(speciesFilter, ageFilter, sizeFilter);
        
        searchBox.getChildren().addAll(titleLabel, searchContainer, filterBox);
        return searchBox;
    }

    private ComboBox<String> createStyledComboBox(String prompt, String... items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        comboBox.getItems().addAll(items);
        comboBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 5 10;" +
            "-fx-font-size: 14px;"
        );
        return comboBox;
    }

    private FlowPane createPetsGrid() {
        FlowPane grid = new FlowPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        
        try {
            ObservableList<Pet> pets = loadPetsFromDatabase();
            for (Pet pet : pets) {
                VBox petCard = createPetCard(pet);
                grid.getChildren().add(petCard);
            }
        } catch (SQLException e) {
            showAlert("Error loading pets: " + e.getMessage());
        }
        
        return grid;
    }

    private VBox createPetCard(Pet pet) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setPrefWidth(250);
        
        // Pet Image
        ImageView petImage = createPetImageView(pet.getImageUrl());
        petImage.setFitWidth(220);
        petImage.setFitHeight(220);
        
        // Clip image to rounded corners
        Rectangle clip = new Rectangle(220, 220);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        petImage.setClip(clip);
        
        // Pet Info
        Label nameLabel = new Label(pet.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.rgb(59, 66, 82));
        
        Label detailsLabel = new Label(
            pet.getSpecies() + " • " + 
            pet.getBreed() + "\n" +
            "Age: " + pet.getAge() + " years\n" +
            "Size: " + pet.getSize()
        );
        detailsLabel.setFont(Font.font("Arial", 14));
        detailsLabel.setTextFill(Color.rgb(76, 86, 106));
        
        // Adopt Button
        Button adoptButton = new Button("Adopt Me");
        adoptButton.setStyle(
            "-fx-background-color: #5e81ac;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20;"
        );
        adoptButton.setOnAction(e -> showAdoptionForm(pet));
        
        card.getChildren().addAll(petImage, nameLabel, detailsLabel, adoptButton);
        card.setAlignment(Pos.CENTER);
        
        return card;
    }

    private ImageView createPetImageView(String imageUrl) {
        ImageView imageView = new ImageView();
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // First try to load directly from the resources directory
                String resourcePath = "/images/" + (imageUrl.contains("/") ? imageUrl.substring(imageUrl.lastIndexOf("/") + 1) : imageUrl);
                var imageStream = getClass().getResourceAsStream(resourcePath);
                
                if (imageStream != null) {
                    imageView.setImage(new Image(imageStream));
                    System.out.println("Successfully loaded image from resources: " + resourcePath);
                } else {
                    // Try loading from the absolute path in the workspace
                    String absolutePath = "file:demo/src/main/resources/images/" + (imageUrl.contains("/") ? imageUrl.substring(imageUrl.lastIndexOf("/") + 1) : imageUrl);
                    try {
                        Image image = new Image(absolutePath);
                        if (!image.isError()) {
                            imageView.setImage(image);
                            System.out.println("Successfully loaded image from absolute path: " + absolutePath);
                        } else {
                            System.err.println("Error loading image from absolute path: " + absolutePath);
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image from absolute path: " + e.getMessage());
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
                    }
                }
            } else {
                System.out.println("No image URL provided, using default image");
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
            } catch (Exception ex) {
                System.err.println("Failed to load default image: " + ex.getMessage());
            }
        }
        
        return imageView;
    }

    private void showPetDetails(Pet pet) {
        Stage detailsStage = new Stage();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: white;");
        
        // Image section
        ImageView petImage = new ImageView();
        try {
            String imageUrl = pet.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Try to load from resources first
                String resourcePath = "/images/" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                var imageStream = getClass().getResourceAsStream(resourcePath);
                if (imageStream != null) {
                    petImage.setImage(new Image(imageStream));
                } else {
                    // Fallback to direct URL if not in resources
                    petImage.setImage(new Image(imageUrl));
                }
            } else {
                petImage.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            petImage.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
        }
        petImage.setFitWidth(400);
        petImage.setFitHeight(400);
        petImage.setPreserveRatio(true);
        
        VBox imageBox = new VBox(petImage);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(20));
        mainLayout.setLeft(imageBox);
        
        // Details section
        VBox detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setAlignment(Pos.TOP_LEFT);
        
        Label nameLabel = new Label(pet.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label detailsLabel = new Label(
            "Species: " + pet.getSpecies() + "\n" +
            "Breed: " + pet.getBreed() + "\n" +
            "Age: " + pet.getAge() + " years\n" +
            "Size: " + pet.getSize() + "\n\n" +
            "Description:\n" + pet.getDescription()
        );
        detailsLabel.setFont(Font.font("Arial", 16));
        detailsLabel.setWrapText(true);
        
        Button adoptButton = new Button("Adopt This Pet");
        adoptButton.setStyle(
            "-fx-background-color: #5e81ac;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 30;"
        );
        adoptButton.setOnAction(e -> showAdoptionForm(pet));
        
        detailsBox.getChildren().addAll(nameLabel, detailsLabel, adoptButton);
        mainLayout.setCenter(detailsBox);
        
        Scene scene = new Scene(mainLayout, 800, 500);
        detailsStage.setTitle("Pet Details - " + pet.getName());
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private ObservableList<Pet> loadPetsFromDatabase() throws SQLException {
        ObservableList<Pet> pets = FXCollections.observableArrayList();
        String query = "SELECT * FROM pets WHERE status = 'Available'";
        
        try (var conn = DatabaseUtil.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            
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
        
        return pets;
    }

    public void showAdoptionForm(Pet pet) {
        Stage formStage = new Stage();
        BorderPane mainLayout = new BorderPane();
        
        // Create form
        VBox formBox = createAdoptionForm(pet);
        mainLayout.setCenter(formBox);
        
        // Create scene
        Scene scene = new Scene(mainLayout, 600, 500);
        formStage.setTitle("Pet Passion - Adoption Application");
        formStage.setScene(scene);
        formStage.show();
    }

    private VBox createAdoptionForm(Pet pet) {
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(20));
        formBox.setAlignment(Pos.CENTER);
        
        // Title
        Label titleLabel = new Label("Adoption Application for " + pet.getName());
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        
        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        
        TextArea addressArea = new TextArea();
        addressArea.setPromptText("Address");
        addressArea.setPrefRowCount(2);
        
        // Shelter selection
        ComboBox<Shelter> shelterComboBox = new ComboBox<>();
        shelterComboBox.setPromptText("Select Shelter");
        try {
            shelterComboBox.setItems(loadSheltersFromDatabase());
        } catch (SQLException e) {
            showAlert("Error loading shelters: " + e.getMessage());
        }
        shelterComboBox.setCellFactory(lv -> new ListCell<Shelter>() {
            @Override
            protected void updateItem(Shelter item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
        shelterComboBox.setButtonCell(new ListCell<Shelter>() {
            @Override
            protected void updateItem(Shelter item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Select Shelter" : item.getName());
            }
        });
        
        TextArea experienceArea = new TextArea();
        experienceArea.setPromptText("Previous pet ownership experience");
        experienceArea.setPrefRowCount(3);
        
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Why do you want to adopt this pet?");
        reasonArea.setPrefRowCount(3);
        
        // Submit button
        Button submitButton = new Button("Submit Application");
        submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            try {
                if (shelterComboBox.getValue() == null) {
                    showAlert("Please select a shelter");
                    return;
                }
                submitAdoptionApplication(
                    pet.getId(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressArea.getText(),
                    experienceArea.getText(),
                    reasonArea.getText(),
                    shelterComboBox.getValue().getId()
                );
                showAlert("Application submitted successfully!");
            } catch (Exception ex) {
                showAlert("Error submitting application: " + ex.getMessage());
            }
        });
        
        // Add all components to form
        formBox.getChildren().addAll(
            titleLabel,
            nameField,
            emailField,
            phoneField,
            addressArea,
            shelterComboBox,
            experienceArea,
            reasonArea,
            submitButton
        );
        
        return formBox;
    }

    private void submitAdoptionApplication(int petId, String name, String email, String phone,
                                         String address, String experience, String reason, int shelterId) throws SQLException {
        String query = "INSERT INTO adoption_applications (pet_id, applicant_name, email, phone, " +
                      "address, experience, reason, application_date, status, shelter_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (var conn = DatabaseUtil.getConnection();
             var stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, petId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, experience);
            stmt.setString(7, reason);
            stmt.setString(8, LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            stmt.setString(9, "Pending");
            stmt.setInt(10, shelterId);
            
            stmt.executeUpdate();
        }
    }

    private ObservableList<Shelter> loadSheltersFromDatabase() throws SQLException {
        ObservableList<Shelter> shelters = FXCollections.observableArrayList();
        String query = "SELECT * FROM shelters";
        
        try (var conn = DatabaseUtil.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Shelter shelter = new Shelter(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("capacity")
                );
                shelters.add(shelter);
            }
        }
        
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
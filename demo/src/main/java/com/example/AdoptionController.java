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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;

public class AdoptionController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    private TableView<Adoption> adoptionsTable;
    @FXML
    private TableView<AdoptionApplication> applicationsTable;
    @FXML
    private TableColumn<AdoptionApplication, String> petNameColumn;
    @FXML
    private TableColumn<AdoptionApplication, String> applicantNameColumn;
    @FXML
    private TableColumn<AdoptionApplication, String> shelterNameColumn;
    @FXML
    private TableColumn<AdoptionApplication, String> statusColumn;
    @FXML
    private TableColumn<AdoptionApplication, LocalDate> applicationDateColumn;

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
                showAlert("Error", "Error returning to dashboard: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
        // Create title section
        Label titleLabel = new Label("Adoption Applications");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        
        // Create applications table
        TableView<AdoptionApplication> applicationsTable = new TableView<>();
        applicationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Define columns
        TableColumn<AdoptionApplication, String> petNameCol = new TableColumn<>("Pet");
        petNameCol.setCellValueFactory(new PropertyValueFactory<>("petName"));
        
        TableColumn<AdoptionApplication, String> applicantCol = new TableColumn<>("Applicant");
        applicantCol.setCellValueFactory(new PropertyValueFactory<>("applicantName"));
        
        TableColumn<AdoptionApplication, String> shelterCol = new TableColumn<>("Shelter");
        shelterCol.setCellValueFactory(new PropertyValueFactory<>("shelterName"));
        
        TableColumn<AdoptionApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<AdoptionApplication, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));
        
        applicationsTable.getColumns().addAll(petNameCol, applicantCol, shelterCol, statusCol, dateCol);
        
        // Load applications
        loadAdoptionApplications(applicationsTable);
        
        // Create new application button
        Button newApplicationBtn = new Button("New Application");
        newApplicationBtn.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        newApplicationBtn.setOnAction(e -> new PetController().showPetsTab(stage, width, height));
        
        // Create top container with back button and title
        HBox topContainer = new HBox(20);
        topContainer.setAlignment(Pos.CENTER_LEFT);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, titleLabel, new Region(), newApplicationBtn);
        HBox.setHgrow(topContainer.getChildren().get(2), Priority.ALWAYS);
        
        // Add padding around the table
        VBox tableContainer = new VBox(applicationsTable);
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        VBox.setVgrow(applicationsTable, Priority.ALWAYS);
        
        // Add components to layout
        mainLayout.setTop(topContainer);
        mainLayout.setCenter(tableContainer);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Adoption Applications");
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
            showAlert("Error", "Error loading pets: " + e.getMessage(), Alert.AlertType.ERROR);
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
            showAlert("Error", "Error loading shelters: " + e.getMessage(), Alert.AlertType.ERROR);
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
        
        // Create table for displaying applications
        TableView<AdoptionApplication> applicationsTable = new TableView<>();
        applicationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Define columns
        TableColumn<AdoptionApplication, String> petNameCol = new TableColumn<>("Pet");
        petNameCol.setCellValueFactory(new PropertyValueFactory<>("petName"));
        
        TableColumn<AdoptionApplication, String> applicantCol = new TableColumn<>("Applicant");
        applicantCol.setCellValueFactory(new PropertyValueFactory<>("applicantName"));
        
        TableColumn<AdoptionApplication, String> shelterCol = new TableColumn<>("Shelter");
        shelterCol.setCellValueFactory(new PropertyValueFactory<>("shelterName"));
        
        TableColumn<AdoptionApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<AdoptionApplication, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));
        
        applicationsTable.getColumns().addAll(petNameCol, applicantCol, shelterCol, statusCol, dateCol);
        
        // Load existing applications
        loadAdoptionApplications(applicationsTable);
        
        submitButton.setOnAction(e -> {
            try {
                if (shelterComboBox.getValue() == null) {
                    showAlert("Error", "Please select a shelter", Alert.AlertType.ERROR);
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
                showAlert("Success", "Adoption application submitted successfully!", Alert.AlertType.INFORMATION);
                
                // Clear form fields
                nameField.clear();
                emailField.clear();
                phoneField.clear();
                addressArea.clear();
                experienceArea.clear();
                reasonArea.clear();
                shelterComboBox.setValue(null);
                
                // Refresh the applications table
                loadAdoptionApplications(applicationsTable);
            } catch (Exception ex) {
                showAlert("Error", "Error submitting adoption application: " + ex.getMessage(), Alert.AlertType.ERROR);
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
            submitButton,
            new Label("Your Applications"),
            applicationsTable
        );
        
        return formBox;
    }

    private void loadAdoptionApplications(TableView<AdoptionApplication> table) {
        try (var conn = DatabaseUtil.getConnection()) {
            String query = "SELECT aa.*, p.name as pet_name, s.name as shelter_name " +
                          "FROM adoption_applications aa " +
                          "JOIN pets p ON aa.pet_id = p.id " +
                          "JOIN shelters s ON aa.shelter_id = s.id " +
                          "ORDER BY aa.application_date DESC";
            
            try (var stmt = conn.prepareStatement(query)) {
                var rs = stmt.executeQuery();
                ObservableList<AdoptionApplication> applications = FXCollections.observableArrayList();
                
                while (rs.next()) {
                    applications.add(new AdoptionApplication(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getString("applicant_name"),
                        rs.getString("shelter_name"),
                        rs.getString("status"),
                        rs.getDate("application_date").toLocalDate()
                    ));
                }
                
                table.setItems(applications);
            }
        } catch (SQLException e) {
            showAlert("Error", "Error loading adoption applications: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
        String query = "SELECT id, name, address, phone, email, rating, capacity FROM shelters";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Shelter shelter = new Shelter(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getDouble("rating"),
                    rs.getInt("capacity")
                );
                shelters.add(shelter);
            }
        }
        return shelters;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void processAdoption(String petName, String adopterName, String shelterName) {
        try {
            if (petName == null || petName.trim().isEmpty()) {
                throw new IllegalArgumentException("Pet name cannot be empty");
            }
            if (adopterName == null || adopterName.trim().isEmpty()) {
                throw new IllegalArgumentException("Adopter name cannot be empty");
            }
            if (shelterName == null || shelterName.trim().isEmpty()) {
                throw new IllegalArgumentException("Shelter name cannot be empty");
            }

            try (Connection conn = DatabaseUtil.getConnection()) {
                // Check if pet exists
                String checkPetQuery = "SELECT id FROM pets WHERE name = ?";
                try (PreparedStatement checkPetStmt = conn.prepareStatement(checkPetQuery)) {
                    checkPetStmt.setString(1, petName);
                    ResultSet rs = checkPetStmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Pet not found: " + petName);
                    }
                }

                // Check if shelter exists
                String checkShelterQuery = "SELECT id FROM shelters WHERE name = ?";
                try (PreparedStatement checkShelterStmt = conn.prepareStatement(checkShelterQuery)) {
                    checkShelterStmt.setString(1, shelterName);
                    ResultSet rs = checkShelterStmt.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Shelter not found: " + shelterName);
                    }
                }

                // Insert adoption record
                String insertQuery = "INSERT INTO adoptions (pet_name, adopter_name, adoption_date, shelter_name) VALUES (?, ?, CURRENT_DATE, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, petName);
                    stmt.setString(2, adopterName);
                    stmt.setString(3, shelterName);
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        showAlert("Success", "Adoption processed successfully!", Alert.AlertType.INFORMATION);
                        loadAdoptions();
                    } else {
                        throw new SQLException("Failed to process adoption");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Error processing adoption: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAdoptions() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM adoptions ORDER BY adoption_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                ObservableList<Adoption> adoptions = FXCollections.observableArrayList();
                while (rs.next()) {
                    adoptions.add(new Adoption(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getString("adopter_name"),
                        rs.getDate("adoption_date").toLocalDate(),
                        rs.getString("shelter_name")
                    ));
                }
                adoptionsTable.setItems(adoptions);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load adoptions: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
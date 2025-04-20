package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private TableView<Pet> petsTable;

    private TextField nameField;
    private ComboBox<String> speciesBox;
    private TextField breedField;
    private TextField ageField;
    private ComboBox<String> genderBox;
    private ComboBox<String> sizeBox;
    private TextArea descriptionArea;
    private TextField imageUrlField;

    public void showPetsTab(Stage stage, double width, double height) {
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

        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        
        // Create search and filter section
        VBox searchBox = createSearchBox();
        topContainer.getChildren().addAll(backButton, searchBox);
        mainLayout.setTop(topContainer);
        
        // Create pets table with modern styling
        petsTable = createPetsTable();
        petsTable.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "rgba(255, 255, 255, 0.9)") + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );
        
        // Wrap table in padding
        VBox tableContainer = new VBox(petsTable);
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        tableContainer.setStyle("-fx-background-color: transparent;");
        mainLayout.setCenter(tableContainer);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - View Pets");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSearchBox() {
        VBox searchBox = new VBox(15);
        searchBox.setPadding(new Insets(20));
        searchBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );
        
        // Title with modern styling
        Label titleLabel = new Label("Manage Pets");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.rgb(44, 62, 80));
        
        // Search field with modern styling
        TextField searchField = new TextField();
        searchField.setPromptText("Search pets...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20px;" +
            "-fx-border-radius: 20px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // Filter options with modern styling
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> speciesFilter = createStyledComboBox("Species", "All", "Dog", "Cat", "Bird", "Other");
        ComboBox<String> sizeFilter = createStyledComboBox("Size", "All", "Small", "Medium", "Large");
        ComboBox<String> statusFilter = createStyledComboBox("Status", "All", "Available", "Adopted", "Pending");
        
        filterBox.getChildren().addAll(speciesFilter, sizeFilter, statusFilter);
        
        // Search button with modern styling
        Button searchButton = new Button("Search");
        searchButton.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 8 20;" +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect
        searchButton.setOnMouseEntered(e -> 
            searchButton.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 8 20;" +
                "-fx-cursor: hand;"
            )
        );
        searchButton.setOnMouseExited(e -> 
            searchButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 8 20;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Add search button action
        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().toLowerCase();
            String species = speciesFilter.getValue();
            String size = sizeFilter.getValue();
            String status = statusFilter.getValue();
            
            try {
                ObservableList<Pet> filteredPets = searchPets(searchText, species, size, status);
                petsTable.setItems(filteredPets);
            } catch (SQLException ex) {
                showAlert("Error searching pets: " + ex.getMessage());
            }
        });
        
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.getChildren().addAll(searchField, searchButton);
        
        searchBox.getChildren().addAll(titleLabel, searchRow, filterBox);
        
        return searchBox;
    }

    private ComboBox<String> createStyledComboBox(String prompt, String... items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        comboBox.getItems().addAll(items);
        comboBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20px;" +
            "-fx-border-radius: 20px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 5 10;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        return comboBox;
    }

    private TableView<Pet> createPetsTable() {
        TableView<Pet> table = new TableView<>();
        table.setStyle("-fx-background-radius: 10px;");
        
        // Create columns with modern styling
        TableColumn<Pet, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Pet, String> speciesCol = new TableColumn<>("Species");
        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));
        
        TableColumn<Pet, String> breedCol = new TableColumn<>("Breed");
        breedCol.setCellValueFactory(new PropertyValueFactory<>("breed"));
        
        TableColumn<Pet, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        TableColumn<Pet, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        
        TableColumn<Pet, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        
        TableColumn<Pet, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Style the columns
        String columnStyle = "-fx-alignment: CENTER;";
        nameCol.setStyle(columnStyle);
        speciesCol.setStyle(columnStyle);
        breedCol.setStyle(columnStyle);
        ageCol.setStyle(columnStyle);
        genderCol.setStyle(columnStyle);
        sizeCol.setStyle(columnStyle);
        statusCol.setStyle(columnStyle);
        
        // Add columns to table
        table.getColumns().addAll(nameCol, speciesCol, breedCol, ageCol, genderCol, sizeCol, statusCol);
        
        // Add double-click handler
        table.setRowFactory(tv -> {
            TableRow<Pet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Pet pet = row.getItem();
                    showPetDetails(pet);
                }
            });
            return row;
        });
        
        // Load data from database
        try {
            table.setItems(loadPetsFromDatabase());
        } catch (SQLException e) {
            showAlert("Error loading pets: " + e.getMessage());
        }
        
        return table;
    }

    private ObservableList<Pet> loadPetsFromDatabase() throws SQLException {
        ObservableList<Pet> pets = FXCollections.observableArrayList();
        String query = "SELECT * FROM pets";
        
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

    public void showAddPetForm(Stage stage, double width, double height) {
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

        // Create title section
        Label titleLabel = new Label("Add a Pet for Adoption");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));

        // Create top container with back button and title
        HBox topContainer = new HBox(20);
        topContainer.setAlignment(Pos.CENTER_LEFT);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, titleLabel);

        // Create form content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox formContainer = createAddPetForm();
        scrollPane.setContent(formContainer);

        // Add components to layout
        mainLayout.setTop(topContainer);
        mainLayout.setCenter(scrollPane);

        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Add Pet");
        stage.setScene(scene);
    }

    private String getDefaultImagePath() {
        return "/images/default-pet.jpg";
    }

    private ImageView createPetImageView(String imagePath) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        try {
            // First try to load from resources
            InputStream resourceStream = getClass().getResourceAsStream("/images/" + imagePath);
            if (resourceStream != null) {
                imageView.setImage(new Image(resourceStream));
                resourceStream.close();
                return imageView;
            }
            
            // If not in resources, try absolute path
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
                return imageView;
            }
            
            // If both fail, use default image
            System.err.println("Image not found: " + imagePath);
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-pet.jpg")));
        }
        
        return imageView;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 5;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;"
        );
        field.setPrefHeight(35);
        return field;
    }

    private String getImageUrl(String input) {
        if (input == null || input.trim().isEmpty()) {
            return getDefaultImagePath();
        }
        
        // If it's a resource path (starts with /)
        if (input.startsWith("/")) {
            try {
                String resourceUrl = getClass().getResource(input).toExternalForm();
                return resourceUrl;
            } catch (Exception e) {
                System.err.println("Could not load resource image: " + input);
                return getDefaultImagePath();
            }
        }
        
        // If it's a full URL or file path, return as is
        return input;
    }

    private VBox createAddPetForm() {
        VBox form = new VBox(20);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.TOP_LEFT);
        form.setMaxWidth(600);

        // Initialize form fields
        nameField = createStyledTextField("Enter pet name");
        speciesBox = createStyledComboBox("Select species", "Dog", "Cat", "Bird", "Other");
        breedField = createStyledTextField("Enter breed");
        ageField = createStyledTextField("Enter age");
        genderBox = createStyledComboBox("Select gender", "Male", "Female", "Other");
        sizeBox = createStyledComboBox("Select size", "Small", "Medium", "Large");
        descriptionArea = createStyledTextArea("Enter description");
        imageUrlField = createStyledTextField("Enter image URL or resource path (e.g., /images/pet.jpg)");

        // Create form groups
        VBox nameGroup = createFormGroup("Pet Name *", nameField);
        VBox speciesGroup = createFormGroup("Species *", speciesBox);
        VBox breedGroup = createFormGroup("Breed", breedField);
        VBox ageGroup = createFormGroup("Age *", ageField);
        VBox genderGroup = createFormGroup("Gender *", genderBox);
        VBox sizeGroup = createFormGroup("Size *", sizeBox);
        VBox descriptionGroup = createFormGroup("Description", descriptionArea);
        VBox imageUrlGroup = createFormGroup("Image URL", imageUrlField);

        // Create submit button
        Button submitButton = new Button("Add Pet");
        styleButton(submitButton);
        submitButton.setOnAction(e -> {
            try {
                // Validate required fields
                if (nameField.getText().trim().isEmpty() ||
                    speciesBox.getValue() == null ||
                    ageField.getText().trim().isEmpty() ||
                    genderBox.getValue() == null ||
                    sizeBox.getValue() == null) {
                    showAlert("Please fill in all required fields (marked with *)");
                    return;
                }

                // Validate age is a positive number
                int age;
                try {
                    age = Integer.parseInt(ageField.getText().trim());
                    if (age < 0) {
                        showAlert("Age must be a positive number");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Age must be a valid number");
                    return;
                }

                // Process the image URL
                String processedImageUrl = getImageUrl(imageUrlField.getText().trim());

                // Submit pet data
                submitPet(
                    nameField.getText().trim(),
                    speciesBox.getValue(),
                    breedField.getText().trim(),
                    age,
                    genderBox.getValue(),
                    sizeBox.getValue(),
                    descriptionArea.getText().trim(),
                    processedImageUrl
                );
            } catch (Exception ex) {
                showAlert("Error adding pet: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
            nameGroup,
            speciesGroup,
            breedGroup,
            ageGroup,
            genderGroup,
            sizeGroup,
            descriptionGroup,
            imageUrlGroup,
            submitButton
        );

        return form;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "black") + ";" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;"
        );
        area.setPrefRowCount(4);
        return area;
    }

    private VBox createFormGroup(String label, Control field) {
        VBox group = new VBox(5);
        Label labelNode = new Label(label);
        labelNode.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        labelNode.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
        group.getChildren().addAll(labelNode, field);
        return group;
    }

    private void styleButton(Button button) {
        button.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        button.setPrefWidth(200);
    }

    private void submitPet(String name, String species, String breed, int age,
                          String gender, String size, String description, String imageUrl) {
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            showAlert("Pet name is required");
            return;
        }
        if (species == null || species.trim().isEmpty()) {
            showAlert("Species is required");
            return;
        }
        if (age < 0) {
            showAlert("Age must be a positive number");
            return;
        }
        if (gender == null || gender.trim().isEmpty()) {
            showAlert("Gender is required");
            return;
        }
        if (size == null || size.trim().isEmpty()) {
            showAlert("Size is required");
            return;
        }

        try {
            // Add pet to database
            Connection conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO pets (name, species, breed, age, gender, size, description, image_url, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'available')";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, species);
                pstmt.setString(3, breed);
                pstmt.setInt(4, age);
                pstmt.setString(5, gender);
                pstmt.setString(6, size);
                pstmt.setString(7, description);
                pstmt.setString(8, imageUrl);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Pet added successfully!");
                    // Get the current stage from any control in the scene
                    Stage stage = (Stage) nameField.getScene().getWindow();
                    new DashboardController().showDashboard(stage, DEFAULT_WIDTH, DEFAULT_HEIGHT, DatabaseUtil.getCurrentUserId());
                } else {
                    showAlert("Failed to add pet");
                }
            }
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String[] getSamplePetImageUrls() {
        return new String[] {
            "golden-retriever.jpg",
            "husky.jpg",
            "german-shepherd.jpg",
            "white-dog.jpg",
            "orange-cat.jpg",
            "white-kitten.jpg",
            "gray-cat.jpg",
            "black-cat.jpg",
            "parrot.jpg",
            "budgie.jpg",
            "cockatiel.jpg"
        };
    }

    private String getRandomPetImageUrl() {
        String[] urls = getSamplePetImageUrls();
        int index = (int) (Math.random() * urls.length);
        return urls[index];
    }

    public void showPetDetails(Pet pet) {
        Stage detailsStage = new Stage();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Create back button
        Button backButton = new Button("← Back");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> detailsStage.close());

        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().add(backButton);
        mainLayout.setTop(topContainer);
        
        // Create pet details section
        VBox detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Pet image
        ImageView petImage = createPetImageView(pet.getImageUrl());
        petImage.setFitWidth(300);
        petImage.setFitHeight(300);
        
        // Pet information
        Label nameLabel = new Label(pet.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.rgb(44, 62, 80));
        
        Label detailsLabel = new Label(
            "Species: " + pet.getSpecies() + "\n" +
            "Breed: " + pet.getBreed() + "\n" +
            "Age: " + pet.getAge() + " years\n" +
            "Size: " + pet.getSize() + "\n" +
            "Status: " + pet.getStatus()
        );
        detailsLabel.setFont(Font.font("Arial", 16));
        detailsLabel.setTextFill(Color.rgb(52, 73, 94));
        
        // Description
        TextArea descriptionArea = new TextArea(pet.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );
        
        // Adopt button
        Button adoptButton = new Button("Adopt This Pet");
        adoptButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 10 20;"
        );
        adoptButton.setOnAction(e -> {
            try {
                new AdoptionController().showAdoptionForm(pet);
            } catch (Exception ex) {
                showAlert("Error starting adoption process: " + ex.getMessage());
            }
        });
        
        detailsBox.getChildren().addAll(petImage, nameLabel, detailsLabel, descriptionArea, adoptButton);
        mainLayout.setCenter(detailsBox);
        
        Scene scene = new Scene(mainLayout, 600, 800);
        detailsStage.setTitle("Pet Details - " + pet.getName());
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private ObservableList<Pet> searchPets(String searchText, String species, String size, String status) throws SQLException {
        ObservableList<Pet> filteredPets = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder("SELECT * FROM pets WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Add search text condition
        if (searchText != null && !searchText.isEmpty()) {
            query.append(" AND (LOWER(name) LIKE ? OR LOWER(breed) LIKE ? OR LOWER(description) LIKE ?)");
            String searchPattern = "%" + searchText + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Add species filter
        if (species != null && !species.equals("All")) {
            query.append(" AND species = ?");
            params.add(species);
        }

        // Add size filter
        if (size != null && !size.equals("All")) {
            query.append(" AND size = ?");
            params.add(size.toLowerCase());
        }

        // Add status filter
        if (status != null && !status.equals("All")) {
            query.append(" AND status = ?");
            params.add(status.toLowerCase());
        }

        try (var conn = DatabaseUtil.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            // Execute query
            try (var rs = stmt.executeQuery()) {
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
                    filteredPets.add(pet);
                }
            }
        }
        
        return filteredPets;
    }

    public void removeSheru() {
        if (DatabaseUtil.removePetByName("Sheru")) {
            showAlert("Sheru has been successfully removed from the pets list.");
        } else {
            showAlert("Could not find Sheru in the pets list.");
        }
    }
} 
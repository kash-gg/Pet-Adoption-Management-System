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

public class PetTableController {
    private TableView<Pet> table;

    public void showPetTable(Stage stage, double width, double height) {
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
        Label titleLabel = new Label("Available Pets");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));

        // Create top container
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, titleLabel);

        // Create table
        table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        // Define columns
        TableColumn<Pet, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Pet, String> speciesCol = new TableColumn<>("Species");
        speciesCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSpecies()));

        TableColumn<Pet, String> breedCol = new TableColumn<>("Breed");
        breedCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBreed()));

        TableColumn<Pet, Number> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAge()));

        TableColumn<Pet, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSize()));

        TableColumn<Pet, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Add columns to table
        table.getColumns().addAll(nameCol, speciesCol, breedCol, ageCol, sizeCol, statusCol);

        // Style the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(height - 200);

        // Add action buttons
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button addButton = new Button("Add New Pet");
        addButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;"
        );
        addButton.setOnAction(e -> showAddPetForm());

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;"
        );
        refreshButton.setOnAction(e -> loadPets());

        actionBox.getChildren().addAll(refreshButton, addButton);

        // Add components to layout
        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(0, 20, 20, 20));
        centerBox.getChildren().addAll(table, actionBox);

        mainLayout.setTop(topContainer);
        mainLayout.setCenter(centerBox);

        // Load pets
        loadPets();

        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Pet Table");
        stage.setScene(scene);
        stage.show();
    }

    private void loadPets() {
        try {
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
            
            table.setItems(pets);
        } catch (Exception e) {
            showAlert("Error loading pets: " + e.getMessage());
        }
    }

    private void showAddPetForm() {
        // This will be implemented to show a form for adding new pets
        showAlert("Add Pet form will be implemented here");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
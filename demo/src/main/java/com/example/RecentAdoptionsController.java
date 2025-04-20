package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class RecentAdoptionsController {
    @FXML
    private Button backButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox adoptionsContainer;

    @FXML
    public void initialize() {
        setupUI();
        loadRecentAdoptions();
    }

    private void setupUI() {
        // Style the back button
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> goBack());

        // Style the scroll pane
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-border-color: #e0e0e0;");

        // Style the container
        adoptionsContainer.setSpacing(10);
        adoptionsContainer.setPadding(new Insets(20));
    }

    private void loadRecentAdoptions() {
        List<Adoption> adoptions = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/pet_adoption";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT a.*, p.name as pet_name, s.name as shelter_name " +
                          "FROM adoption_applications a " +
                          "JOIN pets p ON a.pet_id = p.id " +
                          "JOIN shelters s ON a.shelter_id = s.id " +
                          "WHERE a.status = 'Approved' " +
                          "ORDER BY a.application_date DESC " +
                          "LIMIT 10";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    adoptions.add(new Adoption(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getString("adopter_name"),
                        LocalDate.parse(rs.getString("application_date")),
                        rs.getString("shelter_name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        displayAdoptions(adoptions);
    }

    private void displayAdoptions(List<Adoption> adoptions) {
        adoptionsContainer.getChildren().clear();

        // Add title
        Text title = new Text("Recent Adoptions");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setFill(Color.web("#333333"));
        adoptionsContainer.getChildren().add(title);

        if (adoptions.isEmpty()) {
            Text noAdoptions = new Text("No recent adoptions found");
            noAdoptions.setFont(Font.font("System", 16));
            noAdoptions.setFill(Color.web("#666666"));
            adoptionsContainer.getChildren().add(noAdoptions);
            return;
        }

        for (Adoption adoption : adoptions) {
            VBox adoptionCard = createAdoptionCard(adoption);
            adoptionsContainer.getChildren().add(adoptionCard);
        }
    }

    private VBox createAdoptionCard(Adoption adoption) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-background-radius: 5px;");
        card.setMaxWidth(Double.MAX_VALUE);

        // Pet name
        Text petName = new Text(adoption.getPetName());
        petName.setFont(Font.font("System", FontWeight.BOLD, 18));
        petName.setFill(Color.web("#333333"));

        // Adopter info
        Text adopterInfo = new Text("Adopted by: " + adoption.getAdopterName());
        adopterInfo.setFont(Font.font("System", 14));
        adopterInfo.setFill(Color.web("#666666"));

        // Shelter info
        Text shelterInfo = new Text("Shelter: " + adoption.getShelterName());
        shelterInfo.setFont(Font.font("System", 14));
        shelterInfo.setFill(Color.web("#666666"));

        // Date
        Text date = new Text("Date: " + adoption.getAdoptionDate());
        date.setFont(Font.font("System", 14));
        date.setFill(Color.web("#666666"));

        card.getChildren().addAll(petName, adopterInfo, shelterInfo, date);
        return card;
    }

    private void goBack() {
        // Implement navigation back to the previous screen
        // This will depend on your application's navigation structure
    }
} 
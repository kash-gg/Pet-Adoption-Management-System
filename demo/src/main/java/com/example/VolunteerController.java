package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class VolunteerController {
    private TableView<Volunteer> volunteerTable;
    private final DatabaseUtil dbUtil;

    public VolunteerController() {
        this.dbUtil = new DatabaseUtil();
        this.volunteerTable = new TableView<>();
    }

    public void showVolunteerTab(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Title section
        Label titleLabel = new Label("Volunteer Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Back button
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            try {
                new DashboardController().showDashboard(stage, width, height, DatabaseUtil.getCurrentUserId());
            } catch (Exception ex) {
                showAlert("Error returning to dashboard: " + ex.getMessage());
            }
        });
        
        // Top section with title and back button
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.getChildren().addAll(backButton, titleLabel);
        root.setTop(topSection);

        // Setup volunteer table
        setupVolunteerTable();
        loadVolunteers();

        // Center the table
        VBox centerContent = new VBox(20);
        centerContent.getChildren().add(volunteerTable);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
    }

    private void setupVolunteerTable() {
        // Define table columns
        TableColumn<Volunteer, String> volunteerIdCol = new TableColumn<>("Volunteer ID");
        volunteerIdCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVolunteerId()));

        TableColumn<Volunteer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Volunteer, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

        TableColumn<Volunteer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        TableColumn<Volunteer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Volunteer, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEventName()));

        volunteerTable.getColumns().addAll(volunteerIdCol, nameCol, roleCol, phoneCol, emailCol, eventCol);
        volunteerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadVolunteers() {
        try {
            List<Volunteer> volunteers = new ArrayList<>();
            String query = "SELECT v.*, e.title as event_name FROM volunteers v " +
                         "JOIN events e ON v.event_id = e.id";
            
            Connection conn = dbUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Volunteer volunteer = new Volunteer(
                    rs.getInt("id"),
                    rs.getString("volunteer_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("phone_number"),
                    rs.getString("email"),
                    rs.getInt("event_id"),
                    rs.getString("event_name")
                );
                volunteers.add(volunteer);
            }

            volunteerTable.setItems(FXCollections.observableArrayList(volunteers));
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading volunteers: " + e.getMessage());
        }
    }

    public void showVolunteerForm(Event event, Stage parentStage) {
        Stage formStage = new Stage();
        formStage.setTitle("Volunteer Registration");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField volunteerIdField = new TextField();
        volunteerIdField.setPromptText("Volunteer ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField roleField = new TextField();
        roleField.setPromptText("Role");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (validateForm(volunteerIdField, nameField, roleField, phoneField, emailField)) {
                saveVolunteer(
                    volunteerIdField.getText(),
                    nameField.getText(),
                    roleField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    event.getId(),
                    formStage
                );
            }
        });

        form.getChildren().addAll(
            new Label("Event: " + event.getTitle()),
            volunteerIdField,
            nameField,
            roleField,
            phoneField,
            emailField,
            submitButton
        );

        Scene scene = new Scene(form, 400, 500);
        formStage.setScene(scene);
        formStage.show();
    }

    private boolean validateForm(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                showError("All fields are required");
                return false;
            }
        }
        return true;
    }

    private void saveVolunteer(String volunteerId, String name, String role, 
                             String phone, String email, int eventId, Stage formStage) {
        try {
            String query = "INSERT INTO volunteers (volunteer_id, name, role, phone_number, email, event_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            Connection conn = dbUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, volunteerId);
            stmt.setString(2, name);
            stmt.setString(3, role);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setInt(6, eventId);

            stmt.executeUpdate();
            formStage.close();
            loadVolunteers(); // Refresh the table
            showSuccess("Volunteer registration successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving volunteer: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
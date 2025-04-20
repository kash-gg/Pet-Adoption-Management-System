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
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    private TableView<Event> eventsTable;
    private final DatabaseUtil dbUtil;

    public EventController() {
        this.dbUtil = new DatabaseUtil();
    }

    public void showEventsTab(Stage stage, double width, double height) {
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
        Label titleLabel = new Label("Events");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));

        // Create top container with back button and title
        HBox topContainer = new HBox(20);
        topContainer.setAlignment(Pos.CENTER_LEFT);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, titleLabel);

        // Create events table
        eventsTable = new TableView<>();
        eventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define table columns
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Event, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Event, LocalDateTime> dateCol = new TableColumn<>("Event Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        dateCol.setCellFactory(column -> new TableCell<Event, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Event, Integer> participantsCol = new TableColumn<>("Max Participants");
        participantsCol.setCellValueFactory(new PropertyValueFactory<>("maxParticipants"));

        // Add columns to table
        eventsTable.getColumns().addAll(titleCol, descriptionCol, dateCol, locationCol, participantsCol);

        // Load events data
        loadEventsFromDatabase();

        // Create table container with padding
        VBox tableContainer = new VBox(eventsTable);
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        VBox.setVgrow(eventsTable, Priority.ALWAYS);

        // Add components to layout
        mainLayout.setTop(topContainer);
        mainLayout.setCenter(tableContainer);

        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Events");
        stage.setScene(scene);
        stage.show();
    }

    private void loadEventsFromDatabase() {
        List<Event> events = DatabaseUtil.getAllEvents();
        ObservableList<Event> eventsList = FXCollections.observableArrayList(events);
        eventsTable.setItems(eventsList);
    }

    private void showCreateEventForm(Stage parentStage) {
        // This method will be implemented later to handle event creation
        showAlert("Info", "Event creation feature coming soon!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
} 
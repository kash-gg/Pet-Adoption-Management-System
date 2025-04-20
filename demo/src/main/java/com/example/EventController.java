package com.example;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.image.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventController {
    private List<Event> events = new ArrayList<>();
    private GridPane eventsGrid;
    private Stage stage;

    public void showEventTab(Stage stage, double width, double height) {
        this.stage = stage;
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f0f2f5;");

        // Create top section with title and add event button
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.CENTER_LEFT);
        topSection.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("Events");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2b3467;");

        Button addEventButton = new Button("Add Event");
        addEventButton.setStyle("-fx-background-color: #2b3467; -fx-text-fill: white; -fx-font-weight: bold;");
        addEventButton.setOnAction(e -> showAddEventDialog());

        topSection.getChildren().addAll(titleLabel, addEventButton);
        mainLayout.setTop(topSection);

        // Create search box
        mainLayout.setCenter(createSearchBox());

        // Create events grid
        eventsGrid = createEventsGrid();
        ScrollPane scrollPane = new ScrollPane(eventsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f2f5; -fx-border-color: #f0f2f5;");
        mainLayout.setCenter(scrollPane);

        // Load events from database
        loadEventsFromDatabase();

        Scene scene = new Scene(mainLayout, width, height);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSearchBox() {
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(20));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search events...");
        searchField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
        searchField.setPrefWidth(300);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All", "Adoption", "Fundraising", "Training", "Community");
        typeFilter.setValue("All");
        typeFilter.setStyle("-fx-background-radius: 5;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Upcoming", "Ongoing", "Completed");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-radius: 5;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2b3467; -fx-text-fill: white; -fx-font-weight: bold;");

        searchBar.getChildren().addAll(searchField, typeFilter, statusFilter, searchButton);
        searchBox.getChildren().add(searchBar);

        return searchBox;
    }

    private GridPane createEventsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);

        // Event image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/" + event.getImageUrl()));
            imageView.setImage(image);
        } catch (Exception e) {
            // Use default image if event image not found
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-event.jpg"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        // Event title
        Label titleLabel = new Label(event.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2b3467;");

        // Event details
        Label dateLabel = new Label("Date: " + event.getDate().toString());
        Label timeLabel = new Label("Time: " + event.getTime().toString());
        Label locationLabel = new Label("Location: " + event.getLocation());
        Label typeLabel = new Label("Type: " + event.getType());

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2b3467; -fx-text-fill: white; -fx-font-weight: bold;");
        registerButton.setOnAction(e -> showEventDetails(event));

        card.getChildren().addAll(imageView, titleLabel, dateLabel, timeLabel, locationLabel, typeLabel, registerButton);
        return card;
    }

    private void loadEventsFromDatabase() {
        // TODO: Replace with actual database query
        events.add(new Event(1, "Pet Adoption Day", "Community Center", "Adoption",
                LocalDate.now().plusDays(7), LocalTime.of(10, 0), "Join us for our monthly pet adoption event",
                "adoption-day.jpg"));
        events.add(new Event(2, "Fundraising Gala", "Grand Hotel", "Fundraising",
                LocalDate.now().plusDays(14), LocalTime.of(18, 0), "Annual fundraising gala for animal welfare",
                "gala.jpg"));
        events.add(new Event(3, "Pet Training Workshop", "Training Center", "Training",
                LocalDate.now().plusDays(21), LocalTime.of(14, 0), "Learn basic pet training techniques",
                "training.jpg"));

        updateEventsGrid();
    }

    private void updateEventsGrid() {
        eventsGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        for (Event event : events) {
            eventsGrid.add(createEventCard(event), column, row);
            column++;
            if (column > 2) {
                column = 0;
                row++;
            }
        }
    }

    private void showEventDetails(Event event) {
        Stage detailsStage = new Stage();
        VBox detailsLayout = new VBox(20);
        detailsLayout.setPadding(new Insets(20));
        detailsLayout.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label(event.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2b3467;");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/" + event.getImageUrl()));
            imageView.setImage(image);
        } catch (Exception e) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-event.jpg"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitWidth(400);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);

        Label descriptionLabel = new Label(event.getDescription());
        descriptionLabel.setWrapText(true);

        Label detailsLabel = new Label("Event Details:");
        detailsLabel.setStyle("-fx-font-weight: bold;");

        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(
            new Label("Date: " + event.getDate().toString()),
            new Label("Time: " + event.getTime().toString()),
            new Label("Location: " + event.getLocation()),
            new Label("Type: " + event.getType())
        );

        Button registerButton = new Button("Register for Event");
        registerButton.setStyle("-fx-background-color: #2b3467; -fx-text-fill: white; -fx-font-weight: bold;");
        registerButton.setOnAction(e -> {
            // TODO: Implement event registration
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("You have successfully registered for " + event.getTitle());
            alert.showAndWait();
        });

        detailsLayout.getChildren().addAll(titleLabel, imageView, descriptionLabel, detailsLabel, detailsBox, registerButton);
        Scene scene = new Scene(detailsLayout, 500, 700);
        detailsStage.setScene(scene);
        detailsStage.setTitle("Event Details");
        detailsStage.show();
    }

    private void showAddEventDialog() {
        Stage dialogStage = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Event Title");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Adoption", "Fundraising", "Training", "Community");
        typeComboBox.setPromptText("Event Type");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Event Date");

        TextField timeField = new TextField();
        timeField.setPromptText("Event Time (HH:MM)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Event Description");
        descriptionArea.setPrefRowCount(3);

        Button saveButton = new Button("Save Event");
        saveButton.setStyle("-fx-background-color: #2b3467; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            // TODO: Implement event saving to database
            dialogStage.close();
            loadEventsFromDatabase(); // Refresh the events list
        });

        dialogLayout.getChildren().addAll(
            new Label("Add New Event"),
            titleField,
            locationField,
            typeComboBox,
            datePicker,
            timeField,
            descriptionArea,
            saveButton
        );

        Scene dialogScene = new Scene(dialogLayout, 300, 400);
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle("Add Event");
        dialogStage.show();
    }
} 
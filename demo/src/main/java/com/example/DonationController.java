package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import java.sql.Timestamp;

public class DonationController {
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;

    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private ComboBox<String> donationTypeComboBox;
    @FXML
    private ComboBox<String> purposeComboBox;
    @FXML
    private TextArea messageArea;
    @FXML
    private TableView<Donation> donationsTable;
    @FXML
    private TextField nameField;

    public void showDonationsTab(Stage stage, double width, double height, int userId) {
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
        backButton.setOnAction(e -> {
            try {
                new DashboardController().showDashboard(stage, width, height, userId);
            } catch (Exception ex) {
                showAlert("Error returning to dashboard: " + ex.getMessage());
            }
        });

        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        
        // Create title
        Label titleLabel = new Label("Donations");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.rgb(44, 62, 80));
        
        // Create make donation button
        Button makeDonationButton = new Button("Make a Donation");
        makeDonationButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 10 20;"
        );
        makeDonationButton.setOnAction(e -> showMakeDonationForm(stage, width, height, userId));
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(backButton, makeDonationButton);
        
        topContainer.getChildren().addAll(buttonBox, titleLabel);
        mainLayout.setTop(topContainer);
        
        // Create donations table
        TableView<Donation> donationsTable = createDonationsTable(userId);
        VBox tableContainer = new VBox(donationsTable);
        tableContainer.setPadding(new Insets(20));
        mainLayout.setCenter(tableContainer);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Donations");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<Donation> createDonationsTable(int userId) {
        TableView<Donation> table = new TableView<>();
        
        // Create columns
        TableColumn<Donation, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Donation, String> paymentMethodCol = new TableColumn<>("Payment Method");
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentMethodCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Donation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Donation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER;");
        
        // Add columns to table
        table.getColumns().addAll(amountCol, paymentMethodCol, dateCol, statusCol);
        
        // Load data
        table.setItems(DatabaseUtil.getUserDonations(userId));
        
        // Style the table
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        return table;
    }

    private void showMakeDonationForm(Stage stage, double width, double height, int userId) {
        // Debug log for user ID
        System.out.println("Opening donation form for user ID: " + userId);
        System.out.println("Current user ID from DatabaseUtil: " + DatabaseUtil.getCurrentUserId());
        
        try {
            // Test database connection
            var conn = DatabaseUtil.getConnection();
            System.out.println("Database connection successful");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error connecting to database. Please try again.");
            return;
        }
        
        Stage donationStage = new Stage();
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(30));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );
        
        // Title
        Label titleLabel = new Label("Make a Donation");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.rgb(44, 62, 80));
        
        // Donation Type
        ComboBox<String> donationTypeCombo = new ComboBox<>();
        donationTypeCombo.getItems().addAll("One-time", "Monthly", "Quarterly", "Annual");
        donationTypeCombo.setPromptText("Select donation type");
        donationTypeCombo.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // Amount field
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // Payment method selection
        ComboBox<String> paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Credit Card", "Debit Card", "PayPal", "Bank Transfer");
        paymentMethodCombo.setPromptText("Select payment method");
        paymentMethodCombo.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // PayPal Details
        VBox paypalDetailsBox = new VBox(10);
        paypalDetailsBox.setVisible(false);
        
        TextField paypalEmailField = new TextField();
        paypalEmailField.setPromptText("PayPal Email");
        paypalEmailField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        paypalDetailsBox.getChildren().add(paypalEmailField);
        
        // Show/hide payment details based on payment method
        paymentMethodCombo.setOnAction(e -> {
            String method = paymentMethodCombo.getValue();
            paypalDetailsBox.setVisible(method != null && method.equals("PayPal"));
        });
        
        // Purpose selection
        ComboBox<String> purposeCombo = new ComboBox<>();
        purposeCombo.getItems().addAll("General Fund", "Medical Care", "Food & Supplies", "Facility Maintenance", "Education Programs");
        purposeCombo.setPromptText("Select donation purpose");
        purposeCombo.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // Message
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Optional message (e.g., dedication, special instructions)");
        messageArea.setPrefRowCount(3);
        messageArea.setWrapText(true);
        messageArea.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 8 15;" +
            "-fx-font-size: 14px;"
        );
        
        // Submit button with modern styling
        Button submitButton = new Button("Make Donation");
        submitButton.setPrefWidth(200);
        submitButton.setPrefHeight(45);
        submitButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 25px;" +
            "-fx-padding: 10 20;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"
        );
        
        // Add hover effects
        submitButton.setOnMouseEntered(e -> 
            submitButton.setStyle(
                "-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25px;" +
                "-fx-padding: 10 20;" +
                "-fx-font-size: 16px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);"
            )
        );
        submitButton.setOnMouseExited(e -> 
            submitButton.setStyle(
                "-fx-background-color: #2ecc71;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25px;" +
                "-fx-padding: 10 20;" +
                "-fx-font-size: 16px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"
            )
        );
        
        // Add click handler for the submit button
        submitButton.setOnAction(e -> {
            try {
                // Debug log
                System.out.println("Processing donation with values:");
                System.out.println("User ID: " + userId);
                System.out.println("Amount: " + amountField.getText());
                System.out.println("Payment Method: " + paymentMethodCombo.getValue());
                System.out.println("Donation Type: " + donationTypeCombo.getValue());
                System.out.println("Purpose: " + purposeCombo.getValue());
                
                // Validate inputs
                if (donationTypeCombo.getValue() == null) {
                    showAlert("Please select a donation type");
                    return;
                }
                
                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        showAlert("Please enter a valid amount");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Please enter a valid amount");
                    return;
                }
                
                String paymentMethod = paymentMethodCombo.getValue();
                if (paymentMethod == null) {
                    showAlert("Please select a payment method");
                    return;
                }
                
                if (purposeCombo.getValue() == null) {
                    showAlert("Please select a donation purpose");
                    return;
                }

                // Get the current user's name from the database
                String donorName;
                try (var conn = DatabaseUtil.getConnection();
                     var stmt = conn.prepareStatement("SELECT name FROM users WHERE id = ?")) {
                    stmt.setInt(1, userId);
                    var rs = stmt.executeQuery();
                    if (rs.next()) {
                        donorName = rs.getString("name");
                    } else {
                        showAlert("Error: Could not retrieve user information");
                        return;
                    }
                }
                
                // Process donation
                System.out.println("Adding donation with donor name: " + donorName);
                boolean success = DatabaseUtil.addDonation(
                    userId,
                    amount,
                    paymentMethod,
                    donationTypeCombo.getValue(),
                    purposeCombo.getValue(),
                    messageArea.getText(),
                    donorName
                );
                
                if (success) {
                    showAlert("Donation successful! Thank you for your generosity.");
                    donationStage.close();
                    showDonationsTab(stage, width, height, userId);
                } else {
                    showAlert("Error processing donation. Please try again.");
                }
            } catch (Exception ex) {
                System.err.println("Error processing donation: " + ex.getMessage());
                ex.printStackTrace();
                showAlert("Error: " + ex.getMessage());
            }
        });
        
        // Center the submit button
        HBox submitButtonBox = new HBox(submitButton);
        submitButtonBox.setAlignment(Pos.CENTER);
        submitButtonBox.setPadding(new Insets(20, 0, 0, 0));
        
        // Add all components to the form
        formBox.getChildren().addAll(
            titleLabel,
            donationTypeCombo,
            amountField,
            paymentMethodCombo,
            paypalDetailsBox,
            purposeCombo,
            messageArea,
            submitButtonBox
        );
        
        Scene scene = new Scene(formBox, 500, 600);
        donationStage.setTitle("Make a Donation");
        donationStage.setScene(scene);
        donationStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void processDonation() {
        try {
            int userId = DatabaseUtil.getCurrentUserId();
            System.out.println("Processing donation for user ID: " + userId);
            
            if (userId <= 0) {
                showAlert("Please log in to make a donation.");
                return;
            }

            String donorName = nameField.getText().trim();
            if (donorName.isEmpty()) {
                showAlert("Please enter your name.");
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            String paymentMethod = paymentMethodComboBox.getValue();
            String donationType = donationTypeComboBox.getValue();
            String purpose = purposeComboBox.getValue();
            String message = messageArea.getText();

            System.out.println("Donation details - Donor: " + donorName + 
                             ", Amount: " + amount + 
                             ", Payment Method: " + paymentMethod + 
                             ", Type: " + donationType + 
                             ", Purpose: " + purpose);

            boolean success = DatabaseUtil.addDonation(userId, amount, paymentMethod, 
                                                     donationType, purpose, message,
                                                     donorName);

            if (success) {
                showAlert("Donation processed successfully!");
                loadDonationsFromDatabase(); // Refresh the donations table
                clearForm();
            } else {
                showAlert("Error processing the donation. Please try again.");
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid amount.");
            System.out.println("Error parsing donation amount: " + e.getMessage());
        } catch (Exception e) {
            showAlert("An unexpected error occurred: " + e.getMessage());
            System.out.println("Unexpected error in processDonation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        amountField.clear();
        paymentMethodComboBox.setValue(null);
        donationTypeComboBox.setValue(null);
        purposeComboBox.setValue(null);
        messageArea.clear();
    }

    private void loadDonationsFromDatabase() {
        try {
            String query = "SELECT * FROM donations WHERE user_id = ? ORDER BY date DESC";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, DatabaseUtil.getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();

            ObservableList<Donation> donations = FXCollections.observableArrayList();
            while (rs.next()) {
                donations.add(new Donation(
                    rs.getInt("id"),
                    rs.getString("donor_name"),
                    rs.getDouble("amount"),
                    rs.getString("payment_method"),
                    rs.getString("donation_type"),
                    rs.getString("purpose"),
                    rs.getString("message"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    rs.getInt("user_id")
                ));
            }
            donationsTable.setItems(donations);
        } catch (SQLException e) {
            System.out.println("Error loading donations: " + e.getMessage());
            showAlert("Failed to load donations.");
        }
    }

    public void showDonationTab(Stage stage, double width, double height) {
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
        
        // Title
        Label titleLabel = new Label("Donations");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        titleLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));

        // Top container with back button and title
        VBox topContainer = new VBox(10);
        topContainer.setPadding(new Insets(20));
        topContainer.getChildren().addAll(backButton, titleLabel);

        // Create split pane for donation form and table
        SplitPane splitPane = new SplitPane();
        
        // Left side - Donation Form
        VBox donationForm = createDonationForm();
        
        // Right side - Donations Table
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(20));
        
        Label tableTitle = new Label("Recent Donations");
        tableTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        tableTitle.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        
        donationsTable = createDonationsTable();
        
        // Wrap table in ScrollPane
        ScrollPane scrollPane = new ScrollPane(donationsTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        tableContainer.getChildren().addAll(tableTitle, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Add components to split pane
        splitPane.getItems().addAll(donationForm, tableContainer);
        splitPane.setDividerPositions(0.4);
        
        // Set up main layout
        mainLayout.setTop(topContainer);
        mainLayout.setCenter(splitPane);
        
        // Create scene
        Scene scene = new Scene(mainLayout, width, height);
        stage.setTitle("Pet Passion - Donations");
        stage.setScene(scene);
        
        // Load initial data
        loadDonationsFromDatabase();
    }
    
    private VBox createDonationForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#333333" : "white") + ";" +
            "-fx-background-radius: 10;"
        );

        Label formTitle = new Label("Make a Donation");
        formTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        formTitle.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));

        // Name field
        Label nameLabel = new Label("Name");
        nameLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        nameField = new TextField();
        styleTextField(nameField);

        // Amount field
        Label amountLabel = new Label("Amount ($)");
        amountLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        amountField = new TextField();
        styleTextField(amountField);

        // Payment method
        Label paymentLabel = new Label("Payment Method");
        paymentLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.getItems().addAll("Credit Card", "Debit Card", "PayPal", "Bank Transfer");
        styleComboBox(paymentMethodComboBox);

        // Donation type
        Label typeLabel = new Label("Donation Type");
        typeLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        donationTypeComboBox = new ComboBox<>();
        donationTypeComboBox.getItems().addAll("One-time", "Monthly", "Annual");
        styleComboBox(donationTypeComboBox);

        // Purpose
        Label purposeLabel = new Label("Purpose");
        purposeLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        purposeComboBox = new ComboBox<>();
        purposeComboBox.getItems().addAll("Medical Treatment", "Food and Supplies", "Shelter Maintenance", "General Support");
        styleComboBox(purposeComboBox);

        // Message
        Label messageLabel = new Label("Message (Optional)");
        messageLabel.setTextFill(App.isDarkMode() ? Color.WHITE : Color.rgb(44, 62, 80));
        messageArea = new TextArea();
        messageArea.setPromptText("Add a message with your donation");
        messageArea.setPrefRowCount(3);
        styleTextArea(messageArea);

        // Submit button
        Button submitButton = new Button("Submit Donation");
        submitButton.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 12 30;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> processDonation());

        form.getChildren().addAll(
            formTitle,
            nameLabel, nameField,
            amountLabel, amountField,
            paymentLabel, paymentMethodComboBox,
            typeLabel, donationTypeComboBox,
            purposeLabel, purposeComboBox,
            messageLabel, messageArea,
            submitButton
        );

        return form;
    }
    
    private void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#444444" : "white") + ";" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "black") + ";" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;"
        );
    }
    
    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#444444" : "white") + ";" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "black") + ";" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 4;"
        );
    }
    
    private void styleTextArea(TextArea area) {
        area.setStyle(
            "-fx-background-color: " + (App.isDarkMode() ? "#444444" : "white") + ";" +
            "-fx-text-fill: " + (App.isDarkMode() ? "white" : "black") + ";" +
            "-fx-border-color: #d8dee9;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;"
        );
    }
    
    private void clearForm(TextField nameField, TextField amountField, 
                         ComboBox<String> paymentMethodBox, 
                         ComboBox<String> donationTypeBox,
                         ComboBox<String> purposeBox,
                         TextArea messageArea) {
        nameField.clear();
        amountField.clear();
        paymentMethodBox.setValue(null);
        donationTypeBox.setValue(null);
        purposeBox.setValue(null);
        messageArea.clear();
    }

    private TableView<Donation> createDonationsTable() {
        TableView<Donation> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<Donation, String> nameCol = new TableColumn<>("Donor Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("donorName"));

        TableColumn<Donation, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(tc -> new TableCell<Donation, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        TableColumn<Donation, String> purposeCol = new TableColumn<>("Purpose");
        purposeCol.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        TableColumn<Donation, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Donation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(nameCol, amountCol, purposeCol, dateCol, statusCol);
        return table;
    }
}
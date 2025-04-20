package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SecondaryController {

    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 700;

    public void showRegisterScreen(Stage stage) {
        showRegisterScreen(stage, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void showRegisterScreen(Stage stage, double width, double height) {
        // Store the current stage dimensions
        double stageWidth = width;
        double stageHeight = height;

        // Create the main container 
        BorderPane mainLayout = new BorderPane();
        
        // Create a VBox for the registration form that matches Image 1
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(40, 60, 40, 40));
        formContainer.prefWidthProperty().bind(stage.widthProperty());
        formContainer.prefHeightProperty().bind(stage.heightProperty());
        formContainer.setStyle("-fx-background-color: #e5e9f0;"); // Light gray/blue color like in Image 1
        
        // Create a back button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b4252; -fx-font-size: 14px;");
        
        // Title for the registration page
        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.rgb(59, 66, 82)); // Dark color for visibility
        titleLabel.setPadding(new Insets(20, 0, 20, 0));
        
        // Full Name field
        VBox nameBox = createInputField("Full Name", "Enter your full name");
        
        // Email Address field
        VBox emailBox = createInputField("Email Address", "Enter your email address");
        
        // Username field
        VBox usernameBox = createInputField("Username", "Choose a username");
        
        // Password field
        VBox passwordBox = createInputField("Password", "Create a password", true);
        
        // Confirm Password field
        VBox confirmPasswordBox = createInputField("Confirm Password", "Confirm your password", true);
        
        // Phone Number field
        VBox phoneBox = createInputField("Phone Number (Optional)", "Enter your phone number");
        
        // Terms and conditions checkbox
        CheckBox termsCheckbox = new CheckBox("I agree to the Terms and Conditions");
        termsCheckbox.setStyle("-fx-text-fill: #4c566a;");
        
        // Create the register button (using modern design)
        Button registerButton = new Button("CREATE ACCOUNT");
        registerButton.setPrefHeight(50);
        registerButton.setPrefWidth(200);
        registerButton.setStyle(
            "-fx-background-color: #5e81ac;" + 
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" + 
            "-fx-background-radius: 4px;"
        );
        
        // Button hover effects
        registerButton.setOnMouseEntered(e -> 
            registerButton.setStyle(
                "-fx-background-color: #81a1c1;" + 
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" + 
                "-fx-background-radius: 4px;"
            )
        );
        registerButton.setOnMouseExited(e -> 
            registerButton.setStyle(
                "-fx-background-color: #5e81ac;" + 
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" + 
                "-fx-background-radius: 4px;"
            )
        );
        
        // Center the register button
        HBox buttonBox = new HBox(registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30, 0, 20, 0));
        
        // Already have an account section
        HBox loginPrompt = new HBox(10);
        loginPrompt.setAlignment(Pos.CENTER);
        
        Label accountLabel = new Label("Already have an account?");
        accountLabel.setStyle("-fx-text-fill: #4c566a;");
        
        Hyperlink loginLink = new Hyperlink("Login here");
        loginLink.setStyle("-fx-text-fill: #5e81ac; -fx-underline: false; -fx-border-color: transparent;");
        
        loginPrompt.getChildren().addAll(accountLabel, loginLink);
        
        // Create a ScrollPane to handle overflow
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #e5e9f0; -fx-background-color: #e5e9f0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Add all components to the form container
        VBox formContent = new VBox(20);
        formContent.getChildren().addAll(
            backButton, 
            titleLabel,
            nameBox,
            emailBox,
            usernameBox,
            passwordBox,
            confirmPasswordBox,
            phoneBox,
            termsCheckbox,
            buttonBox,
            loginPrompt
        );
        
        scrollPane.setContent(formContent);
        formContainer.getChildren().add(scrollPane);
        
        // Set the formContainer as the center of the BorderPane
        mainLayout.setCenter(formContainer);
        
        // Set action handlers for buttons
        backButton.setOnAction(e -> backToHome(stage, stageWidth, stageHeight));
        
        registerButton.setOnAction(e -> {
            // Extract input values from the form fields
            String name = ((TextField)nameBox.getChildren().get(1)).getText();
            String email = ((TextField)emailBox.getChildren().get(1)).getText();
            String username = ((TextField)usernameBox.getChildren().get(1)).getText();
            String password = ((PasswordField)passwordBox.getChildren().get(1)).getText();
            String confirmPassword = ((PasswordField)confirmPasswordBox.getChildren().get(1)).getText();
            String phone = ((TextField)phoneBox.getChildren().get(1)).getText();
            
            handleRegistration(name, email, username, password, phone, stage);
        });
        
        loginLink.setOnAction(e -> {
            try {
                new PrimaryController().showLoginScreen(stage, stageWidth, stageHeight);
            } catch (Exception ex) {
                showAlert("Error opening login screen: " + ex.getMessage());
            }
        });

        // Create the scene
        Scene scene = new Scene(mainLayout, stageWidth, stageHeight);
        stage.setTitle("Register - Find Your New Bestie");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createInputField(String labelText, String promptText) {
        return createInputField(labelText, promptText, false);
    }
    
    private VBox createInputField(String labelText, String promptText, boolean isPassword) {
        VBox fieldBox = new VBox(8);
        
        // Label with light gray color as in Image 1
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(Color.rgb(229, 233, 240).darker().darker()); // Light color but still visible
        
        // Text field with styling to match Image 1
        Control inputField;
        if (isPassword) {
            inputField = new PasswordField();
        } else {
            inputField = new TextField();
        }
        
        if (inputField instanceof TextField) {
            ((TextField) inputField).setPromptText(promptText);
        }
        inputField.setPrefHeight(40);
        inputField.setMaxWidth(Double.MAX_VALUE);
        
        // Style to match the clean design in Image 1
        inputField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 4px;" + 
            "-fx-border-color: #d8dee9;" + 
            "-fx-border-radius: 4px;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 8px 12px;" +
            "-fx-font-size: 14px;"
        );
        
        fieldBox.getChildren().addAll(label, inputField);
        return fieldBox;
    }

    private void handleRegistration(String name, String email, String username, String password, String phone, Stage currentStage) {
        if (name == null || name.trim().isEmpty()) {
            showAlert("Name cannot be empty");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            showAlert("Email cannot be empty");
            return;
        }
        
        if (username == null || username.trim().isEmpty()) {
            showAlert("Username cannot be empty");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showAlert("Password cannot be empty");
            return;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            showAlert("Phone number cannot be empty");
            return;
        }
        
        try {
            if (DatabaseUtil.registerUser(name, email, username, password, phone)) {
                int userId = DatabaseUtil.getUserId(username);
                if (userId > 0) {
                    boolean wasFullScreen = currentStage.isFullScreen();
                    double width = currentStage.getWidth();
                    double height = currentStage.getHeight();
                    
                    new DashboardController().showDashboard(currentStage, width, height, userId);
                    if (wasFullScreen) {
                        currentStage.setFullScreen(true);
                    }
                } else {
                    showAlert("Error: Could not retrieve user information");
                }
            } else {
                showAlert("Error registering user");
            }
        } catch (Exception e) {
            showAlert("Error during registration: " + e.getMessage());
        }
    }

    private void backToHome(Stage stage, double width, double height) {
        try {
            new App().start(stage, width, height);
        } catch (Exception e) {
            showAlert("Error returning to home: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
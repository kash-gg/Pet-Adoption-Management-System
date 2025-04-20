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
import javafx.stage.Window;
import javafx.scene.Node;

public class PrimaryController {

    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 700;
    private TextField usernameField;

    public void showLoginScreen(Stage stage) {
        showLoginScreen(stage, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void showLoginScreen(Stage stage, double width, double height) {
        // Store the current stage dimensions
        double stageWidth = width;
        double stageHeight = height;

        // Create the main container 
        BorderPane mainLayout = new BorderPane();
        
        // Create a VBox for the login form that matches the registration form style
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(40, 60, 40, 40));
        formContainer.prefWidthProperty().bind(stage.widthProperty());
        formContainer.prefHeightProperty().bind(stage.heightProperty());
        formContainer.setStyle("-fx-background-color: #e5e9f0;"); // Light gray/blue color like in Image 1
        
        // Create a back button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b4252; -fx-font-size: 14px;");
        
        // Title for the login page
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.rgb(59, 66, 82)); // Dark color for visibility
        titleLabel.setPadding(new Insets(20, 0, 20, 0));
        
        // Subtitle
        Label subtitleLabel = new Label("Login to your account to continue");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.rgb(76, 86, 106));
        subtitleLabel.setPadding(new Insets(0, 0, 20, 0));
        
        // Username/Email field
        VBox usernameBox = createInputField("Username or Email", "Enter your username or email");
        
        // Password field
        VBox passwordBox = createInputField("Password", "Enter your password", true);
        
        // Options row (Remember me and Forgot password)
        HBox optionsBox = new HBox();
        optionsBox.setAlignment(Pos.CENTER_LEFT);
        optionsBox.setSpacing(50);
        
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill: #4c566a;");
        
        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.setStyle("-fx-text-fill: #5e81ac; -fx-underline: false; -fx-border-color: transparent;");
        
        optionsBox.getChildren().addAll(rememberMe, forgotPassword);
        optionsBox.setPadding(new Insets(5, 0, 20, 0));
        
        // Create the login button (using modern design)
        Button loginButton = new Button("LOG IN");
        loginButton.setPrefHeight(50);
        loginButton.setPrefWidth(200);
        loginButton.setStyle(
            "-fx-background-color: #5e81ac;" + 
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" + 
            "-fx-background-radius: 4px;"
        );
        
        // Button hover effects
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle(
                "-fx-background-color: #81a1c1;" + 
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" + 
                "-fx-background-radius: 4px;"
            )
        );
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle(
                "-fx-background-color: #5e81ac;" + 
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" + 
                "-fx-background-radius: 4px;"
            )
        );
        
        // Center the login button
        HBox buttonBox = new HBox(loginButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 20, 0));
        
        // Don't have an account section
        HBox registerPrompt = new HBox(10);
        registerPrompt.setAlignment(Pos.CENTER);
        
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-text-fill: #4c566a;");
        
        Hyperlink registerLink = new Hyperlink("Create one now");
        registerLink.setStyle("-fx-text-fill: #5e81ac; -fx-underline: false; -fx-border-color: transparent;");
        
        registerPrompt.getChildren().addAll(noAccountLabel, registerLink);
        
        // Create content VBox
        VBox formContent = new VBox(10);
        formContent.getChildren().addAll(
            backButton, 
            titleLabel,
            subtitleLabel,
            usernameBox,
            passwordBox,
            optionsBox,
            buttonBox,
            registerPrompt
        );
        
        // Wrap in a ScrollPane in case of small screens
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #e5e9f0; -fx-background-color: #e5e9f0;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(formContent);
        
        formContainer.getChildren().add(scrollPane);
        
        // Set the formContainer as the center of the BorderPane
        mainLayout.setCenter(formContainer);
        
        // Set action handlers for buttons
        backButton.setOnAction(e -> backToHome(stage, stageWidth, stageHeight));
        
        loginButton.setOnAction(e -> {
            String username = ((TextField)usernameBox.getChildren().get(1)).getText();
            String password = ((PasswordField)passwordBox.getChildren().get(1)).getText();
            handleLogin(username, password);
        });
        
        registerLink.setOnAction(e -> {
            try {
                new SecondaryController().showRegisterScreen(stage, stageWidth, stageHeight);
            } catch (Exception ex) {
                showAlert("Error opening registration screen: " + ex.getMessage());
            }
        });
        
        forgotPassword.setOnAction(e -> showAlert("Password reset functionality will be implemented here."));

        // Create the scene
        Scene scene = new Scene(mainLayout, stageWidth, stageHeight);
        stage.setTitle("Login - Find Your New Bestie");
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
            usernameField = (TextField)inputField;
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

    private void handleLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showAlert("Username cannot be empty");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showAlert("Password cannot be empty");
            return;
        }
        
        try {
            if (DatabaseUtil.validateUser(username, password)) {
                int userId = DatabaseUtil.getUserId(username);
                if (userId > 0) {
                    Stage currentStage = (Stage) usernameField.getScene().getWindow();
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
                showAlert("Invalid username or password");
            }
        } catch (Exception e) {
            showAlert("Error during login: " + e.getMessage());
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
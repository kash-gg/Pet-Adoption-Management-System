package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class App extends Application {

    private static final String APP_TITLE = "Pet Passion";
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    
    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, SCENE_WIDTH, SCENE_HEIGHT);
    }
    
    public void start(Stage primaryStage, double width, double height) {
        // Set up the main container with a gradient background
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        
        // Create a stylish background with linear gradient
        String gradientStyle = "-fx-background-color: linear-gradient(to bottom right, #2c3e50, #3498db);";
        root.setStyle(gradientStyle);
        
        // Application Title
        Label titleLabel = new Label(APP_TITLE);
        titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        
        // Welcome message
        Label welcomeLabel = new Label("Welcome to your one stop pet adoption and donation app!");
        welcomeLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        welcomeLabel.setTextFill(Color.WHITE);
        
        // Container for buttons to allow proper styling and layout
        HBox buttonContainer = new HBox(30);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(40, 0, 0, 0));
        
        // Create stylish buttons with hover effects
        Button loginButton = createStyledButton("Login");
        Button registerButton = createStyledButton("Register");
        
        // Add buttons to the container
        buttonContainer.getChildren().addAll(loginButton, registerButton);
        
        // Add all elements to the root container
        root.getChildren().addAll(titleLabel, welcomeLabel, buttonContainer);
        
        // Set up action handlers for buttons
        loginButton.setOnAction(e -> openLoginWindow(primaryStage, width, height));
        registerButton.setOnAction(e -> openRegisterWindow(primaryStage, width, height));
        
        // Create the scene and set up the stage
        Scene scene = new Scene(root, width, height);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        
        // Set application icon (optional)
        try {
            primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/resources/app-icon.png")));
        } catch (Exception e) {
            System.out.println("Icon not found, continuing without it.");
        }
        
        primaryStage.show();
    }
    
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        
        // Basic button styling
        button.setPrefSize(150, 50);
        button.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        
        // Custom CSS styling for the button
        String buttonStyle = 
            "-fx-background-color: #ecf0f1;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;";
        
        String hoverStyle = 
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25;";
        
        button.setStyle(buttonStyle);
        
        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        button.setEffect(shadow);
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(buttonStyle));
        
        return button;
    }
    
    private void openLoginWindow(Stage primaryStage, double width, double height) {
        // We'll call the PrimaryController to handle this
        System.out.println("Opening login window...");
        try {
            new PrimaryController().showLoginScreen(primaryStage, width, height);
        } catch (Exception e) {
            System.err.println("Error opening login window: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void openRegisterWindow(Stage primaryStage, double width, double height) {
        // We'll call the SecondaryController to handle this
        System.out.println("Opening registration window...");
        try {
            new SecondaryController().showRegisterScreen(primaryStage, width, height);
        } catch (Exception e) {
            System.err.println("Error opening registration window: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
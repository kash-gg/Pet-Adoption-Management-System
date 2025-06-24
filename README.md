# Pet Adoption Management System

A JavaFX-based desktop application to manage pet adoptions for animal shelters. The system allows users to handle adoption records and applications through an interactive graphical user interface.

## Features

- View and manage pet adoption records
- Submit and track adoption applications
- Interactive table-based UI using JavaFX
- Clean, modular code using MVC architecture
- Object-oriented models for application logic

## Technologies Used

- Java 17+
- JavaFX
- Maven
- MVC Architecture

## How It Works

- `Adoption.java` and `AdoptionApplication.java` are model classes that represent adoption records and applications.
- `AdoptionController.java` handles the input and processing of new adoption records.
- `AdoptionsTableController.java` manages the display and updating of adoption data in a table format.
- `App.java` launches the JavaFX GUI application.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- JavaFX SDK (if using a standalone setup)

### Steps to Run

```bash
git clone https://github.com/Shivam-Kapure/Pet-Adoption-Management-System.git
mvn clean javafx:run

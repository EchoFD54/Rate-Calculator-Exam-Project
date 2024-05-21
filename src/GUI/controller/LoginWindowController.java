package GUI.controller;

import BE.User;
import GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginWindowController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final ArrayList<User> users = new ArrayList<>();
    private final Model model = new Model();


    @FXML
    private void clickLogin(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        for (User user : users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                openHomeWindow();
                return;
            }
        }
        // Show an alert if the username or password is incorrect
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Incorrect username or password.");
        loadAlertStyle(alert);
        alert.showAndWait();
    }


    private void openHomeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/EmployeeWindowView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        try {
            users.addAll(model.getUsersFromDB());
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing user data: " + e.getMessage(), e);
        }

        usernameField.setOnKeyPressed(this::handleKeyPress);
        passwordField.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            clickLogin(new ActionEvent());
        }
    }

    private void loadAlertStyle(Alert alert) {
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }
}

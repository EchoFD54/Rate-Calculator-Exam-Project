package GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {
    public void openAddEmployee(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddEmployeeView.fxml"));
        Parent root;
        try {
            root = loader.load();
            AddEmployeeController addEmployeeController = loader.getController();
            addEmployeeController.setMainWindowController(this);
            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateEmployeeProperties(){

    }
}

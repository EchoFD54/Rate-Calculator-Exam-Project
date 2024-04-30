package GUI.controller;

import BE.Employee;
import BLL.EmployeeManager;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddEmployeeController {
    public TextField empNameField, empAnnSalaryField, empMultPerField, empFixedAnnAmtField, empCountryField, empTeamField, empWorkHoursField, empUtilizationField;
    public ChoiceBox OverheadChoiceBox;

    private MainWindowController mainWindowController;
    private Stage stage;

    private EmployeeManager employeeManager = new EmployeeManager();

    public void addEmployeeBtn(ActionEvent actionEvent) throws SQLException {
        String name = empNameField.getText();
        String annSalary = empAnnSalaryField.getText();
        String multPer = empMultPerField.getText();
        String fixedAnnAmt = empFixedAnnAmtField.getText();
        String country = empCountryField.getText();
        String team = empTeamField.getText();
        String workHours = empWorkHoursField.getText();
        String utilization = empUtilizationField.getText();
        Boolean isOverheadCost;
        if (OverheadChoiceBox.getSelectionModel().getSelectedItem().equals("Overhead Cost")) {
            isOverheadCost = true;
        } else {
            isOverheadCost = false;
        }


        //add employee to database and tableview
       mainWindowController.updateEmployeeProperties(name, annSalary, multPer, fixedAnnAmt, country, team, workHours, utilization, isOverheadCost);

        ((Stage) empNameField.getScene().getWindow()).close();



    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }


}

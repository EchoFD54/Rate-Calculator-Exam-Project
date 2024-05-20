package GUI.controller;

import BE.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;

public class AddEmployeeController {
    @FXML
    private TextField empNameField, empAnnSalaryField, empMultPerField, empFixedAnnAmtField, empWorkHoursField, empUtilizationField, empDayHoursField;
    @FXML
    private ChoiceBox OverheadChoiceBox;
    public Button addEmployeeBtn;
    @FXML
    private ChoiceBox<String> empCountryChoiceBox;

    private EmployeeWindowController employeeWindowController;

    private ObservableList<String> countries = FXCollections.observableArrayList();
    private Employee employee;

    public void initialize() {
      loadCountries();
      empCountryChoiceBox.setItems(countries);
    }

    private void loadCountries() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Resources/CountryList"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                countries.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addEmployeeBtn(ActionEvent actionEvent) throws SQLException {
        String name = empNameField.getText();
        String annSalary = empAnnSalaryField.getText();
        String multPer = empMultPerField.getText();
        String fixedAnnAmt = empFixedAnnAmtField.getText();
        String country = empCountryChoiceBox.getValue();
        String workHours = empWorkHoursField.getText();
        String utilization = empUtilizationField.getText();
        String dayHours = empDayHoursField.getText();
        int id = employee.getId();
        Boolean isOverheadCost;

        if (OverheadChoiceBox.getSelectionModel().getSelectedItem().equals("Overhead Cost")) {
            isOverheadCost = true;
        } else {
            isOverheadCost = false;
        }

        // Check if all input are valid numbers and if the percentage is within a valid range
        boolean isValidInput = true;
        String invalidField = "";

        if (!isValidDouble(annSalary)) {
            isValidInput = false;
            invalidField = "Annual Salary";
        } else if (!isValidDouble(multPer)) {
            isValidInput = false;
            invalidField = "Multiplier Percentage";
        } else if (!isValidDouble(fixedAnnAmt)) {
            isValidInput = false;
            invalidField = "Fixed Annual Amount";
        } else if (!isValidDouble(workHours)) {
            isValidInput = false;
            invalidField = "Work Hours";
        } else if (!isValidDouble(utilization) || !isValidUtilization(utilization)) {
            isValidInput = false;
            invalidField = "Utilization";
        }

        if (isValidInput){
            // Add employee to database and tableview if input is valid
            employeeWindowController.updateEmployeeProperties(id, name, annSalary, multPer, fixedAnnAmt, country, workHours, utilization, isOverheadCost, dayHours);
            ((Stage) empNameField.getScene().getWindow()).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please input a valid number for " + invalidField);
            loadAlertStyle(alert);
            alert.showAndWait();
        }
    }

    private boolean isValidUtilization(String utilization) {
        try {
            double utilizationValue = Double.parseDouble(utilization);
            return utilizationValue >= 0 && utilizationValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setEmployeeWindowController(EmployeeWindowController employeeWindowController) {
        this.employeeWindowController = employeeWindowController;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        empNameField.setText(employee.getName());
        empNameField.setText(employee.getName());
        empAnnSalaryField.setText(employee.getAnnualSalary() + "");
        empMultPerField.setText(employee.getOverheadMultPercent() + "");
        empFixedAnnAmtField.setText(employee.getFixedAnnualAmount() + "");
        empWorkHoursField.setText(employee.getAnnualWorkingHours() + "");
        empUtilizationField.setText(employee.getUtilizationPercentage() + "");
        empDayHoursField.setText(employee.getDailyHours() + "");

        empCountryChoiceBox.setValue(employee.getCountry());

        if (employee.isOverHeadCost()){
            OverheadChoiceBox.setValue("Overhead Cost");
        } else {
            OverheadChoiceBox.setValue("Production Resource");
        }
    }

    private void loadAlertStyle(Alert alert) {
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }
}

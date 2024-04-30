package GUI.controller;

import BE.Employee;
import BLL.EmployeeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainWindowController {
    public TableView<Employee> employeeTableView;
    public Label employeeNameLbl, employeeCountryLbl, employeeAnnSalLbl, employeOverMultLbl, employeeFixAmtLbl, employeeTeamLbl, employeeEffectHoursLbl, employeeUtilizationLbl, employeeBooleanLbl;

    private EmployeeManager employeeManager = new EmployeeManager();


    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setDataBase();
        setEmployeeTab();
    }

    private void setEmployeeTableView(){
        //set columns
        TableColumn<Employee, String> employeeNameColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(0);
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Employee, String> employeeTeamColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(1);
        employeeTeamColumn.setCellValueFactory(new PropertyValueFactory<>("employeeTeam"));
    }

    private void setDataBase() throws SQLException {
        try {
            for (Employee employee : employeeManager.getAllEmployees()){
                employeeTableView.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setEmployeeTab() {
        // Add listener to the TableView's selection model
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLabels(newSelection); // Update labels with information of the selected employee
            }
        });
    }

    private void updateLabels(Employee employee) {
        employeeNameLbl.setText(employee.getName());
        employeeCountryLbl.setText("Country: " + employee.getCountry());
        employeeAnnSalLbl.setText("Annual Salary: " + employee.getAnnualSalary());
        employeOverMultLbl.setText("Overhead Multiplier Percent: " + employee.getOverheadMultPercent());
        employeeFixAmtLbl.setText("Fixed annual amount: " + employee.getFixedAnnualAmount());
        employeeTeamLbl.setText("Team: " + employee.getEmployeeTeam());
        employeeEffectHoursLbl.setText("Annual Effective Working Hours: " + employee.getAnnualWorkingHours());
        employeeUtilizationLbl.setText("Utilization Percentage: " + employee.getUtilizationPercentage());
        if (employee.isOverHeadCost()){
            employeeBooleanLbl.setText("Overhead Cost");
        } else {
            employeeBooleanLbl.setText("Production Resource");
        }
    }

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

    protected void updateEmployeeProperties(String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization, Boolean isOverHeadCost) throws SQLException {
        boolean employeeExists = false;
        Employee existingEmployee = null;
        //update employee
        for (Employee employee : employeeTableView.getItems()){
            if (employee.getName().equals(name)){
                existingEmployee = employee;
                existingEmployee.setName(name);
                existingEmployee.setAnnualSalary(Integer.parseInt(annSalary));
                existingEmployee.setOverheadMultPercent(Integer.parseInt(multPer));
                existingEmployee.setFixedAnnualAmount(Integer.parseInt(fixedAnnAmt));
                existingEmployee.setEmployeeTeam(team);
                existingEmployee.setAnnualWorkingHours(Integer.parseInt(workHours));
                existingEmployee.setUtilizationPercentage(Integer.parseInt(utilization));
                existingEmployee.setOverHeadCost(isOverHeadCost);
                //update employee on database (cant edit name for now)
                employeeManager.updateEmployee(existingEmployee);
              employeeExists = true;
              break;
            }
        }

        //create employee
        if (!employeeExists){
            Employee newEmployee = new Employee(name, annSalary, multPer, fixedAnnAmt, country, team, workHours, utilization, isOverHeadCost);
            int employeeID = employeeManager.createEmployee(newEmployee);
            newEmployee.setId(employeeID);
            employeeTableView.getItems().add(newEmployee);
        }

    }

    public void deleteEmployee(ActionEvent actionEvent) {
        Employee selectedEmployee = (Employee) employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this employee?");
            alert.setContentText("This action cannot be undone.");
           loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int selectedIndex = employeeTableView.getSelectionModel().getSelectedIndex();
                    try {
                        employeeManager.deleteEmployee(selectedEmployee.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    employeeTableView.getItems().remove(selectedIndex);
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employee Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to delete.");
            loadAlertStyle(alert);
            alert.showAndWait();
        }
        }


    public void openEditEmployee(ActionEvent actionEvent) {
        Employee selectedEmployee = (Employee) employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddEmployeeView.fxml"));
            Parent root;
            try {
                root = loader.load();
                AddEmployeeController addEmployeeController = loader.getController();
                addEmployeeController.setMainWindowController(this);

                //set employee properties
                addEmployeeController.empNameField.setText(selectedEmployee.getName());
                addEmployeeController.empAnnSalaryField.setText(selectedEmployee.getAnnualSalary() + "");
                addEmployeeController.empMultPerField.setText(selectedEmployee.getOverheadMultPercent() + "");
                addEmployeeController.empFixedAnnAmtField.setText(selectedEmployee.getFixedAnnualAmount() + "");
                addEmployeeController.empCountryField.setText(selectedEmployee.getCountry());
                addEmployeeController.empTeamField.setText(selectedEmployee.getEmployeeTeam());
                addEmployeeController.empWorkHoursField.setText(selectedEmployee.getAnnualWorkingHours() + "");
                addEmployeeController.empUtilizationField.setText(selectedEmployee.getUtilizationPercentage() + "");

                Stage stage = new Stage();
                stage.setTitle("Edit Employee");
                stage.setScene(new Scene(root));
                stage.show();
                addEmployeeController.addEmployeeBtn.setText("Edit Employee");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employee Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to edit.");
            alert.showAndWait();
        }

    }

    private void loadAlertStyle(Alert alert){
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }


}


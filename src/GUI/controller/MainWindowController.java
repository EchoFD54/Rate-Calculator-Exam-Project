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
    public Label employeeNameLbl, employeeCountryLbl;

    private EmployeeManager employeeManager = new EmployeeManager();

    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setDataBase();

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

    protected void updateEmployeeProperties(String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization) throws SQLException {
        boolean employeeExists = false;
        Employee existingEmployee = null;
        //update employee
        for (Employee employee : employeeTableView.getItems()){
            if (employee.getName().equals(name)){
              employeeExists = true;
            }
        }

        //create employee
        if (!employeeExists){
            Employee newEmployee = new Employee(name, annSalary, multPer, fixedAnnAmt, country, team, workHours, utilization);
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
            alert.showAndWait();
        }
        }



    }


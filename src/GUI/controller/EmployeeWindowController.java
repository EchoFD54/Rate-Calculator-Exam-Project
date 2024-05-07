package GUI.controller;

import BE.Employee;
import BE.Team;
import BLL.EmployeeManager;
import BLL.TeamManager;
import javafx.collections.ObservableList;
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

public class EmployeeWindowController {
    public TableView<Employee> employeeTableView;
    public Label employeeNameLbl, employeeCountryLbl, employeeAnnSalLbl, employeOverMultLbl, employeeFixAmtLbl, employeeTeamLbl,
            employeeEffectHoursLbl, employeeUtilizationLbl, employeeBooleanLbl, hourRateLbl, dailyRateLbl;
    public TableView<Team> teamsEmployeeTableView;

    private EmployeeManager employeeManager = new EmployeeManager();
    private TeamManager teamManager = new TeamManager();


    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setDataBase();
        setEmployeeTab();
        setTeamTableView();
    }

    private void setEmployeeTableView() {
        TableColumn<Employee, String> employeeNameColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(0);
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Employee, String> employeeTeamColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(1);
        employeeTeamColumn.setCellValueFactory(new PropertyValueFactory<>("employeeTeam"));
    }

    private void setDataBase() throws SQLException {
        try {
            for (Employee employee : employeeManager.getAllEmployees()) {
                employeeTableView.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setEmployeeTab() {
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    updateLabels(newSelection); // update labels with information of the selected employee
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setTeamTableView() {
        TableColumn<Team, String> teamNameColumn = (TableColumn<Team, String>) teamsEmployeeTableView.getColumns().get(0);
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        try {
            for (Team team : teamManager.getAllTeams()) {
                teamsEmployeeTableView.getItems().add(team);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addToTeamBtn(ActionEvent actionEvent) throws SQLException {
        addSelectedEmployeeToTeam();
    }

    private void addSelectedEmployeeToTeam() throws SQLException {
        Integer teamId = teamsEmployeeTableView.getSelectionModel().getSelectedItem().getTeamId();
        Integer employeeId = employeeTableView.getSelectionModel().getSelectedItem().getId();
        if (teamId != null && employeeId != null) {
            teamManager.addEmployeeToTeam(employeeId, teamId);
        }
    }

    public void removeEmployeeFromTeamBtn(ActionEvent actionEvent) throws SQLException {
        removeSelectedEmployeeFromTeam();
    }

    private void removeSelectedEmployeeFromTeam() throws SQLException {
        Integer employeeId = employeeTableView.getSelectionModel().getSelectedItem().getId();
        try {
            teamManager.removeEmployeeFromTeam(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateLabels(Employee employee) throws SQLException {
        employeeNameLbl.setText(employee.getName());
        employeeCountryLbl.setText("Country: " + employee.getCountry());
        employeeAnnSalLbl.setText("Annual Salary: " + employee.getAnnualSalary());
        employeOverMultLbl.setText("Overhead Multiplier Percent: " + employee.getOverheadMultPercent());
        employeeFixAmtLbl.setText("Fixed annual amount: " + employee.getFixedAnnualAmount());
        String teamName = employeeManager.getTeamName(employee.getId());
        employeeTeamLbl.setText("Team: " + teamName);
        employeeEffectHoursLbl.setText("Annual Effective Working Hours: " + employee.getAnnualWorkingHours());
        employeeUtilizationLbl.setText("Utilization Percentage: " + employee.getUtilizationPercentage());
        if (employee.isOverHeadCost()) {
            employeeBooleanLbl.setText("Overhead Cost");
        } else {
            employeeBooleanLbl.setText("Production Resource");
        }
        String hourlyRate = String.valueOf(employee.calculateHourlyDate());
        hourRateLbl.setText("Hourly Rate: " + hourlyRate);
        String dailyRate = String.valueOf(employee.calculateDailyRate());
        dailyRateLbl.setText("Daily Rate: " + dailyRate);
    }

    public void openAddEmployee(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddEmployeeView.fxml"));
        Parent root;
        try {
            root = loader.load();
            AddEmployeeController addEmployeeController = loader.getController();
            addEmployeeController.setEmployeeWindowController(this);
            Employee employee = new Employee();
            addEmployeeController.setEmployee(employee);
            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateEmployeeProperties(int id, String name, String annSalary, String multPer, String fixedAnnAmt, String country, String workHours, String utilization, Boolean isOverHeadCost) throws SQLException {
        boolean employeeExists = false;
        Employee existingEmployee = null;
        //update employee
        for (Employee employee : employeeTableView.getItems()) {
            if (employee.getId() == id) {
                existingEmployee = employee;
                existingEmployee.setName(name);
                existingEmployee.setAnnualSalary(Double.parseDouble(annSalary));
                existingEmployee.setOverheadMultPercent(Double.parseDouble(multPer));
                existingEmployee.setFixedAnnualAmount(Double.parseDouble(fixedAnnAmt));
                existingEmployee.setCountry(country);
                existingEmployee.setAnnualWorkingHours(Double.parseDouble(workHours));
                existingEmployee.setUtilizationPercentage(Double.parseDouble(utilization));
                existingEmployee.setOverHeadCost(isOverHeadCost);
                //update employee on database
                employeeManager.updateEmployee(existingEmployee);
                refreshEmployeeTable(existingEmployee);
                employeeExists = true;
                break;
            }
        }

        //create employee
        if (!employeeExists) {
            Employee newEmployee = new Employee(name, annSalary, multPer, fixedAnnAmt, country, workHours, utilization, isOverHeadCost);
            int employeeID = employeeManager.createEmployee(newEmployee);
            newEmployee.setId(employeeID);
            employeeTableView.getItems().add(newEmployee);
        }

    }

    public void refreshEmployeeTable(Employee updatedEmployee) {
        ObservableList<Employee> items = employeeTableView.getItems();
        int index = items.indexOf(updatedEmployee);
        if (index >= 0) {
            items.set(index, updatedEmployee);
        }
    }

    public void deleteEmployee(ActionEvent actionEvent) {
        Employee selectedEmployee = (Employee) employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
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
        if (selectedEmployee != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddEmployeeView.fxml"));
            Parent root;
            try {
                root = loader.load();
                AddEmployeeController addEmployeeController = loader.getController();
                addEmployeeController.setEmployeeWindowController(this);
                addEmployeeController.setEmployee(selectedEmployee);
                Stage stage = new Stage();
                stage.setTitle("Edit Employee");
                stage.setScene(new Scene(root));
                stage.show();
                addEmployeeController.addEmployeeBtn.setText("Edit Employee");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Employee Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to edit.");
            alert.showAndWait();
        }

    }

    private void loadAlertStyle(Alert alert) {
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }



}


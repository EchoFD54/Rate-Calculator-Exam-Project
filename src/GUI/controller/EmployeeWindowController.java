package GUI.controller;

import BE.Employee;
import BE.Team;
import BLL.EmployeeManager;
import BLL.TeamManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import java.util.List;
import java.util.StringJoiner;

public class EmployeeWindowController {
    public TableView<Employee> employeeTableView;
    public TableView<Team> teamsTableView;
    public TableColumn<Team, String> teamNameColumn;
    public TableColumn<Team, String> teamEmployeesColumn;
    public Label employeeNameLbl, employeeCountryLbl, employeeAnnSalLbl, employeOverMultLbl, employeeFixAmtLbl, employeeTeamLbl,
            employeeEffectHoursLbl, employeeUtilizationLbl, employeeBooleanLbl, hourRateLbl, dailyRateLbl;
    public TableView<Team> teamsEmployeeTableView;

    private final EmployeeManager employeeManager = new EmployeeManager();
    private final TeamManager teamManager = new TeamManager();
    public TextField searchTextField;
    public Button searchBtn;
    


    private Boolean isFilterActive = false;


    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setTeamsTableView();
        setDataBase();
        setTeamsDatabase();
        setEmployeeTab();
        setTeamTableView();
        setButtons();
    }

    private void setEmployeeTableView() {
        TableColumn<Employee, String> employeeNameColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(0);
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

    }

    private void setTeamsTableView() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        teamEmployeesColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            try {
                List<Employee> employees = teamManager.getEmployeesFromTeam(team.getTeamId());
                StringJoiner employeeNames = new StringJoiner(", ");
                for (Employee employee : employees) {
                    employeeNames.add(employee.getName());
                }
                return new SimpleStringProperty(employeeNames.toString());
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving employees for team: " + e.getMessage(), e);
            }
        });
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

    private void setTeamsDatabase() throws SQLException {
        try {
            teamsTableView.getItems().addAll(teamManager.getAllTeams());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving teams: " + e.getMessage(), e);
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

    private void setButtons(){
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                try {
                    clearFilter();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
        employeOverMultLbl.setText("Overhead Multiplier Percentage: " + employee.getOverheadMultPercent());
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
            //add to database
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
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
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
            loadAlertStyle(alert);
            alert.showAndWait();
        }

    }

    public void openAddTeam(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddTeamVIew.fxml"));
        Parent root;
        try {
            root = loader.load();
            AddTeamController addTeamController = loader.getController();
            addTeamController.setEmployeeWindowController(this);
            Team newTeam = new Team();
            addTeamController.setTeam(newTeam);
            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTeamProperties(int teamId, String name) throws SQLException {
        boolean teamExists = false;
        Team existingTeam = null;
        for (Team team : teamsTableView.getItems()) {
            if (team.getTeamId() == teamId){
                existingTeam = team;
                existingTeam.setName(name);
                //update team on database and refresh tableview
                teamManager.updateTeam(existingTeam);
                refreshTeamsTableView(existingTeam);
                teamExists = true;
                break;
            }
        }

        if (!teamExists){
            Team newTeam = new Team(name);
            int teamID = teamManager.createTeam(newTeam);
            newTeam.setTeamId(teamID);
            teamsTableView.getItems().add(newTeam);

        }

    }

    private void refreshTeamsTableView(Team updatedTeam) {
        ObservableList<Team> items = teamsTableView.getItems();
        int index = items.indexOf(updatedTeam);
        if (index >= 0){
            items.set(index, updatedTeam);
        }


    }

    public void openEditTeam(ActionEvent actionEvent) {
        Team selectedTeam = (Team) teamsTableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddTeamView.fxml"));
            Parent root;
            try {
                root = loader.load();
                AddTeamController addTeamController = loader.getController();
                addTeamController.setEmployeeWindowController(this);
                addTeamController.setTeam(selectedTeam);
                Stage stage = new Stage();
                stage.setTitle("Edit Team");
                stage.setScene(new Scene(root));
                stage.show();
                addTeamController.addTeamBtn.setText("Edit Team");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Team Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a Team to edit.");
            loadAlertStyle(alert);
            alert.showAndWait();
        }
    }

    public void deleteTeam(ActionEvent actionEvent) {
        Team selectedTeam = (Team) teamsTableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this team?");
            alert.setContentText("This action cannot be undone.");
            loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int selectedIndex = teamsTableView.getSelectionModel().getSelectedIndex();
                    try {
                        teamManager.deleteTeam(selectedTeam.getTeamId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    teamsTableView.getItems().remove(selectedIndex);
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

    private void loadAlertStyle(Alert alert) {
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }


    public void toggleFilter(ActionEvent actionEvent) throws SQLException {
         if (isFilterActive){
           clearFilter();
        } else {
        applyFilter();
         }
    }

    private void applyFilter() throws SQLException {
        String searchQuery = searchTextField.getText().toLowerCase();
        ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

        for ( Employee employee : employeeManager.getAllEmployees()){
            if (employee.getName().toLowerCase().contains(searchQuery)){
                filteredEmployees.add(employee);
            }
        }

        employeeTableView.setItems(filteredEmployees);
        searchBtn.setText("Clear");
        isFilterActive = true;
    }

    private void clearFilter() throws SQLException {
        employeeTableView.setItems(FXCollections.observableArrayList(employeeManager.getAllEmployees()));
        searchTextField.clear();
        searchBtn.setText("Search");
        isFilterActive = false;
    }

}


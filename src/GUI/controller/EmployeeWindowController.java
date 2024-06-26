package GUI.controller;

import BE.Employee;
import BE.EmployeeInTeam;
import BE.Team;
import GUI.model.Model;
import GUI.model.RateCalculator;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class EmployeeWindowController {
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    public TableColumn<Employee, String> employeeNameColumn;
    @FXML
    private TableView<Team> teamsTableView;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, String> teamEmployeesColumn;
    @FXML
    private Label teamNameLbl, teamDailyRateLbl, teamCostLbl, teamHourlyRateLbl, teamRevenueLbl,
            teamHourRateWithMultLbl, teamDayRateWithMultLbl, teamCostWithMultiLbl,
            teamRevenueWithMultiLbl, teamDailyHoursLbl, teamCountriesLbl;
    @FXML
    private Label booleanLbl, nameLbl, teamsLbl, countryLbl, hourlyRateLbl, dailyRateLbl,
            annualSalaryLbl, overheadMultiLbl, fixedAmountLbl, annualHoursLbl, utilizationLbl;

    @FXML
    private TextField searchTextField, markupTextField, gmTextField;
    @FXML
    private Button searchBtn;

    private final Model model = Model.getInstance();

    private final RateCalculator rateCalculator = RateCalculator.getInstance();

    private Boolean isFilterActive = false;


    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setTeamsTableView();
        setDataBase();
        setTeamsDatabase();
        setEmployeeTab();
        setTeamTab();
        setButtons();
    }

    private void setEmployeeTableView() {
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void setTeamsTableView() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        teamEmployeesColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            try {
                List<EmployeeInTeam> employees = model.getEmployeesInTeamFromDB(team.getTeamId());
                StringJoiner employeeNames = new StringJoiner(", ");
                for (EmployeeInTeam employee : employees) {
                    employeeNames.add(employee.getEmployee().getName());
                }
                return new SimpleStringProperty(employeeNames.toString());
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving employees for team: " + e.getMessage(), e);
            }
        });

    }

    private void setDataBase() {
        try {
            for (Employee employee : model.getEmployeesFromDB()) {
                employeeTableView.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTeamsDatabase() {
        try {
            teamsTableView.getItems().addAll(model.getTeamsFromDB());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving teams: " + e.getMessage(), e);
        }
    }

    private void setEmployeeTab() {
        employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    updateEmployeeLabels(newSelection); // update labels with information of the selected Employee
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void setTeamTab() {
        teamsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    updateTeamLabels(newSelection); // update labels with information of the selected Team
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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

    private void updateEmployeeLabels(Employee employee) throws SQLException {
        List<String> employeeInTeams = model.getTeamsFromDBUsingEmployee(employee.getId());
        nameLbl.setText(employee.getName());
        if (employee.isOverHeadCost()){
            booleanLbl.setText("Overhead Cost");
        }else {
            booleanLbl.setText("Production Resource");
        }
        teamsLbl.setText(String.valueOf(employeeInTeams));
        countryLbl.setText(employee.getCountry());
        String hourlyRate = String.valueOf(rateCalculator.calculateHourlyRate(employee));
        hourlyRateLbl.setText(hourlyRate);
        String dailyRate = String.valueOf(rateCalculator.calculateDailyRate(employee));
        dailyRateLbl.setText(dailyRate);
        annualSalaryLbl.setText(String.valueOf(employee.getAnnualSalary()));
        overheadMultiLbl.setText(String.valueOf(employee.getOverheadMultPercent()));
        fixedAmountLbl.setText(String.valueOf(employee.getFixedAnnualAmount()));
        annualHoursLbl.setText(String.valueOf(employee.getAnnualWorkingHours()));
        utilizationLbl.setText(String.valueOf(employee.getUtilizationPercentage()));
    }

    private void updateTeamLabels(Team team) throws SQLException {
        resetFields();
        List<EmployeeInTeam> employeeInTeams = model.getEmployeesInTeamFromDB(team.getTeamId());

        //set Teams Information
        teamNameLbl.setText(team.getName());

        //Display Teams' rates
        String hourlyRate  = String.format("%.2f", rateCalculator.calculateTeamHourlyRate(team.getTeamId()));
        teamHourlyRateLbl.setText(hourlyRate);
        String dailyRate = String.format("%.2f", rateCalculator.calculateTeamDailyRate(team.getTeamId()));
        teamDailyRateLbl.setText(dailyRate);

        //Display base cost and revenue
        String baseTeamCost = String.valueOf(rateCalculator.calculateTeamCost(employeeInTeams));
        teamCostLbl.setText(baseTeamCost);
        String baseTeamRevenue = String.valueOf(rateCalculator.calculateTeamRevenueWithoutMultipliers(employeeInTeams));
        teamRevenueLbl.setText(baseTeamRevenue);

        //Display Team multipliers
        String markup = String.valueOf(team.getTeamMarkup());
        markupTextField.setText(markup);
        String gm = String.valueOf(team.getTeamGm());
        gmTextField.setText(gm);

        //Display Team Daily hours and the countries that a team has
        String dailyHours = String.valueOf(model.getTeamDailyHours(team.getTeamId()));
        teamDailyHoursLbl.setText(dailyHours);

        //Display the countries that a team has
        Set<String> countries = model.getCountriesOfEmployeesInTeam(team.getTeamId());
        teamCountriesLbl.setText(countries.toString());
    }

    private void resetFields() {
        markupTextField.setText("");
        gmTextField.setText("");
        teamDayRateWithMultLbl.setText("");
        teamHourRateWithMultLbl.setText("");
        teamCostWithMultiLbl.setText("");
        teamRevenueWithMultiLbl.setText("");
    }

    @FXML
    private void toggleFilter(ActionEvent actionEvent) throws SQLException {
        if (isFilterActive){
            clearFilter();
        } else {
            applyFilter();
        }
    }

    private void applyFilter() throws SQLException {
        String searchQuery = searchTextField.getText().toLowerCase();
        ObservableList<Employee> filteredEmployees = FXCollections.observableArrayList();

        for ( Employee employee : model.getEmployeesFromDB()){
            if (employee.getName().toLowerCase().contains(searchQuery)){
                filteredEmployees.add(employee);
            }
        }

        employeeTableView.setItems(filteredEmployees);
        searchBtn.setText("Clear");
        isFilterActive = true;
    }

    private void clearFilter() throws SQLException {
        employeeTableView.setItems(FXCollections.observableArrayList(model.getEmployeesFromDB()));
        searchTextField.clear();
        searchBtn.setText("Search");
        isFilterActive = false;
    }


    private FXMLLoader loadFXML(String path) {
        return new FXMLLoader(getClass().getResource(path));
    }

    private Stage createStage(String title, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        return stage;
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        loadAlertStyle(alert);
        alert.showAndWait();
    }

    private void loadAlertStyle(Alert alert) {
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }


    ///////////////////////
    //Employee Management//
    ///////////////////////

    private void openAddOrEditEmployee(String title, Employee employee) {
        FXMLLoader loader = loadFXML("/GUI/View/AddEmployeeView.fxml");
        try {
            Parent root = loader.load();
            AddEmployeeController addEmployeeController = loader.getController();
            addEmployeeController.setEmployeeWindowController(this);
            addEmployeeController.setEmployee(employee);
            Stage stage = createStage(title, root);
            stage.setMaximized(true);
            stage.show();
            if (title.equals("Edit Employee")) {
                addEmployeeController.addEmployeeBtn.setText("Edit Employee");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Employee getSelectedEmployee(){
        return employeeTableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void openAddEmployee(ActionEvent actionEvent) {
        openAddOrEditEmployee("Add Employee", new Employee());
    }

    @FXML
    private void openEditEmployee(ActionEvent actionEvent) {
        Employee employee = getSelectedEmployee();
        if (employee != null) {
            openAddOrEditEmployee("Edit Employee", employee);
        } else {
            showAlert("No Employee Selected", "Please select an employee to edit.");
        }
    }

    protected void updateEmployeeProperties(int id, String name, String annSalary, String multPer, String fixedAnnAmt, String country, String workHours, String utilization, Boolean isOverHeadCost, String dailyHours) throws SQLException {
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
                existingEmployee.setDailyHours(Integer.parseInt(dailyHours));
                //update employee on database
                model.updateEmployeeInDB(existingEmployee);
                //refresh related things
                refreshEmployeeTable(existingEmployee);
                refreshTeamsTableView();
                employeeExists = true;
                break;
            }
        }

        //create employee
        if (!employeeExists) {
            Employee newEmployee = new Employee(name, annSalary, multPer, fixedAnnAmt, country, workHours, utilization, isOverHeadCost, dailyHours);
            //add to database
            int employeeID = model.createEmployeeInDB(newEmployee);
            newEmployee.setId(employeeID);
            employeeTableView.getItems().add(newEmployee);
        }
    }

    private void refreshEmployeeTable(Employee updatedEmployee) {
        ObservableList<Employee> items = employeeTableView.getItems();
        int index = items.indexOf(updatedEmployee);
        if (index >= 0) {
            items.set(index, updatedEmployee);
        }
    }

    @FXML
    private void addToTeamBtn(ActionEvent actionEvent) {
        Employee selectedEmployee = getSelectedEmployee();

        if (selectedEmployee != null) {
            displayTeamSelectionDialog(selectedEmployee);
        } else {
            showAlert("Employee not selected", "Please select an employee to add to a team");
        }
    }

    @FXML
    private void removeEmployeeFromTeamBtn(ActionEvent actionEvent) {
        Employee selectedEmployee = getSelectedEmployee();
        if (selectedEmployee != null) {
            displayRemoveTeamSelectionDialog(selectedEmployee);
        } else {
            showAlert("Employee not selected", "Please select an employee to remove from a team");
        }
    }

    private void displayTeamSelectionDialog(Employee employee) {
        ChoiceDialog<Team> dialog = new ChoiceDialog<>();
        dialog.setTitle("Select Team");
        dialog.setHeaderText("Select a team to add " + employee.getName() + " to:");

        try {
            List<Team> teams = model.getTeamsFromDB();
            dialog.getItems().addAll(teams);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.showAndWait().ifPresent(selectedTeam -> {
            try {
                // Check if the employee is already part of the selected team and show alert if thats the case
                List<String> currentTeams = model.getTeamsFromDBUsingEmployee(employee.getId());
                if (currentTeams.contains(selectedTeam.getName())) {
                    showAlert("Employee Already in Team", employee.getName() + " is already a member of the selected team.");
                } else {
                    model.addEmployeeToTeamInDB(employee.getId(), selectedTeam.getTeamId(), 0, 0);
                    refreshTeamsTableView();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void displayRemoveTeamSelectionDialog(Employee employee) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Select Team");
        dialog.setHeaderText("Select a team to remove " + employee.getName() + " from:");

        Map<String, Integer> teamMap = new HashMap<>();

        try {
            List<Team> allTeams = model.getTeamsFromDB();
            for (Team team : allTeams) {
                teamMap.put(team.getName(), team.getTeamId());
            }
            List<String> teamNames = model.getTeamsFromDBUsingEmployee(employee.getId());
            dialog.getItems().addAll(teamNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.showAndWait().ifPresent(selectedTeamName -> {
            try {
                Integer teamId = teamMap.get(selectedTeamName);
                if (teamId != null) {
                    model.deleteEmployeeFromTeamInDB(employee.getId(), teamId);
                    refreshTeamsTableView();
                } else {
                    showAlert("Team Not Found", "Selected team not found in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void deleteEmployee(ActionEvent actionEvent) {
        Employee employee = getSelectedEmployee();
        if (employee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this employee?");
            alert.setContentText("This action cannot be undone.");
            loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int selectedIndex = employeeTableView.getSelectionModel().getSelectedIndex();
                    try {
                        model.deleteEmployeeFromDB(employee.getId());
                        refreshTeamsTableView();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    employeeTableView.getItems().remove(selectedIndex);
                }
            });
        } else {
          showAlert("No Employee Selected", "Please select an employee to delete.");
        }
    }


    ///////////////////
    //Team Management//
    ///////////////////

    private void openAddOrEditTeam(String title, Team team) {
        FXMLLoader loader = loadFXML("/GUI/View/AddTeamView.fxml");
        try {
            Parent root = loader.load();
            AddTeamController addTeamController = loader.getController();
            addTeamController.setEmployeeWindowController(this);
            addTeamController.setTeam(team);
            Stage stage = createStage(title, root);
            stage.setMaximized(true);
            stage.show();
            if (title.equals("Edit Team")) {
                addTeamController.addTeamBtn.setText("Edit Team");
            } else {
                addTeamController.employeeTableView.setVisible(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Team getSelectedTeam(){
        return teamsTableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void openAddTeam(ActionEvent actionEvent) {
        openAddOrEditTeam("Add Team", new Team());
    }

    @FXML
    private void openEditTeam(ActionEvent actionEvent) {
        Team team = getSelectedTeam();
        if (team != null) {
            openAddOrEditTeam("Edit Team", team);
        } else {
            showAlert("No Team Selected", "Please select a Team to edit.");
        }
    }

    protected void updateTeamProperties(int teamId, String name, List<EmployeeInTeam> employeesInTeam) throws SQLException {
        boolean teamExists = false;
        Team existingTeam = null;
        // Edit existing team
        for (Team team : teamsTableView.getItems()) {
            if (team.getTeamId() == teamId) {
                existingTeam = team;
                existingTeam.setName(name);
                // Update team on database
                model.updateTeamInDB(existingTeam);
                teamExists = true;
                break;
            }
        }

        if (!teamExists) {
            Team newTeam = new Team(teamId, name);
            // Add new team to database
            int teamID = model.createTeamInDB(newTeam);
            newTeam.setTeamId(teamID);
            teamsTableView.getItems().add(newTeam);
        }

        // Update employees in team in the database
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            model.updateEmployeeInTeam(existingTeam.getTeamId(), employeeInTeam);
        }
        refreshTeamsTableView();
    }

    @FXML
    private void deleteTeam(ActionEvent actionEvent) {
        Team team = getSelectedTeam();
        if (team != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this team?");
            alert.setContentText("This action cannot be undone.");
            loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        model.deleteTeamFromDB(team.getTeamId());
                        refreshTeamsTableView();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        } else {
           showAlert("No Team Selected", "Please select an Team to delete.");
        }
    }



    private void refreshTeamsTableView() throws SQLException {
        teamsTableView.getItems().clear();
        List<Team> allTeams = model.getTeamsFromDB();
        teamsTableView.getItems().addAll(allTeams);
    }



    @FXML
    private void calculateCostAndRevenue(ActionEvent actionEvent) {
        Team selectedTeam = getSelectedTeam();

        if (selectedTeam != null) {
            try {
                // If fields are empty, make the values 0
                double markupPercentage = markupTextField.getText().isEmpty() ? 0 : Double.parseDouble(markupTextField.getText());
                double grossMarginPercentage = gmTextField.getText().isEmpty() ? 0 : Double.parseDouble(gmTextField.getText());

                if (markupPercentage < 0 || markupPercentage > 100 || grossMarginPercentage < 0 || grossMarginPercentage > 100) {
                    throw new IllegalArgumentException("Markup and Gross Margin percentages must be between 0 and 100.");
                }

                //update multipliers in database
                for (Team team : teamsTableView.getItems()) {
                    if (team.getTeamId() == selectedTeam.getTeamId()) {
                        selectedTeam = team;
                        selectedTeam.setTeamMarkup(markupPercentage);
                        selectedTeam.setTeamGm(grossMarginPercentage);
                        model.updateTeamMultipliersInDB(selectedTeam);
                        break;
                    }
                }

                // Calculate cost and revenue with the multipliers
                double teamCostWithMarkup = rateCalculator.calculateTeamCostWithMarkup(model.getEmployeesInTeamFromDB(selectedTeam.getTeamId()), markupPercentage);
                double revenueWithMarkup = rateCalculator.calculateTeamRevenueWithGM(model.getEmployeesInTeamFromDB(selectedTeam.getTeamId()), grossMarginPercentage);


                // Calculate hourly and daily rates with multipliers applied
                double hourlyRateWithMultipliers = rateCalculator.calculateTeamHourlyRateWithMultipliers(selectedTeam.getTeamId(), markupPercentage, grossMarginPercentage);
                double dailyRateWithMultipliers = rateCalculator.calculateTeamDailyRateWithMultipliers(selectedTeam.getTeamId(), markupPercentage, grossMarginPercentage);

                // Update UI labels with calculated values
                String hourlyRateStr = String.format("%.2f", hourlyRateWithMultipliers);
                teamHourRateWithMultLbl.setText(hourlyRateStr);

                String dailyRateStr = String.format("%.2f", dailyRateWithMultipliers);
                teamDayRateWithMultLbl.setText(dailyRateStr);

                String costWithMarkupStr = String.format("%.2f", teamCostWithMarkup);
                teamCostWithMultiLbl.setText(costWithMarkupStr);

                String revenueWithMarkupStr = String.format("%.2f", revenueWithMarkup);
                teamRevenueWithMultiLbl.setText(revenueWithMarkupStr);



            } catch (NumberFormatException e) {
                showAlert("Invalid numbers", "Please enter a number for Markup and GM multipliers");
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                showAlert("Invalid numbers", "Please enter a number between 0 and 100");
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert("No employee selected", "Please select an employee to make calculations.");
        }
    }
    

}


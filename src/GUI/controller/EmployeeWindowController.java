package GUI.controller;

import BE.CountryInfo;
import BE.Employee;
import BE.Team;
import GUI.model.Model;
import GUI.model.RateCalculator;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class EmployeeWindowController {
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableView<Team> teamsTableView;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, String> teamEmployeesColumn;
    @FXML
    private TableColumn<Team, Double> teamDailyRateColumn;
    @FXML
    private TableView<CountryInfo> countriesTableView;
    @FXML
    private TableColumn<CountryInfo, String> countryNameColumn;
    @FXML
    private TableColumn<CountryInfo, String> countryEmployeesColumn;
    @FXML
    private TableColumn<CountryInfo, Double> countryDailyRateColumn;
    @FXML
    private Label employeeNameLbl, employeeCountryLbl, employeeAnnSalLbl, employeOverMultLbl, employeeFixAmtLbl, employeeTeamLbl,
            employeeEffectHoursLbl, employeeUtilizationLbl, employeeBooleanLbl, hourRateLbl, dailyRateLbl, employeeDailyHourLbl,
            employeeCostLbl, employeeRevenueLbl;
    @FXML
    private TextField searchTextField, markupTextField, gmTextField;
    @FXML
    private Button searchBtn;

    private ObservableList<CountryInfo> countryInfoList = FXCollections.observableArrayList();

    private final Model model = new Model();

    private final RateCalculator rateCalculator = new RateCalculator();

    private Boolean isFilterActive = false;


    @FXML
    private void initialize() throws SQLException {
        setEmployeeTableView();
        setTeamsTableView();
        setDataBase();
        setTeamsDatabase();
        setEmployeeTab();
        setButtons();
        setCountriesTableView();
    }

    private void setEmployeeTableView() {
        TableColumn<Employee, String> employeeNameColumn = (TableColumn<Employee, String>) employeeTableView.getColumns().get(0);
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(EmployeeActionCell.forTableColumn(this));
        employeeTableView.getColumns().add(actionColumn);

    }

    public void displayTeamSelectionDialog(Employee employee) {
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
                    model.addEmployeeToTeamInDB(employee.getId(), selectedTeam.getTeamId());
                    refreshTeamsTableView();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void displayRemoveTeamSelectionDialog(Employee employee) {
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

    private void setTeamsTableView() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        teamEmployeesColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            try {
                List<Employee> employees = model.getEmployeesFromTeamInDB(team.getTeamId());
                StringJoiner employeeNames = new StringJoiner(", ");
                for (Employee employee : employees) {
                    employeeNames.add(employee.getName());
                }
                return new SimpleStringProperty(employeeNames.toString());
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving employees for team: " + e.getMessage(), e);
            }
        });

        teamDailyRateColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            try {
                double dailyRate = rateCalculator.calculateTeamDailyRate(team.getTeamId());
                team.setTeamDailyRate(dailyRate); 
                return new SimpleDoubleProperty(dailyRate).asObject();
            } catch (SQLException e) {
                throw new RuntimeException("Error calculating daily rate for team: " + e.getMessage(), e);
            }
        });

        TableColumn<Team, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(TeamActionCell.forTableColumn(this));
        teamsTableView.getColumns().add(actionColumn);
    }

    private void setCountriesTableView() {
        countryNameColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        countryEmployeesColumn.setCellValueFactory(new PropertyValueFactory<>("employeesInCountry"));
        countryDailyRateColumn.setCellValueFactory(new PropertyValueFactory<>("countryDailyRate"));
        loadCountryInfo();
        countriesTableView.setItems(countryInfoList);
    }

    private void loadCountryInfo() {
       try {
           List<String> countries = model.getAllCountriesFromDB();
           for (String country : countries) {
               List<Employee> employees = model.getEmployeesFromCountryInDB(country);
               StringBuilder employeesInCountry = new StringBuilder();
               double totalDailyRate = 0;
               for (Employee employee : employees) {
                   employeesInCountry.append(employee.getName()).append(", ");
                   totalDailyRate = rateCalculator.calculateTotalDayRateByCountry(country);
               }

               if (!employeesInCountry.isEmpty()){
                   employeesInCountry.deleteCharAt(employeesInCountry.length() - 2);
               }

               countryInfoList.add(new CountryInfo(country, employeesInCountry.toString(), totalDailyRate));
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }

    private void setDataBase() throws SQLException {
        try {
            for (Employee employee : model.getEmployeesFromDB()) {
                employeeTableView.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTeamsDatabase() throws SQLException {
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
                    updateLabels(newSelection); // update labels with information of the selected employee
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



    private void updateLabels(Employee employee) throws SQLException {
        resetFields();
        //set Employees Information
        employeeNameLbl.setText(employee.getName());
        employeeCountryLbl.setText("Country: " + employee.getCountry());
        employeeAnnSalLbl.setText("Annual Salary: " + employee.getAnnualSalary());
        employeOverMultLbl.setText("Overhead Multiplier Percentage: " + employee.getOverheadMultPercent());
        employeeFixAmtLbl.setText("Fixed annual amount: " + employee.getFixedAnnualAmount());
        employeeEffectHoursLbl.setText("Annual Effective Working Hours: " + employee.getAnnualWorkingHours());
        employeeDailyHourLbl.setText("Daily Working Hours: " + employee.getDailyHours());
        employeeUtilizationLbl.setText("Utilization Percentage: " + employee.getUtilizationPercentage());
        if (employee.isOverHeadCost()) {
            employeeBooleanLbl.setText("Overhead Cost");
        } else {
            employeeBooleanLbl.setText("Production Resource");
        }
        //Get the teams that the employee belongs to
        List <String> teamNames = model.getTeamsFromDBUsingEmployee(employee.getId());
        String teamNamesString = String.join(", ", teamNames);
        employeeTeamLbl.setText("Teams: " + teamNamesString);
        //Display employee's rates
        String hourlyRate = String.valueOf(rateCalculator.calculateHourlyRate(employee));
        hourRateLbl.setText("Hourly Rate: " + hourlyRate);
        String dailyRate = String.valueOf(rateCalculator.calculateDailyRate(employee));
        dailyRateLbl.setText("Daily Rate: " + dailyRate);
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


    private void openAddOrEditEmployee(String title, Employee employee) {
        FXMLLoader loader = loadFXML("/GUI/View/AddEmployeeView.fxml");
        try {
            Parent root = loader.load();
            AddEmployeeController addEmployeeController = loader.getController();
            addEmployeeController.setEmployeeWindowController(this);
            addEmployeeController.setEmployee(employee);
            Stage stage = createStage(title, root);
            stage.show();
            if (title.equals("Edit Employee")) {
                addEmployeeController.addEmployeeBtn.setText("Edit Employee");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openAddEmployee(ActionEvent actionEvent) {
        openAddOrEditEmployee("Add Employee", new Employee());
    }

    public void openEditEmployee(Employee employee) {
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
                refreshCountriesTableView();
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
            refreshCountriesTableView();
        }

    }

    public void refreshEmployeeTable(Employee updatedEmployee) {
        ObservableList<Employee> items = employeeTableView.getItems();
        int index = items.indexOf(updatedEmployee);
        if (index >= 0) {
            items.set(index, updatedEmployee);
        }
    }

    public void deleteEmployee(Employee employee) {
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
                        refreshCountriesTableView();
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


    private void openAddOrEditTeam(String title, Team team) {
        FXMLLoader loader = loadFXML("/GUI/View/AddTeamView.fxml");
        try {
            Parent root = loader.load();
            AddTeamController addTeamController = loader.getController();
            addTeamController.setEmployeeWindowController(this);
            addTeamController.setTeam(team);
            Stage stage = createStage(title, root);
            stage.show();
            if (title.equals("Edit Team")) {
                addTeamController.addTeamBtn.setText("Edit Team");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void openAddTeam(ActionEvent actionEvent) {
        openAddOrEditTeam("Add Team", new Team());
    }

    public void openEditTeam(Team team) {
        if (team != null) {
            openAddOrEditTeam("Edit Team", team);
        } else {
            showAlert("No Team Selected", "Please select a Team to edit.");
        }
    }

    public void updateTeamProperties(int teamId, String name) throws SQLException {
        boolean teamExists = false;
        Team existingTeam = null;
        //edit existing team
        for (Team team : teamsTableView.getItems()) {
            if (team.getTeamId() == teamId){
                existingTeam = team;
                existingTeam.setName(name);
                //update team on database and refresh tableview
                model.updateTeamInDB(existingTeam);
                refreshTeamsTableView();
                teamExists = true;
                break;
            }
        }

        //create a new team
        if (!teamExists){
            Team newTeam = new Team(name);
            int teamID = model.createTeamInDB(newTeam);
            newTeam.setTeamId(teamID);
            teamsTableView.getItems().add(newTeam);
            refreshTeamsTableView();

        }

    }

    public void deleteTeam(Team team) {
        if (team != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this team?");
            alert.setContentText("This action cannot be undone.");
            loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int selectedIndex = teamsTableView.getSelectionModel().getSelectedIndex();
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

    private void refreshTeamsTableView() throws SQLException {
        teamsTableView.getItems().clear();
        List<Team> allTeams = model.getTeamsFromDB();
        teamsTableView.getItems().addAll(allTeams);
    }

    private void refreshCountriesTableView() throws SQLException {
        countriesTableView.getItems().clear();
        loadCountryInfo();
        countriesTableView.setItems(countryInfoList);
    }

    public void calculateCostAndRevenue(ActionEvent actionEvent) {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            try {
                // If fields are empty, make the values 0
                double markupPercentage = markupTextField.getText().isEmpty() ? 0 : Double.parseDouble(markupTextField.getText());
                double grossMarginPercentage = gmTextField.getText().isEmpty() ? 0 : Double.parseDouble(gmTextField.getText());

                if (markupPercentage < 0 || markupPercentage > 100 || grossMarginPercentage < 0 || grossMarginPercentage > 100) {
                    throw new IllegalArgumentException("Markup and Gross Margin percentages must be between 0 and 100.");
                }

                // Calculate cost and revenue
                double cost = rateCalculator.calculateEmployeeCost(selectedEmployee);
                double revenue = rateCalculator.calculateEmployeeRevenue(selectedEmployee, markupPercentage, grossMarginPercentage);
                employeeCostLbl.setText("Cost: " + String.format("%.2f", cost));
                employeeRevenueLbl.setText("Revenue: " + String.format("%.2f", revenue));

                // Calculate hourly and daily rates with multipliers applied
                double hourlyRate = rateCalculator.calculateHourlyRate(selectedEmployee);
                double markedUpHourlyRate = rateCalculator.applyMarkup(hourlyRate, markupPercentage);
                double hourlyRateWithMargin = rateCalculator.calculateRateWithGrossMargin(markedUpHourlyRate, grossMarginPercentage);
                double dailyRate = rateCalculator.calculateDailyRate(selectedEmployee);
                double markedUpDailyRate = rateCalculator.applyMarkup(dailyRate, markupPercentage);
                double dailyRateWithMargin = rateCalculator.calculateRateWithGrossMargin(markedUpDailyRate, grossMarginPercentage);
                hourRateLbl.setText("Hourly Rate: " + String.format("%.2f", hourlyRateWithMargin));
                dailyRateLbl.setText("Daily Rate: " + String.format("%.2f", dailyRateWithMargin));

            } catch (NumberFormatException e) {
                showAlert("Invalid numbers", "Please enter a number for Markup and GM multipliers");
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                showAlert("Invalid numbers", "Please enter a number between 0 and 100");
                e.printStackTrace();
            }
        } else {
            showAlert("No employee selected", "Please select an employee to make calculations.");
        }
    }

    private void resetFields() {
        markupTextField.setText("");
        gmTextField.setText("");
        employeeCostLbl.setText("Cost: ");
        employeeRevenueLbl.setText("Revenue: ");
        hourRateLbl.setText("Hourly Rate: ");
        dailyRateLbl.setText("Daily Rate: ");
    }




}


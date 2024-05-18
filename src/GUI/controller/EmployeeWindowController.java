package GUI.controller;

import GUI.model.CountryInfo;
import BE.Employee;
import BE.Team;
import GUI.model.Model;
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
import java.util.List;
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
            employeeEffectHoursLbl, employeeUtilizationLbl, employeeBooleanLbl, hourRateLbl, dailyRateLbl;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchBtn;

    private ObservableList<CountryInfo> countryInfoList = FXCollections.observableArrayList();

    private final Model model = new Model();

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
                double dailyRate = model.calculateTeamDailyRate(team.getTeamId());
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
                   totalDailyRate = model.calculateTotalDayRateByCountry(country);
               }

               if (employeesInCountry.length() > 0){
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


    public void addToTeamBtn(ActionEvent actionEvent) throws SQLException {
        addSelectedEmployeeToTeam();
    }

    private void addSelectedEmployeeToTeam() throws SQLException {
        Integer employeeId = employeeTableView.getSelectionModel().getSelectedItem().getId();
        Integer teamId = teamsTableView.getSelectionModel().getSelectedItem().getTeamId();

        if (teamId != null && employeeId != null) {
            model.addEmployeeToTeamInDB(employeeId, teamId);
            refreshTeamsTableView();
        }
    }

    public void removeEmployeeFromTeamBtn(ActionEvent actionEvent) throws SQLException {
        removeSelectedEmployeeFromTeam();
    }

    private void removeSelectedEmployeeFromTeam() throws SQLException {
        Integer employeeId = employeeTableView.getSelectionModel().getSelectedItem().getId();
        Team selectedTeam = teamsTableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            int teamId = selectedTeam.getTeamId();
            try {
                model.deleteEmployeeFromTeamInDB(employeeId, teamId);
                refreshTeamsTableView();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Team Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a team to delete the employee from.");
            loadAlertStyle(alert);
            alert.showAndWait();
        }
    }


    private void updateLabels(Employee employee) throws SQLException {
        //set Employees Information
        employeeNameLbl.setText(employee.getName());
        employeeCountryLbl.setText("Country: " + employee.getCountry());
        employeeAnnSalLbl.setText("Annual Salary: " + employee.getAnnualSalary());
        employeOverMultLbl.setText("Overhead Multiplier Percentage: " + employee.getOverheadMultPercent());
        employeeFixAmtLbl.setText("Fixed annual amount: " + employee.getFixedAnnualAmount());
        //Get the teams that the employee belongs to
        List <String> teamNames = model.GetTeamsFromDBUsingEmployee(employee.getId());
        String teamNamesString = String.join(", ", teamNames);
        employeeTeamLbl.setText("Teams: " + teamNamesString);

        employeeEffectHoursLbl.setText("Annual Effective Working Hours: " + employee.getAnnualWorkingHours());
        employeeUtilizationLbl.setText("Utilization Percentage: " + employee.getUtilizationPercentage());
        if (employee.isOverHeadCost()) {
            employeeBooleanLbl.setText("Overhead Cost");
        } else {
            employeeBooleanLbl.setText("Production Resource");
        }
        //Display employee's rates
        String hourlyRate = String.valueOf(employee.calculateHourlyDate());
        hourRateLbl.setText("Hourly Rate: " + hourlyRate);
        String dailyRate = String.valueOf(employee.calculateDailyRate());
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
            Employee newEmployee = new Employee(name, annSalary, multPer, fixedAnnAmt, country, workHours, utilization, isOverHeadCost);
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


    private Team getSelectedTeam() {
        return teamsTableView.getSelectionModel().getSelectedItem();
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

}


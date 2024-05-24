package GUI.controller;

import BE.EmployeeInTeam;
import BE.Team;
import GUI.model.Model;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddTeamController {
    @FXML
    private TableView<EmployeeInTeam> employeeTableView;
    @FXML
    private TableColumn<EmployeeInTeam, String> employeeNameColumn;
    @FXML
    private TableColumn<EmployeeInTeam, Double> employeeHoursColumn;
    @FXML
    private TableColumn<EmployeeInTeam, Double> employeeCostColumn;
    @FXML
    private TextField teamNameField;
    public Button addTeamBtn;
    private EmployeeWindowController employeeWindowController;
    private Team team;

    private final Model model = new Model();

    @FXML
    private void initialize() {
        employeeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployee().getName()));
        employeeHoursColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHours()).asObject());
        employeeCostColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCostPercentage()).asObject());

        employeeTableView.setEditable(true);

        employeeHoursColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        employeeCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));

        employeeHoursColumn.setOnEditCommit(event -> {
            EmployeeInTeam employeeInTeam = event.getRowValue();
            double newHours = event.getNewValue();
            double totalAssignedHours = 0;
            try {
                totalAssignedHours = model.getTotalHoursForEmployee(employeeInTeam.getEmployee().getId()) - employeeInTeam.getHours() + newHours;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (totalAssignedHours <= employeeInTeam.getEmployee().getDailyHours()) {
                employeeInTeam.setHours(newHours);
            } else {
                showAlert("Invalid Hours", "Total assigned hours for this employee exceed the daily hours limit");
                employeeTableView.refresh();
            }
        });

        employeeCostColumn.setOnEditCommit(event -> {
            EmployeeInTeam employeeInTeam = event.getRowValue();
            double totalCostPercentage = 0;
            try {
                totalCostPercentage = model.getTotalCostPercentageForEmployee(employeeInTeam.getEmployee().getId()) - employeeInTeam.getCostPercentage() + event.getNewValue();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (totalCostPercentage <= 100) {
                employeeInTeam.setCostPercentage(event.getNewValue());
            } else {
                showAlert("Invalid Cost Percentage", "Total cost percentage for this employee exceeds 100%");
                employeeTableView.refresh();
            }
        });
    }

    @FXML
    private void addTeam(ActionEvent actionEvent) throws SQLException {
        String name = teamNameField.getText();
        int teamId = team.getTeamId();

        List<EmployeeInTeam> employeesInTeam = new ArrayList<>(employeeTableView.getItems());

        employeeWindowController.updateTeamProperties(teamId, name, employeesInTeam);
        ((Stage) teamNameField.getScene().getWindow()).close();

    }

    public void setTeam(Team team) {
        this.team = team;
        teamNameField.setText(team.getName());
        try {
            loadEmployeesInTeam();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void loadEmployeesInTeam() throws SQLException {
        List<EmployeeInTeam> employeesInTeam = model.getEmployeesInTeamFromDB(team.getTeamId());
        ObservableList<EmployeeInTeam> observableEmployeesInTeam = FXCollections.observableArrayList(employeesInTeam);
        employeeTableView.setItems(observableEmployeesInTeam);
    }

    public void setEmployeeWindowController(EmployeeWindowController employeeWindowController) {
        this.employeeWindowController = employeeWindowController;
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}

package GUI.controller;

import BE.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddTeamController {
    @FXML
    private TextField teamNameField;
    public Button addTeamBtn;
    private EmployeeWindowController employeeWindowController;
    private Team team;

    @FXML
    private void addTeam(ActionEvent actionEvent) throws SQLException {
        String name = teamNameField.getText();
        int teamId = team.getTeamId();

        employeeWindowController.updateTeamProperties( teamId, name);
        ((Stage) teamNameField.getScene().getWindow()).close();

    }

    public void setTeam(Team team) {
        this.team = team;
        teamNameField.setText(team.getName());
    }

    public void setEmployeeWindowController(EmployeeWindowController employeeWindowController) {
        this.employeeWindowController = employeeWindowController;

    }
}

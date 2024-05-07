package GUI.controller;

import BE.Team;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddTeamController {
    public TextField teamNameField;
    public Button addTeamBtn;
    private TeamWindowControler teamWindowControler;
    private Team team;

    public void addTeam(ActionEvent actionEvent) throws SQLException {
        String name = teamNameField.getText();
        int teamId = team.getTeamId();

        teamWindowControler.updateTeamProperties( teamId, name);
        ((Stage) teamNameField.getScene().getWindow()).close();

    }

    public void setTeam(Team team) {
        this.team = team;
        teamNameField.setText(team.getName());
    }

    public void setTeamWindowController(TeamWindowControler teamWindowControler) {
        this.teamWindowControler = teamWindowControler;

    }
}

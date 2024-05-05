package GUI.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddTeamController {
    public TextField teamNameField;
    private TeamWindowControler teamWindowControler;

    public void addTeam(ActionEvent actionEvent) throws SQLException {
        String name = teamNameField.getText();

        teamWindowControler.updateTeamProperties(name);
        ((Stage) teamNameField.getScene().getWindow()).close();

    }

    public void setTeamWindowController(TeamWindowControler teamWindowControler) {
        this.teamWindowControler = teamWindowControler;

    }
}

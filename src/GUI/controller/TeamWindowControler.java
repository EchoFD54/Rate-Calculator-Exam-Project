package GUI.controller;

import BE.Employee;
import BE.Team;
import BLL.TeamManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class TeamWindowControler {
    public TableView<Team> teamsTableVIew;
    private TeamManager teamManager = new TeamManager();


    @FXML
    private void initialize(){
        TableColumn<Team, String> teamNameColumn = (TableColumn<Team, String>) teamsTableVIew.getColumns().get(0);
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        try {
            for (Team team: teamManager.getAllTeams()){
                teamsTableVIew.getItems().add(team);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void openAddTeam(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddTeamVIew.fxml"));
        Parent root;
        try {
            root = loader.load();
            AddTeamController addTeamController = loader.getController();
            addTeamController.setTeamWindowController(this);
            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTeamProperties(String name) throws SQLException {
        boolean teamExists = false;
        Team existingTeam = null;
        for (Team team : teamsTableVIew.getItems()) {
            if (team.getName().equals(name)){
                existingTeam = team;
                existingTeam.setName(name);
                teamExists = true;
                break;
            }
        }

        if (!teamExists){
            Team newTeam = new Team(name);
            int teamID = teamManager.createTeam(newTeam);
            newTeam.setTeamId(teamID);
            teamsTableVIew.getItems().add(newTeam);

        }

    }

    public void openEditTeam(ActionEvent actionEvent) {
    }

    public void deleteTeam(ActionEvent actionEvent) {

    }
}

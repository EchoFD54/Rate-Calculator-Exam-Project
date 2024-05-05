package GUI.controller;

import BE.Employee;
import BE.Team;
import BLL.TeamManager;
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
                teamManager.updateTeam(existingTeam);
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
        Team selectedTeam = (Team) teamsTableVIew.getSelectionModel().getSelectedItem();
        if (selectedTeam != null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/View/AddTeamView.fxml"));
            Parent root;
            try {
                root = loader.load();
                AddTeamController addTeamController = loader.getController();
                addTeamController.setTeamWindowController(this);
                addTeamController.teamNameField.setText(selectedTeam.getName());
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
        Team selectedTeam = (Team) teamsTableVIew.getSelectionModel().getSelectedItem();
        if (selectedTeam != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this team?");
            alert.setContentText("This action cannot be undone.");
            loadAlertStyle(alert);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int selectedIndex = teamsTableVIew.getSelectionModel().getSelectedIndex();
                    try {
                        teamManager.deleteTeam(selectedTeam.getTeamId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    teamsTableVIew.getItems().remove(selectedIndex);
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

    private void loadAlertStyle(Alert alert){
        String alertStylesheet = "GUI/view/Styles/alert.css";
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(alertStylesheet);
    }
}

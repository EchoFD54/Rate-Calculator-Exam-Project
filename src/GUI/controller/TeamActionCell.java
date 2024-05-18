package GUI.controller;

import BE.Employee;
import BE.Team;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TeamActionCell extends TableCell<Team, Void> {
    private final HBox actionButtons;

    private final Button editButton;
    private final Button deleteButton;
    private final EmployeeWindowController controller;


    public TeamActionCell(EmployeeWindowController controller) {
        this.controller = controller;
        this.editButton = new Button("Edit");
        this.deleteButton = new Button("Delete");
        this.actionButtons = new HBox(10, editButton, deleteButton);


        editButton.setOnAction(event -> {
            Team currentTeam = getTableView().getItems().get(getIndex());
            controller.openEditTeam(currentTeam);
        });

        deleteButton.setOnAction(event -> {
            Team currentTeam = getTableView().getItems().get(getIndex());
            controller.deleteTeam(currentTeam);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButtons);
        }
    }

    public static Callback<TableColumn<Team, Void>, TableCell<Team, Void>> forTableColumn(EmployeeWindowController controller) {
        return param -> new TeamActionCell(controller);
    }
}
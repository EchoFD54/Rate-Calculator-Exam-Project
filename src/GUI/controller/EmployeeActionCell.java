package GUI.controller;

import BE.Employee;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class EmployeeActionCell extends TableCell<Employee, Void> {
    private final HBox actionButtons;

    private final Button editButton;
    private final Button deleteButton;
    private final Button addToTeamButton;
    private final Button removeFromTeamButton;
    private final EmployeeWindowController controller;


    public EmployeeActionCell(EmployeeWindowController controller) {
        this.controller = controller;
        this.editButton = new Button("Edit");
        this.deleteButton = new Button("Delete");
        this.addToTeamButton = new Button("Add To Team");
        this.removeFromTeamButton = new Button("Remove From Team");
        this.actionButtons = new HBox(10, editButton, deleteButton, addToTeamButton, removeFromTeamButton);


        editButton.setOnAction(event -> {
            Employee currentEmployee = getTableView().getItems().get(getIndex());
            controller.openEditEmployee(currentEmployee);
        });

        deleteButton.setOnAction(event -> {
            Employee currentEmployee = getTableView().getItems().get(getIndex());
            controller.deleteEmployee(currentEmployee);
        });

        addToTeamButton.setOnAction(event -> {
            Employee employee = getTableView().getItems().get(getIndex());
            controller.displayTeamSelectionDialog(employee);
        });

        removeFromTeamButton.setOnAction(event -> {
            Employee employee = getTableView().getItems().get(getIndex());
            controller.displayRemoveTeamSelectionDialog(employee);
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

    public static Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> forTableColumn(EmployeeWindowController controller) {
        return param -> new EmployeeActionCell(controller);
    }
}
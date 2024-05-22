package GUI.controller;

import BE.Employee;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        // Load images
        Image editImage = new Image(getClass().getResourceAsStream("/GUI/view/Images/edit_icon.png"));
        Image deleteImage = new Image(getClass().getResourceAsStream("/GUI/view/Images/delete_icon.png"));
        Image addToTeamImage = new Image(getClass().getResourceAsStream("/GUI/view/Images/addToTeam_icon.png"));
        Image removeImage = new Image(getClass().getResourceAsStream("/GUI/view/Images/removeFromTeam_icon.png"));
        ImageView editImageView = new ImageView(editImage);
        ImageView deleteImageView = new ImageView(deleteImage);
        ImageView addToTeamImageView = new ImageView(addToTeamImage);
        ImageView removeFromTeamImageView = new ImageView(removeImage);
        editImageView.setFitWidth(25);
        editImageView.setFitHeight(25);
        deleteImageView.setFitWidth(25);
        deleteImageView.setFitHeight(25);
        addToTeamImageView.setFitWidth(25);
        addToTeamImageView.setFitHeight(25);
        removeFromTeamImageView.setFitWidth(25);
        removeFromTeamImageView.setFitHeight(25);

        this.editButton = new Button();
        this.editButton.setGraphic(editImageView);

        this.deleteButton = new Button();
        this.deleteButton.setGraphic(deleteImageView);

        this.addToTeamButton = new Button();
        this.addToTeamButton.setGraphic(addToTeamImageView);

        this.removeFromTeamButton = new Button();
        this.removeFromTeamButton.setGraphic(removeFromTeamImageView);

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
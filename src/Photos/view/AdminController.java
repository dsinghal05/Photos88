package Photos.view;

import java.io.IOException;

import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Admin is a special sub-system that can list, add, and remove users.
 * It does not support photo functionality. Admin can not be deleted from the user list.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class AdminController {
    @FXML private TextField usernameField;
    @FXML private ListView<User> userListView;
    @FXML private Button LogOutButton;

    private UserList userList;

    /**
     * Lets AdminController recieve userList to be able to list users, as well as call to add/remove users.
     * @param list
     */
    public void setUserList(UserList list) {
        this.userList = list;
        refreshList();
    }


    @FXML
    public void initialize() {
        userListView.setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);

                    if (empty || user == null) {
                        setText(null);
                        setContextMenu(null); // remove menu for empty cells
                    } else {
                        setText(user.getUsername());

                        // Create context menu ONLY for valid users
                        MenuItem deleteItem = new MenuItem("Delete User");
                        deleteItem.setOnAction(e -> {
                            try {
                                userList.removeUser(user);
                                refreshList();
                            } catch (IOException | IllegalArgumentException ex) {
                                showError(ex.getLocalizedMessage());
                            }
                        });

                        ContextMenu menu = new ContextMenu(deleteItem);
                        setContextMenu(menu); // attach only here
                    }
                }
            };

            return cell;
        });    
    }

    /**
     * Updates the cell view after changes have been made to the user list.
     */
    private void refreshList() {
        if (userList != null) {
            userListView.getItems().setAll(userList.getUsers());
        }
    }

    @FXML
    public void handleAddUser() throws IOException {
        String username = usernameField.getText().strip();

        try {
            userList.addUser(username);
        }
        catch (IllegalArgumentException e) {
            showError(e.getLocalizedMessage());
        }
        usernameField.clear();
        refreshList();
    }

    @FXML
    public void returnToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/login.fxml"));
            Parent root = loader.load();

            //Get controller and pass data
            LoginController controller = loader.getController();
            controller.setUserList(userList);

            //Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            //Switch scene
            stage.setScene(new Scene(root, 400, 300)); 
            stage.setTitle("Photos");
            stage.show();

        } catch (Exception e) {
            showError(e.getLocalizedMessage());
        }
        return;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
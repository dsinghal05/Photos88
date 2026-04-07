package Photos.view;

import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminController {
    @FXML private TextField usernameField;
    @FXML private ListView<User> userListView;
    @FXML private Button LogOutButton;

    private UserList userList;

    public void setUserList(UserList list) {
        this.userList = list;
    }


    @FXML
    public void initialize() {
        userListView.setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    setText(empty || user == null ? null : user.getUsername());
                }
            };

            // Context menu (right-click)
            MenuItem deleteItem = new MenuItem("Delete User");
            deleteItem.setOnAction(e -> {
                User selected = cell.getItem();
                if (selected != null && !selected.getUsername().equals("admin")) {
                    userList.removeUser(selected.getUsername());
                    refreshList();
                    try {
                        UserList.write(userList);  
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            ContextMenu menu = new ContextMenu(deleteItem);
            cell.setContextMenu(menu);

            return cell;
        });
    }
    private void refreshList() {
        userListView.getItems().setAll(userList.getUsers());
    }

    @FXML
    public void handleAddUser() {
        String username = usernameField.getText();

        if (username == null || username.isEmpty()) {
            //return error code
            return;
        }

        userList.addUser(username);
        usernameField.clear();
        refreshList();

        try {
            UserList.write(userList);  
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            stage.setScene(new Scene(root, 400, 300)); // 👈 add size
            stage.setTitle("Photos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
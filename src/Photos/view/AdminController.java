package Photos.view;

import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class AdminController {
    @FXML private TextField usernameField;
    @FXML private ListView<User> userListView;

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

        if (username == null || username.isEmpty()) return;

        userList.addUser(username);
        usernameField.clear();
        refreshList();

        try {
            UserList.write(userList);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
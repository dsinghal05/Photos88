package Photos.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

/**This is the first window of the app. Entering "admin" will take you to a special admin view. 
Entering a different valid username will take you to the non-admin subsystem.
Will display error windows when you enter an empty or invalid username. */
public class LoginController {
    @FXML TextField usernameField;
    
    private UserList userList;

    /**
     * Lets LoginController recieve userList so it can verify an entered username is valid and exists on the user list.
     * @param list
     */
    public void setUserList(UserList list) {
        this.userList = list;
    }

    public void usernameEntered() {
        String username = usernameField.getText().strip(); //removes all whitespace

        if (username.equals("")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Username");
            alert.setContentText("Please enter a username.");

            alert.showAndWait();
            usernameField.clear();
            return;
        }
        if (username.equals("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/admin.fxml"));
                Parent root = loader.load();

                //Get controller and pass data
                AdminController controller = loader.getController();
                controller.setUserList(userList);

                //Get current stage
                Stage stage = (Stage) usernameField.getScene().getWindow();

                //Switch scene
                stage.setScene(new Scene(root, 600, 500)); // 👈 add size
                stage.setTitle("Admin Panel");
                stage.show();

            } catch (Exception e) {
                showError(e.getLocalizedMessage());
            }
            return;
        }

        User findUser = null;
        for (User u : userList.getUsers()) {
            if (username.equals(u.getUsername())) {
                findUser = u;
                break;
            }
        }
        
        if (findUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/main.fxml"));
                Parent root = loader.load();

                // 🔑 pass the user
                MainController controller = loader.getController();
                controller.setUser(findUser, userList);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("User View");
                stage.sizeToScene();
                stage.show();

            } catch (Exception e) {
                showError(e.getLocalizedMessage());
            }
        }

        else {
            showError("Username does not exist.");
        }
        
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

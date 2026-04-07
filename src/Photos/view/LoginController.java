package Photos.view;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class LoginController {
    @FXML TextField usernameField;
    
    private UserList userList;

    public void setUserList(UserList list) {
        this.userList = list;
    }

    public void usernameEntered() {
        String username = usernameField.getText();

        if (username.equals("")) {
            //Invalid username
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
                e.printStackTrace();
            }
            return;
        }
        for (User u : userList.getUsers()) {
            if (username.equals(u.getUsername())) {
                //if not admin but confirmed to be an existing User, go to main.fxml 
                break;
            }
        }
        
        //else user not found
        
    }
}

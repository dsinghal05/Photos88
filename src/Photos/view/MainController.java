package Photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import Photos.model.User;
import Photos.model.UserList;

/**Represents non-admin subsytem. Opens with displaying all the User's albums. */
public class MainController {
    private UserList userList;
    @FXML private Label usernameLabel;

    public void setUserList(UserList list) {
        this.userList = list;
    }

    public void setUser(User user) {
        usernameLabel.setText("Logged in as: " + user.getUsername());
    }

    //helper program to call after modifying any data (albums, photos, tags, captions, etc.)
    private void save() {
        try {
            UserList.write(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
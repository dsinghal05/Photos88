package Photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import Photos.model.User;

/**Represents non-admin subsytem. Opens with displaying all the User's albums. */
public class MainController {

    @FXML private Label usernameLabel;

    public void setUser(User user) {
        usernameLabel.setText("Logged in as: " + user.getUsername());
    }
}
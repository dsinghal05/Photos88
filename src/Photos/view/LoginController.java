package Photos.view;


import javafx.scene.control.TextField;
import javafx.fxml.FXML;
public class LoginController {
    @FXML TextField usernameField;
    
    public void usernameEntered() {
        String username = usernameField.getText();
        //if username is admin: open a specific admin.fxml file that has the capability of listing, creating, deleting users.
        //if not admin but confirmed to be an existing User, go to main.fxml 
    }
}

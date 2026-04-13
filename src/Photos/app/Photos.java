package Photos.app;

import Photos.model.UserList;
import Photos.view.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Photos application.
 * Loads the user list from disk, displays the login screen,
 * and ensures all data is saved when the window is closed.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class Photos extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load user list once at startup
            UserList userList = UserList.readList();

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            // Pass user list to controller
            LoginController controller = loader.getController();
            controller.setUserList(userList);

            // Safe quit: save all data when the window is closed
            primaryStage.setOnCloseRequest(event -> {
                try {
                    UserList.write(userList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            primaryStage.setTitle("Photos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
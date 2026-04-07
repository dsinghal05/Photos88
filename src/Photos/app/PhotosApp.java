package Photos.app;

import Photos.model.UserList;
import Photos.view.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhotosApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 🔑 Load user list once at startup
            UserList userList = UserList.readList();

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            // 🔑 Pass user list to controller
            LoginController controller = loader.getController();
            controller.setUserList(userList);

            primaryStage.setTitle("Photos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
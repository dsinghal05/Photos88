package Photos.view;

import Photos.model.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Calendar;

/**
 * Controller for the album view. Displays photo thumbnails in a grid,
 * supports adding/removing photos, editing captions and tags,
 * and opening the full photo display view.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class AlbumController {
    @FXML private Label albumTitleLabel;
    @FXML private Label albumInfoLabel;
    @FXML private TilePane photoTilePane;

    private User currentUser;
    private UserList userList;
    private Album currentAlbum;

    // 🔑 Called when switching scenes
    public void setData(User user, UserList list, Album album) {
        this.currentUser = user;
        this.userList = list;
        this.currentAlbum = album;

        refresh();
    }

    // 🔄 Refresh everything
    private void refresh() {
        albumTitleLabel.setText(currentAlbum.getTitle());
        updateAlbumInfo();
        loadPhotos();
    }

    // 📊 Album info (count + date range)
    private void updateAlbumInfo() {
        int count = currentAlbum.getPhotos().size();

        if (count == 0) {
            albumInfoLabel.setText("No photos");
            return;
        }

        Calendar earliest = null;
        Calendar latest = null;

        for (Photo p : currentAlbum.getPhotos()) {
            Calendar d = p.getDateModified();

            if (earliest == null || d.before(earliest)) earliest = d;
            if (latest == null || d.after(latest)) latest = d;
        }

        albumInfoLabel.setText(
            count + " photos | " +
            earliest.getTime() + " - " +
            latest.getTime()
        );
    }

    // 🖼 Load thumbnails
    private void loadPhotos() {
        photoTilePane.getChildren().clear();

        for (Photo p : currentAlbum.getPhotos()) {
            VBox box = createPhotoTile(p);
            photoTilePane.getChildren().add(box);
        }
    }

    // 🧱 Create one photo tile
    private VBox createPhotoTile(Photo p) {
        ImageView imageView = new ImageView(p.getImage());
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label caption = new Label(p.getCaption());

        VBox box = new VBox(5, imageView, caption);

        // 🖱 Double click → open full view
        box.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openPhoto(p);
            }
        });

        // 🖱 Right-click menu
        ContextMenu menu = new ContextMenu();

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            currentAlbum.removePhoto(p);
            save();
            refresh();
        });

        MenuItem captionItem = new MenuItem("Edit Caption");
        captionItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(p.getCaption());
            dialog.setHeaderText("Edit Caption");

            dialog.showAndWait().ifPresent(text -> {
                p.setCaption(text);
                save();
                refresh();
            });
        });

        
        MenuItem tags = new MenuItem("Edit Tags");
        tags.setOnAction(e -> openTagEditor(p));

        MenuItem copy = new MenuItem("Copy to Album");
        copy.setOnAction(e -> {
            Album target = chooseAlbum("Copy Photo", "Select album to copy to:");
            if (target != null) {
                try {
                    target.addPhoto(p);
                    save();
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        MenuItem move = new MenuItem("Move to Album");
        move.setOnAction(e -> {
            Album target = chooseAlbum("Move Photo", "Select album to move to:");
            if (target != null) {
                try {
                    target.addPhoto(p);
                    currentAlbum.removePhoto(p);
                    save();
                    refresh();
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        menu.getItems().addAll(delete, captionItem, tags, copy, move);
        box.setOnContextMenuRequested(e -> menu.show(box, e.getScreenX(), e.getScreenY()));

        return box;
    }
    
    // Open the tag editor window for a photo
    private void openTagEditor(Photo p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/tagEditor.fxml"));
            Parent root = loader.load();
 
            TagEditorController controller = loader.getController();
            controller.setData(p, currentUser, userList);
 
            Stage stage = new Stage();
            stage.setTitle("Edit Tags");
            stage.setScene(new Scene(root, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
 
            // Refresh tile captions in case tags affect display later
            refresh();
 
        } catch (Exception e) {
            showError("Could not open tag editor.");
        }
    }

    // ➕ Add photo
    @FXML
    public void handleAddPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image");

        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png")
        );

        File file = chooser.showOpenDialog(photoTilePane.getScene().getWindow());

        if (file != null) {
            try {
                Photo p = currentUser.getOrCreatePhoto(file.getAbsolutePath());
                currentAlbum.addPhoto(p);

                save();
                refresh();

            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }


    // 🔙 Back to main
    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setUser(currentUser, userList);

            Stage stage = (Stage) photoTilePane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photos");
            stage.sizeToScene();

        } catch (Exception e) {
            showError(e.getLocalizedMessage());
        }
    }

    // 🔍 Open full photo view with slideshow
    private void openPhoto(Photo p) {
        try {
            int index = currentAlbum.getPhotos().indexOf(p);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/photoView.fxml"));
            Parent root = loader.load();

            PhotoViewController controller = loader.getController();
            controller.setData(currentAlbum, index);

            Stage stage = new Stage();
            stage.setTitle("Photo Viewer");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh in case caption/tags were changed elsewhere
            refresh();

        } catch (Exception e) {
            showError("Could not open photo viewer.");
        }
    }

    /**
     * Shows a dialog letting the user pick one of their other albums.
     * @param title the dialog title
     * @param header the dialog header text
     * @return the selected Album, or null if cancelled
     */
    private Album chooseAlbum(String title, String header) {
        // Build list of albums excluding the current one
        java.util.List<Album> otherAlbums = new java.util.ArrayList<>();
        for (Album a : currentUser.getAlbums()) {
            if (!a.getTitle().equals(currentAlbum.getTitle())) {
                otherAlbums.add(a);
            }
        }

        if (otherAlbums.isEmpty()) {
            showError("No other albums to choose from. Create another album first.");
            return null;
        }

        ChoiceDialog<Album> dialog = new ChoiceDialog<>(otherAlbums.get(0), otherAlbums);
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Display album titles in the dropdown
        dialog.setContentText("Album:");

        java.util.Optional<Album> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // 💾 Save
    private void save() {
        try {
            UserList.write(userList);
        } catch (Exception e) {
            showError("Save failed.");
        }
    }

    // ⚠️ Error helper
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
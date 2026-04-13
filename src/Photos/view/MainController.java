package Photos.view;

import java.io.IOException;
import java.util.ArrayList;

import Photos.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the main user view. Displays all albums belonging to the user
 * with photo count and date range. Supports creating, deleting, renaming, and
 * opening albums, as well as searching photos by date or tags.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class MainController {

    @FXML private Label userLabel;
    @FXML private ListView<Album> albumListView;
    @FXML private ListView<Photo> searchResultsListView;
    @FXML private VBox searchResultsBox;
    @FXML private TextField newAlbumNameField;

    private User currentUser;
    private UserList userList;

    // 🔑 Called from LoginController
    public void setUser(User user, UserList list) {
        this.currentUser = user;
        this.userList = list;

        userLabel.setText("Logged in as: " + user.getUsername());
        refreshList();
    }

    @FXML
    public void initialize() {

        // Display album titles
        albumListView.setCellFactory(lv -> {
            ListCell<Album> cell = new ListCell<>() {
                @Override
                protected void updateItem(Album album, boolean empty) {
                    super.updateItem(album, empty);

                    if (empty || album == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        // Show album title, photo count, and date range
                        int count = album.getPhotos().size();
                        String display = album.getTitle() + "  |  " + count + " photo" + (count != 1 ? "s" : "");

                        if (count > 0) {
                            java.util.Calendar earliest = null;
                            java.util.Calendar latest = null;
                            for (Photo p : album.getPhotos()) {
                                java.util.Calendar d = p.getDateModified();
                                if (earliest == null || d.before(earliest)) earliest = d;
                                if (latest == null || d.after(latest)) latest = d;
                            }
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                            display += "  |  " + sdf.format(earliest.getTime()) + " - " + sdf.format(latest.getTime());
                        }

                        setText(display);

                        // Right-click menu
                        MenuItem renameItem = new MenuItem("Rename");
                        renameItem.setOnAction(e -> handleRenameAlbum(album));

                        MenuItem deleteItem = new MenuItem("Delete");
                        deleteItem.setOnAction(e -> handleDeleteAlbum(album));

                        ContextMenu menu = new ContextMenu(renameItem, deleteItem);
                        setContextMenu(menu);
                    }
                }
            };
            return cell;
        });

        // Double click to open album
        albumListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Album selected = albumListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openAlbum(selected);
                }
            }
        });
    }

    // 🔄 Refresh album list
    private void refreshList() {
        if (currentUser != null) {
            albumListView.getItems().setAll(currentUser.getAlbums());
        }
    }

    @FXML
    public void handleSearchByDate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dateSearch.fxml"));
            Parent root = loader.load();

            DateSearchController controller = loader.getController();
            controller.setData(this, currentUser);

            Stage stage = new Stage();
            stage.setTitle("Search by Date");
            stage.setScene(new Scene(root, 300, 200));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks main window
            stage.showAndWait();

        } catch (IOException e) {
            showError(e.getLocalizedMessage());
        }
    }
    public void handleSearchByTags(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tagSearch.fxml"));
            Parent root = loader.load();

            TagSearchController controller = loader.getController();
            controller.setData(this, currentUser);

            Stage stage = new Stage();
            stage.setTitle("Search by Tags");
            stage.setScene(new Scene(root, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks main window
            stage.showAndWait();

        } catch (IOException e) {
            showError(e.getMessage());
        }
    }
    public void showSearchResults(ArrayList<Photo> results) {

        searchResultsListView.getItems().clear();
        searchResultsListView.getItems().addAll(results);

        searchResultsBox.setVisible(true);
        searchResultsBox.setManaged(true);
    }
    public void handleCreateAlbumFromSearch() {
        String newAlbumName = newAlbumNameField.getText();
        ArrayList<Photo> results = new ArrayList<>(searchResultsListView.getItems());
        if (results.isEmpty()) {
            showError("Album will be empty, no search results to add.");
        }

        try {
            currentUser.createAlbum(newAlbumName);
        }
        catch (IllegalArgumentException e) {
            showError(e.getLocalizedMessage());
        }

        //populate new album
        Album newAlbum = null;
        for (Album a : currentUser.getAlbums()) {
            if (a.getTitle().equals(newAlbumName)) {
                newAlbum = a;
                break;
            }
        }
        for (Photo p : results) {
            newAlbum.addPhoto(p);
        }

        save();
        refreshList();
        newAlbumNameField.clear();
 
        // Hide results panel
        searchResultsBox.setVisible(false);
        searchResultsBox.setManaged(false);
    }
    // ➕ Add album
    @FXML
    public void handleAddAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Create Album");
        dialog.setContentText("Enter album name:");

        dialog.showAndWait().ifPresent(name -> {
            try {
                currentUser.createAlbum(name);
                save();
                refreshList();
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        });
    }

    // ✏️ Rename album
    private void handleRenameAlbum(Album album) {
        TextInputDialog dialog = new TextInputDialog(album.getTitle());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Rename Album");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(name -> {
            album.setTitle(name);
            save();
            refreshList();
        });
    }

    // ❌ Delete album
    private void handleDeleteAlbum(Album album) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Album");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete album: " + album.getTitle());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                currentUser.getAlbums().remove(album);
                save();
                refreshList();
            }
        });
    }

    // 📂 Open album (next screen)
    private void openAlbum(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/album.fxml"));
            Parent root = loader.load();

            AlbumController controller = loader.getController();
            controller.setData(currentUser, userList, album);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Album: " + album.getTitle());
            stage.sizeToScene();

        } catch (Exception e) {
            showError(e.getLocalizedMessage());
        }
    }

    // 🔙 Logout
    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Photos/view/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setUserList(userList);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Photos");

        } catch (Exception e) {
            showError(e.getLocalizedMessage());
        }
    }

    // 💾 Save system
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
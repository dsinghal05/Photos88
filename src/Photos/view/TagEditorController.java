package Photos.view;

import Photos.model.Photo;
import Photos.model.Tag;
import Photos.model.User;
import Photos.model.UserList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Map;

public class TagEditorController {

    @FXML private Label photoLabel;
    @FXML private ListView<Tag> tagListView;
    @FXML private ComboBox<String> tagTypeCombo;
    @FXML private TextField tagValueField;
    @FXML private TextField newTagTypeField;
    @FXML private CheckBox singleValueCheck;

    private Photo currentPhoto;
    private User currentUser;
    private UserList userList;

    /**
     * Called by AlbumController when opening this window.
     */
    public void setData(Photo photo, User user, UserList list) {
        this.currentPhoto = photo;
        this.currentUser = user;
        this.userList = list;

        photoLabel.setText("Editing tags for: " + photo.getCaption()
                .replace("", photo.getCaption().isEmpty() ? "(no caption)" : photo.getCaption()));
        // Fallback label using filename
        String[] parts = photo.getFilePath().replace("\\", "/").split("/");
        photoLabel.setText("Editing tags for: " + parts[parts.length - 1]);

        refreshTagList();
        refreshTagTypeCombo();
    }

    /** Populate the ListView with the photo's current tags */
    private void refreshTagList() {
        tagListView.getItems().clear();
        // We need Photo to expose getTags() — see note below.
        tagListView.getItems().addAll(currentPhoto.getTags());
    }

    /** Populate the ComboBox with the user's defined tag type names */
    private void refreshTagTypeCombo() {
        String previous = tagTypeCombo.getValue();
        tagTypeCombo.getItems().clear();

        for (Map.Entry<String, Boolean> entry : currentUser.getTagTypes().entrySet()) {
            tagTypeCombo.getItems().add(entry.getKey());
        }
        tagTypeCombo.getItems().sort(String::compareTo);

        if (previous != null && tagTypeCombo.getItems().contains(previous)) {
            tagTypeCombo.setValue(previous);
        }
    }

    /** Remove the selected tag from the photo */
    @FXML
    public void handleRemoveTag() {
        Tag selected = tagListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a tag to remove.");
            return;
        }
        currentPhoto.removeTag(selected);
        save();
        refreshTagList();
    }

    /** Add a new tag to the photo after validating type and single-value constraint */
    @FXML
    public void handleAddTag() {
        String typeName = tagTypeCombo.getValue();
        String value = tagValueField.getText().strip();

        // Enforce single-value constraint
        boolean isSingleValue = currentUser.getTagTypes().get(typeName);
        if (isSingleValue) {
            for (Tag t : currentPhoto.getTags()) {
                if (t.getName().equals(typeName)) {
                    showError("\"" + typeName + "\" is a single-value tag type. Remove the existing one first.");
                    return;
                }
            }
        }

        try {
            currentPhoto.addTag(typeName, value);
            save();
            tagValueField.clear();
            refreshTagList();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /** Create a new tag type for the user */
    @FXML
    public void handleCreateTagType() {
        String name = newTagTypeField.getText().strip().toLowerCase();
        boolean singleValue = singleValueCheck.isSelected();
        try {
            currentUser.addTagType(name, singleValue);
        }
        catch (IllegalArgumentException e) {
            showError(e.getLocalizedMessage());
        }
        save();

        newTagTypeField.clear();
        singleValueCheck.setSelected(false);
        refreshTagTypeCombo();

        // Auto-select the newly created type
        tagTypeCombo.setValue(name);
    }

    @FXML
    public void handleDone() {
        Stage stage = (Stage) tagListView.getScene().getWindow();
        stage.close();
    }

    private void save() {
        try {
            UserList.write(userList);
        } catch (Exception e) {
            showError("Save failed.");
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tag Editor");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
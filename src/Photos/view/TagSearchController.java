package Photos.view;
 
import Photos.model.Photo;
import Photos.model.Tag;
import Photos.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
 
import java.util.ArrayList;
import java.util.Map;

public class TagSearchController {
    // ── Tag row 1 ──
    @FXML private ComboBox<String> typeCombo1;
    @FXML private TextField        valueField1;
 
    // ── Tag row 2 (shown only in two-tag mode) ──
    @FXML private HBox             tag2Row;
    @FXML private ComboBox<String> typeCombo2;
    @FXML private TextField        valueField2;
 
    // ── AND / OR row ──
    @FXML private HBox        operatorRow;
    @FXML private RadioButton andRadio;
    @FXML private RadioButton orRadio;
 
    // ── Mode radios ──
    @FXML private RadioButton oneTagRadio;
    @FXML private RadioButton twoTagsRadio;

    private MainController mainController;
    private User currentUser;

    public void setData(MainController controller, User user) {
        this.mainController = controller;
        this.currentUser    = user;
 
        populateTypeCombos();
 
        // Default state: one-tag mode, AND pre-selected
        ToggleGroup modeGroup = new ToggleGroup();
        oneTagRadio.setToggleGroup(modeGroup);
        twoTagsRadio.setToggleGroup(modeGroup);
        oneTagRadio.setSelected(true);
 
        ToggleGroup opGroup = new ToggleGroup();
        andRadio.setToggleGroup(opGroup);
        orRadio.setToggleGroup(opGroup);
        andRadio.setSelected(true);
    }
    /** Fill both ComboBoxes with the user's defined tag type names. */
    private void populateTypeCombos() {
        ArrayList<String> types = new ArrayList<>(currentUser.getTagTypes().keySet());
        types.sort(String::compareTo);
 
        typeCombo1.getItems().setAll(types);
        typeCombo2.getItems().setAll(types);
    }
    /** Show/hide the second-tag row and AND/OR row based on mode selection. */
    @FXML
    public void handleModeChange() {
        boolean twoTags = twoTagsRadio.isSelected();
 
        tag2Row.setVisible(twoTags);
        tag2Row.setManaged(twoTags);
        operatorRow.setVisible(twoTags);
        operatorRow.setManaged(twoTags);
    }

    @FXML
    public void handleSearch() {
        String type1  = typeCombo1.getValue();
        String value1 = valueField1.getText().strip().toLowerCase();
        if (type1 == null || type1.isEmpty()) {
            showError("Please select a type for Tag 1.");
            return;
        }
        if (value1.isEmpty()) {
            showError("Please enter a value for Tag 1.");
            return;
        }
        Tag tag1 = new Tag(type1, value1);
        ArrayList<Photo> results;
        if (oneTagRadio.isSelected()) {
            results = currentUser.singleSearch(tag1);
        }
        else {
            // ── Two-tag search ──
            String type2  = typeCombo2.getValue();
            String value2 = valueField2.getText().strip().toLowerCase();
 
            if (type2 == null || type2.isEmpty()) {
                showError("Please select a type for Tag 2.");
                return;
            }
            if (value2.isEmpty()) {
                showError("Please enter a value for Tag 2.");
                return;
            }
            Tag tag2 = new Tag(type2, value2);
            if (andRadio.isSelected()) {
                results = currentUser.searchAnd(tag1, tag2);
            } else {
                results = currentUser.searchOr(tag1, tag2);
            }
        }
        mainController.showSearchResults(results);
 
        // Close this window
        Stage stage = (Stage) typeCombo1.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tag Search");
        alert.setHeaderText("Invalid input");
        alert.setContentText(msg);
        alert.showAndWait();
    }

}

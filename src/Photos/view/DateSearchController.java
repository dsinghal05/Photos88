package Photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import Photos.model.Photo;
import Photos.model.User;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Controller for the date range search dialog.
 * Allows users to search for photos between a start and end date.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class DateSearchController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private MainController mainController;
    private User currentUser;

    public void setData(MainController controller, User user) {
        this.mainController = controller;
        this.currentUser = user;
    }

    @FXML
    private void handleSearch() {

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showError("Enter dates to search between.");
            return; // you can show alert later
        }

        Calendar start = toCalendar(startDatePicker.getValue());
        Calendar end = toCalendar(endDatePicker.getValue());

        if (start.after(end)) {showError("Dates entered in wrong order."); return;}

        ArrayList<Photo> results = currentUser.searchByDate(start, end);

        mainController.showSearchResults(results);

        // close window
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }

    private Calendar toCalendar(java.time.LocalDate date) {
        Date utilDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        return cal;
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search by Date");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
package Photos.view;

import Photos.model.Album;
import Photos.model.Photo;
import Photos.model.Tag;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Controller for the full photo display view.
 * Shows the selected photo full-size along with its caption, date, and tags.
 * Supports forward/backward navigation through the album (manual slideshow).
 * 
 * @author Divya Raizada
 */
public class PhotoViewController {

    @FXML private ImageView photoImageView;
    @FXML private Label captionLabel;
    @FXML private Label dateLabel;
    @FXML private Label indexLabel;
    @FXML private ListView<Tag> tagListView;

    private Album currentAlbum;
    private ArrayList<Photo> photos;
    private int currentIndex;

    /**
     * Called by AlbumController when opening this view.
     * @param album the album being browsed
     * @param startIndex the index of the photo to display first
     */
    public void setData(Album album, int startIndex) {
        this.currentAlbum = album;
        this.photos = album.getPhotos();
        this.currentIndex = startIndex;

        displayCurrentPhoto();
    }

    /**
     * Updates the view to show the photo at currentIndex.
     */
    private void displayCurrentPhoto() {
        if (photos.isEmpty()) return;

        Photo photo = photos.get(currentIndex);

        // Display image
        Image image = new Image("file:" + photo.getFilePath());
        photoImageView.setImage(image);

        // Display caption
        String caption = photo.getCaption();
        if (caption == null || caption.isEmpty()) {
            captionLabel.setText("(no caption)");
        } else {
            captionLabel.setText(caption);
        }

        // Display date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        dateLabel.setText("Date: " + sdf.format(photo.getDateModified().getTime()));

        // Display tags
        tagListView.getItems().clear();
        tagListView.getItems().addAll(photo.getTags());

        // Update index label (e.g. "Photo 1 of 5")
        indexLabel.setText("Photo " + (currentIndex + 1) + " of " + photos.size());
    }

    /**
     * Navigate to the previous photo in the album.
     * Wraps around to the last photo if at the beginning.
     */
    @FXML
    public void handlePrevious() {
        if (photos.isEmpty()) return;

        if (currentIndex == 0) {
            currentIndex = photos.size() - 1;
        } else {
            currentIndex--;
        }
        displayCurrentPhoto();
    }

    /**
     * Navigate to the next photo in the album.
     * Wraps around to the first photo if at the end.
     */
    @FXML
    public void handleNext() {
        if (photos.isEmpty()) return;

        if (currentIndex == photos.size() - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
        displayCurrentPhoto();
    }

    /**
     * Close this window and return to the album view.
     */
    @FXML
    public void handleBack() {
        Stage stage = (Stage) photoImageView.getScene().getWindow();
        stage.close();
    }
}

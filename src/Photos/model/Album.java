package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<Photo> photos;
    private String title;
    

    public Album(String title) {
        this.title = title;
        this.photos = new ArrayList<Photo>();
    }
    /**
     * Adds photo to album, checks to make sure Photo isn't already in album. 
     * @param p Given photo
     */
    public void addPhoto(Photo p) {
        for (Photo photo : photos) {
            if (photo.getFilePath().equals(p.getFilePath())) {
                throw new IllegalArgumentException("Photo already exists in album");
            }
        }
        photos.add(p);
    }
    public void removePhoto(Photo p) {
        photos.remove(p);
    }
    public ArrayList<Photo> getPhotos() {
        return photos;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}   
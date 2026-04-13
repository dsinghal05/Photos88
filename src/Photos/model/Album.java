package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Album implements Serializable{
    private ArrayList<Photo> photos;
    private String title;
    

    public Album(String title) {
        this.title = title;
    }
    public void addPhoto(Photo p) {
        photos.add(p);
    }
    public void removePhoto(Photo p) {
        photos.remove(p);
    }
    public void sortBy(Comparator<Photo> c) {
        photos.sort(c);
        //Choose & refresh view in Controller
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
    //
}   
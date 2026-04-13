package Photos.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.scene.image.Image;

public class Photo implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<Tag> tags;
    private String caption;
    private String filePath;
    Calendar dateModified;

    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<Tag>();

        File file = new File(filePath);
        long lastModifiedMillis = file.lastModified();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastModifiedMillis);
        cal.set(Calendar.MILLISECOND,0);
        this.dateModified = cal;
    }
    public String getFilePath() {
        return filePath;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String c) {
        this.caption = c;
    }
    public Calendar getDateModified() {
        return dateModified;
    }
    public void addTag(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Enter a valid name");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Enter a valid value");
        }
        Tag newTag = new Tag(name, value);

        //Check for SingleValueTag in Controller
        if (tags.contains(newTag)) {
            throw new IllegalArgumentException("Duplicate tag");
        }
        tags.add(newTag);
    }
    public ArrayList<Tag> getTags() {
        return tags;
    }
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }
    public Image getImage() {
        return new Image("file:" + filePath);
    }
    public String toString() {
        return filePath;
    }

}
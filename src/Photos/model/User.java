package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/** Non admin user can: Create, delete, rename, open albums.  */
public class User implements Serializable, Comparable<User>{
    private static final long serialVersionUID = 1L;
    String username;
    private ArrayList<Album> albums;
    private HashMap<String, Photo> photoMap; //all photos User has added, to avoid making two Photo objects for the same filePath
    private HashMap<String, Boolean> tagTypes;

    public String getUsername() {
        return username;
    }
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<Album>();
        this.photoMap = new HashMap<>();
        this.tagTypes = new HashMap<>();
        tagTypes.put("location", true);
        tagTypes.put("person", false);
    }
    public void addTagType(String name, boolean singleValue) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Please enter a name for the new tag type.");
        }
        if (tagTypes.containsKey(name)) {
            throw new IllegalArgumentException("Tag type \"" + name + "\" already exists.");
        }
        tagTypes.put(name.toLowerCase(), singleValue);
    }
    public HashMap<String, Boolean> getTagTypes() {
        return tagTypes;
    }
    public void createAlbum(String title) {
        for (Album a : albums) {
            if (a.getTitle().equals(title)) {
                throw new IllegalArgumentException("Album already exists.");
            }
        }
        albums.add(new Album(title));
    }
    public void deleteAlbum(Album a) {
        albums.remove(a);
    }
    public ArrayList<Album> getAlbums() {
        return albums;
    }
    public void renameAlbum(Album a, String title) {
        a.setTitle(title);
    }
    //User handles returning Photo objects to ensure the same Object is only referenced once between all of User's albums.
    public Photo getOrCreatePhoto(String filePath) {
        if (photoMap.containsKey(filePath)) {
            return photoMap.get(filePath);
        }

        Photo p = new Photo(filePath);
        photoMap.put(filePath, p);
        return p;
    }

    public ArrayList<Photo> searchByDate(Calendar start, Calendar end) {
        ArrayList<Photo> validPhotos = new ArrayList<>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                Calendar pDate = p.getDateModified();
                if ((!pDate.before(start) && !pDate.after(end)) && (!validPhotos.contains(p))) {validPhotos.add(p);}
            }
        }
        return validPhotos;
    }
    public ArrayList<Photo> singleSearch(Tag tag) {
        ArrayList<Photo> validPhotos = new ArrayList<>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                if (p.hasTag(tag) && !validPhotos.contains(p)) {validPhotos.add(p);}
            }
        }
        return validPhotos;
    }
    public ArrayList<Photo> searchAnd(Tag tag1, Tag tag2) {
        ArrayList<Photo> validPhotos = new ArrayList<>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                if (p.hasTag(tag1) && p.hasTag(tag2) && !validPhotos.contains(p)) {validPhotos.add(p);}
            }
        }
        return validPhotos;
    }
    public ArrayList<Photo> searchOr(Tag tag1, Tag tag2) {
        ArrayList<Photo> validPhotos = new ArrayList<>();
        for (Album a : albums) {
            for (Photo p : a.getPhotos()) {
                if ((p.hasTag(tag1) || p.hasTag(tag2)) && !validPhotos.contains(p)) {validPhotos.add(p);}
            }
        }
        return validPhotos;
    }

    /** Compares User objects by alphabetical order of their username     */
    public int compareTo(User u) {
        return this.getUsername().compareTo(u.getUsername());
    }
}

package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;

/** Non admin user can: Create, delete, rename, open albums.  */
public class User implements Serializable, Comparable<User>{
    String username;
    private ArrayList<Album> albums;

    public String getUsername() {
        return username;
    }
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<Album>();
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

    /** Compares User objects by alphabetical order of their username     */
    public int compareTo(User u) {
        return this.getUsername().compareTo(u.getUsername());
    }
}

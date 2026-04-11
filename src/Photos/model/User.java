package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable, Comparable<User>{
    String username;
    private ArrayList<Album> albums;

    public String getUsername() {
        return username;
    }
    public User(String username) {
        this.username = username;
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

package Photos.model;

import java.io.Serializable;


public class User implements Serializable, Comparable<User>{
    String username;
    //Will hold albums eventually
    public String getUsername() {
        return username;
    }

    public User(String username) {
        this.username = username;
    }

    /** Compares User objects by alphabetical order of their username     */
    public int compareTo(User u) {
        return this.getUsername().compareTo(u.getUsername());
    }
}

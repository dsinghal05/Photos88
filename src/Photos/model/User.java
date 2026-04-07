package Photos.model;

import java.io.Serializable;

public class User implements Serializable{
    String username;
    
    public String getUsername() {
        return username;
    }

    public User(String username) {
        this.username = username;
    }
}

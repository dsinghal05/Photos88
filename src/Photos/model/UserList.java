package Photos.model;

import java.io.*;
import java.util.ArrayList;

public class UserList implements Serializable {
    private ArrayList<User> users = new ArrayList<>();

    public void addUser(String username) {
        users.add(new User(username));
        //ensure you can't reuse usernames
    }

    public void removeUser(String username) {
        if (username.equals("admin")) {
            //admin can not be removed
        }

        users.removeIf(u -> u.getUsername().equals(username));
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    //Saving
    public static void write(UserList list) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream("userslist.dat")
        );
        oos.writeObject(list);
        oos.close();
    }

    //Loading
    public static UserList readList() throws IOException, ClassNotFoundException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userslist.dat"));
            UserList list = (UserList)ois.readObject();
            ois.close();
            return list;
        }
        catch (Exception e) {
            return new UserList();
        }
        
    }
}
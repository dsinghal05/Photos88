package Photos.model;

import java.io.*;
import java.util.ArrayList;

/**
 * UserList is the Serializable object that holds the users ArrayList.
 * It handles adding and removing users and saves the data to "userslist.dat"
 */
public class UserList implements Serializable {
    private ArrayList<User> users = new ArrayList<>();
    
    /**
     * Adds User to users array given their username & saves it to userslist.dat
     * @param username of new User
     * @throws IOException
     */
    public void addUser(String username) throws IOException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Must enter a valid username.");
        }
        if (usernameAlreadyUsed(username)) {
            throw new IllegalArgumentException("User already exists.");
        }
        else {
            users.add(new User(username));
            write(this);
        }
    }

    /**
     * Helper function for addUser to ensure unique usernames
     * @param candidate Username to test
     */
    public boolean usernameAlreadyUsed(String candidate) {
        for (User u : users) {
            if (u.getUsername().equals(candidate)) {
                return true;
            }
        }
        return false;
        
    }

    /**
     * Removes specified user and updates userslist.dat
     * @param u User you seek to remove
     * @throws IOException 
     */
    public void removeUser(User u) throws IOException {
        if (u.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Can not remove admin.");
        }

        users.remove(u);
        write(this);
    }

    /**
     * @return users ArrayList
     */
    public ArrayList<User> getUsers() {
        users.sort(null);
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
    public static UserList readList() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userslist.dat"));
            UserList list = (UserList) ois.readObject();
            ois.close();

            // Admin is treated as a seperate entity, add a fake user for the list.
            if (!list.usernameAlreadyUsed("admin")) {
                list.users.add(new User("admin"));
            }
            
            return list;

        } catch (Exception e) {
            // First run: create list WITH admin
            UserList list = new UserList();
            list.users.add(new User("admin"));
            return list;
        }
    }
}
package Photos.model;

import java.io.*;
import java.util.ArrayList;

/**
 * UserList is the Serializable object that holds the users ArrayList.
 * It handles adding and removing users and saves the data to "userslist.dat".
 * Also initializes the stock user with pre-loaded photos on first run.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class UserList implements Serializable {
    private static final long serialVersionUID = 1L;
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
            // First run: create list WITH admin and stock user
            UserList list = new UserList();
            list.users.add(new User("admin"));
            initializeStockUser(list);
            return list;
        }
    }

    /**
     * Creates the "stock" user with a "stock" album containing
     * all photos found in the data directory.
     * @param list the UserList to add the stock user to
     */
    private static void initializeStockUser(UserList list) {
        User stockUser = new User("stock");
        stockUser.createAlbum("stock");
        Album stockAlbum = stockUser.getAlbums().get(0);

        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                    || lower.endsWith(".png") || lower.endsWith(".bmp")
                    || lower.endsWith(".gif");
            });

            if (files != null) {
                for (File f : files) {
                    Photo p = stockUser.getOrCreatePhoto(f.getAbsolutePath());
                    try {
                        stockAlbum.addPhoto(p);
                    } catch (IllegalArgumentException ex) {
                        // skip duplicates
                    }
                }
            }
        }

        list.users.add(stockUser);
    }
}
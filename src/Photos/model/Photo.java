package Photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Photo implements Serializable{
    ArrayList<Tag> tags;
    String caption;
    //Save actual photo as File path?
    Calendar dateModified;

    
}

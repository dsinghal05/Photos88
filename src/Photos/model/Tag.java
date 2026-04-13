package Photos.model;

import java.io.Serializable;

/**
 * Represents a tag as a name-value pair, e.g. ("person", "Alice") or ("location", "New Brunswick").
 * Two tags are considered equal if they have the same name and value.
 * 
 * @author Divit Singhal
 * @author Divya Raizada
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;

    //When adding a tag, make sure it exists in User.tagTypes
    public Tag(String name, String value) {
        this.name = name.toLowerCase();
        this.value = value.toLowerCase();
    }

    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag t = (Tag) o;
        return name.equals(t.name) && value.equals(t.value);
    }
    public String toString() {
        return name + ": " + value;
    }
}
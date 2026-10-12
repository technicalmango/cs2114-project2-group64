package waypoint;

import java.util.Objects;

/**
 * Represents an activity planned at a destination. This class stores the
 * activity name and provides methods to get and change it. Activities are
 * considered equal when their names match. The toString() method returns the
 * activity name for display.
 *
 * @author Alexander Kyle
 * @version October 2026
 */
public class Activity
{
    private String name;

    /** Creates an activity with the given name. */
    public Activity(String name)
    {
        this.name = name;
    }


    /** Returns the activity name. */
    public String getName()
    {
        return name;
    }


    /** Changes the activity name. */
    public void setName(String name)
    {
        this.name = name;
    }


    /** Checks whether two activities have the same name. */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Activity other = (Activity)obj;
        return Objects.equals(name, other.name);
    }


    /** Returns a hash code based on the activity name. */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(name);
    }


    /** Returns the activity name for display. */
    @Override
    public String toString()
    {
        return name;
    }
}

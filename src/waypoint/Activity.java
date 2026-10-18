package waypoint;

import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate date;
    private LocalTime time;
    private double estimatedCost;
    private String name;

    /** Creates an activity with the given name. */
    public Activity(String name)
    {
        this.name = name;
    }


    public Activity(
        String name,
        LocalDate date,
        LocalTime time,
        double estimatedCost)
    {
        setName(name);
        setDate(date);
        setTime(time);
        setEstimatedCost(estimatedCost);
    }


    public LocalDate getDate()
    {
        return date;
    }


    public double getEstimatedCost()
    {
        return estimatedCost;
    }


    public LocalTime getTime()
    {
        return time;
    }


    public void setDate(LocalDate date)
    {
        if (date == null)
        {
            throw new IllegalArgumentException("Activity date cannot be null");
        }
        this.date = date;
    }


    public void setTime(LocalTime time)
    {
        if (time == null)
        {
            throw new IllegalArgumentException("Activity date cannot be null");
        }
        this.time = time;
    }


    public void setEstimatedCost(double estimatedCost)
    {
        if (!InputValidator.isValidCost(estimatedCost))
        {
            throw new IllegalArgumentException("Activity date cannot be null");
        }
        this.estimatedCost = estimatedCost;
    }


    /** Returns the activity name. */

    public String getName()
    {
        return name;
    }


    /** Changes the activity name. */
    public void setName(String name)
    {
        if (name.isEmpty())
        {
            throw new IllegalArgumentException("Name cannot be null");
        }

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

package waypoint;

import java.time.LocalDate;

/**
 * Represents a destination and its planned activities.
 */
public class Destination
{
    private String name;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private LinkedChain<Activity> activities;

    /**
     * Creates a destination without scheduled dates.
     * Supports older saved files.
     */
    public Destination(String name)
    {
        this(name, null, null);
    }


    /**
     * Creates a destination with optional arrival and departure dates.
     */
    public Destination(
        String name,
        LocalDate arrivalDate,
        LocalDate departureDate)
    {
        setName(name);
        setDates(arrivalDate, departureDate);
        activities = new LinkedChain<Activity>();
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        if (!InputValidator.isValidName(name))
        {
            throw new IllegalArgumentException(
                "Destination name cannot be null or blank");
        }

        this.name = name.trim();
    }


    public LocalDate getArrivalDate()
    {
        return arrivalDate;
    }


    public LocalDate getDepartureDate()
    {
        return departureDate;
    }


    /**
     * Updates both dates together.
     * Null means that the date has not been scheduled.
     */
    public void setDates(LocalDate arrival, LocalDate departure)
    {
        if (arrival != null && departure != null
            && !InputValidator.isValidDateRange(arrival, departure))
        {
            throw new IllegalArgumentException(
                "Departure date cannot precede arrival date");
        }

        arrivalDate = arrival;
        departureDate = departure;
    }


    public void setArrivalDate(LocalDate arrival)
    {
        setDates(arrival, departureDate);
    }


    public void setDepartureDate(LocalDate departure)
    {
        setDates(arrivalDate, departure);
    }


    public void addActivity(Activity activity)
    {
        if (activity == null)
        {
            throw new IllegalArgumentException("Activity cannot be null");
        }

        activities.add(activity);
    }


    public Activity getActivity(int position)
    {
        return activities.get(position);
    }


    public Activity removeActivity(int position)
    {
        return activities.remove(position);
    }


    public int getActivityCount()
    {
        return activities.size();
    }


    public boolean hasActivities()
    {
        return !activities.isEmpty();
    }


    /**
     * Destinations remain equal when their names match.
     */
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

        Destination other = (Destination)obj;
        return name.equals(other.name);
    }


    @Override
    public int hashCode()
    {
        return name.hashCode();
    }


    /**
     * Keeps the existing text representation.
     * The application displays scheduling details separately.
     */
    @Override
    public String toString()
    {
        return name + " (" + getActivityCount() + " activities)";
    }
}

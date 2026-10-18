package waypoint;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Destination
{
    // ~ Fields ................................................................
    private String name;
    private LinkedChain<Activity> activities;
    // Optional stay dates. They remain null until entered.
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    // ~ Constructors ..........................................................

    public Destination(String name)
    {
        setName(name);
        activities = new LinkedChain<Activity>();
    }


    // ~Public Methods ........................................................
    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Destination name cannot be null or blank");

        }
        this.name = name.trim();
    }


    public void addActivity(Activity a)
    {
        if (a == null)
        {
            throw new IllegalArgumentException("Activity cannot be null");
        }
        // Preserve the order activities were added.
        activities.addToEnd(a);
    }


    public Activity getActivity(int p)
    {
        return activities.get(p);
    }


    public Activity removeActivity(int p)
    {
        return activities.remove(p);
    }


    public int getActivityCount()
    {
        return activities.size();
    }


    public boolean hasActivities()
    {
        return !activities.isEmpty();
    }


    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Destination s = (Destination)o;
        return name.equals(s.name);
    }


    public int hashCode()
    {
        return name.hashCode();
    }


    public String toString()
    {
        return name + " (" + getActivityCount() + " activities)";
    }

    /** Returns the arrival date, or null if not entered. */
public LocalDate getArrivalDate()
{
    return arrivalDate;
}

/** Returns the departure date, or null if not entered. */
public LocalDate getDepartureDate()
{
    return departureDate;
}

/** Sets arrival without allowing it to fall after departure. */
public void setArrivalDate(LocalDate arrival)
{
    if (arrival == null)
    {
        throw new IllegalArgumentException("Arrival date is required.");
    }

    if (departureDate != null && arrival.isAfter(departureDate))
    {
        throw new IllegalArgumentException(
            "Arrival cannot be after departure.");
    }

    arrivalDate = arrival;
}

/** Sets departure without allowing it to fall before arrival. */
public void setDepartureDate(LocalDate departure)
{
    if (departure == null)
    {
        throw new IllegalArgumentException("Departure date is required.");
    }

    if (arrivalDate != null && departure.isBefore(arrivalDate))
    {
        throw new IllegalArgumentException(
            "Departure cannot be before arrival.");
    }

    departureDate = departure;
}

/** Changes both dates together, validating before changing either field. */
public void setStayDates(LocalDate arrival, LocalDate departure)
{
    if (!InputValidator.isValidDateRange(arrival, departure))
    {
        throw new IllegalArgumentException(
            "Both dates are required, and departure cannot precede arrival.");
    }

    // Assign only after validation succeeds.
    arrivalDate = arrival;
    departureDate = departure;
}

/** Reports whether both stay dates have been entered. */
public boolean hasStayDates()
{
    return arrivalDate != null && departureDate != null;
}

/** Counts both arrival and departure days; returns 0 for unknown dates. */
public long getStayDays()
{
    if (!hasStayDates())
    {
        return 0;
    }

    // A same-day visit counts as one day.
    return ChronoUnit.DAYS.between(arrivalDate, departureDate) + 1;
}
}

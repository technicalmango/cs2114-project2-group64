package waypoint;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Runs the WayPoint console application. Menu positions start at one; stored
 * indices start at zero.
 *
 * @author Jovany
 * @version 2.0
 */
public class RoadTrip
{
    private String name;
    private LinkedChain<Destination> destinations;

    /**
     * Creates the normal console application.
     */
    public RoadTrip(String name)
    {
        if (!InputValidator.isValidName(name))
        {
            throw new IllegalArgumentException("Trip name cannot be blank.");
        }

        this.name = name.trim();
        destinations = new LinkedChain<Destination>();
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        if (!InputValidator.isValidName(name))
        {
            throw new IllegalArgumentException("Trip name cannot be blank.");
        }

        this.name = name.trim();
    }


    public void addDestination(Destination destination)
    {
        if (destination == null)
        {
            throw new IllegalArgumentException("Destination cannot be null.");
        }

        destinations.addToEnd(destination);
    }


    public void insertDestination(int position, Destination destination)
    {
        if (destination == null)
        {
            throw new IllegalArgumentException("Destination cannot be null.");
        }

        if (!InputValidator
            .isValidInsertionPosition(position, destinations.size()))
        {
            throw new IndexOutOfBoundsException(
                "Invalid insertion position: " + position);
        }

        destinations.add(position, destination);
    }


    public Destination getDestination(int position)
    {
        if (!InputValidator
            .isValidExistingPosition(position, destinations.size()))
        {
            throw new IndexOutOfBoundsException(
                "Invalid destination position: " + position);
        }

        return destinations.get(position);
    }


    public Destination removeDestination(int position)
    {
        if (!InputValidator
            .isValidExistingPosition(position, destinations.size()))
        {
            throw new IndexOutOfBoundsException(
                "Invalid destination position: " + position);
        }

        return destinations.remove(position);
    }


    /**
     * Returns the number of destinations in the trip.
     *
     * @return the destination count
     */
    public int getDestinationCount()
    {
        return destinations.size();
    }


    /**
     * Checks whether the trip has no destinations.
     *
     * @return true if the trip has no destinations
     */
    public boolean isEmpty()
    {
        return destinations.isEmpty();
    }


    /**
     * Calculates the total number of activities across all destinations.
     *
     * @return the total activity count
     */
    public int getTotalActivities()
    {
        int total = 0;

        for (int i = 0; i < destinations.size(); i++)
        {
            total += destinations.get(i).getActivityCount();
        }

        return total;
    }


    /**
     * Finds the destination containing the most activities. If multiple
     * destinations tie, the first is returned.
     *
     * @return the most active destination, or null if the trip contains no
     *             destinations
     */
    public Destination getMostActiveDestination()
    {
        if (destinations.isEmpty())
        {
            return null;
        }

        Destination mostActive = destinations.get(0);

        for (int i = 1; i < destinations.size(); i++)
        {
            Destination current = destinations.get(i);

            if (current.getActivityCount() > mostActive.getActivityCount())
            {
                mostActive = current;
            }
        }

        return mostActive;
    }


    /**
     * Returns the destinations in their travel order.
     *
     * @return a formatted description of the trip
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append("Trip: ").append(name);

        if (destinations.isEmpty())
        {
            result.append("\nNo destinations planned.");
            return result.toString();
        }

        for (int i = 0; i < destinations.size(); i++)
        {
            result.append("\n");
            result.append(i + 1);
            result.append(". ");
            result.append(destinations.get(i).getName());
        }

        return result.toString();
    }
}

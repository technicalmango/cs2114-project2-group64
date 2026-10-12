package waypoint;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Runs the WayPoint console application.
 * Menu positions start at one; stored indices start at zero.
 *
 * @author Jovany
 * @version 2.0
 */
public class WayPointApp
{
    private RoadTrip currentTrip;
    private final Scanner scanner;
    private final PrintStream output;
    private final TripFileManager fileManager;

    /**
     * Creates the normal console application.
     */
    public WayPointApp()
    {
        this(new Scanner(System.in),
            System.out, new TripFileManager());
    }

    /**
     * Creates an application with supplied dependencies for testing.
     *
     * @param scanner the input source
     * @param output the output destination
     * @param fileManager the persistence service
     */
    WayPointApp(
        Scanner scanner,
        PrintStream output,
        TripFileManager fileManager)
    {
        this.scanner = scanner;
        this.output = output;
        this.fileManager = fileManager;
    }

    /**
     * Starts the application.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args)
    {
        new WayPointApp().run();
    }

    /**
     * Loads saved data and runs the menu.
     */
    void run()
    {
        try
        {
            currentTrip = fileManager.loadTrip();

            if (currentTrip != null)
            {
                output.println(
                    "Loaded trip: " + currentTrip.getName());
            }
        }
        catch (Exception exception)
        {
            output.println(
                "Could not load trip: " + exception.getMessage());
        }

        try
        {
            boolean running = true;

            while (running)
            {
                displayMainMenu();

                switch (readMenuChoice())
                {
                    case 1:
                        createTrip();
                        break;

                    case 2:
                        addDestination();
                        break;

                    case 3:
                        insertDestination();
                        break;

                    case 4:
                        removeDestination();
                        break;

                    case 5:
                        viewDestinations();
                        break;

                    case 6:
                        addActivity();
                        break;

                    case 7:
                        removeActivity();
                        break;

                    case 8:
                        viewActivities();
                        break;

                    case 9:
                        viewTripSummary();
                        break;

                    case 10:
                        viewRouteInformation();
                        break;

                    case 0:
                        running = !saveAndExit();
                        break;

                    default:
                        break;
                }
            }
        }
        catch (NoSuchElementException exception)
        {
            output.println(
                "Input ended. Attempting to save the trip.");
            saveAndExit();
        }
    }

    /**
     * Displays the main menu.
     */
    public void displayMainMenu()
    {
        output.println("\n========== WAYPOINT ==========");
        output.println("1. Create trip");
        output.println("2. Add destination");
        output.println("3. Insert destination");
        output.println("4. Remove destination");
        output.println("5. View destinations");
        output.println("6. Add activity");
        output.println("7. Remove activity");
        output.println("8. View activities");
        output.println("9. View trip summary");
        output.println("10. Route information (optional)");
        output.println("0. Save and exit");
    }

    /**
     * Creates a new trip.
     */
    public void createTrip()
    {
        if (currentTrip != null)
        {
            output.print(
                "Replace current trip and lose unsaved changes? "
                    + "Enter yes: ");

            if (!scanner.nextLine().trim().equalsIgnoreCase("yes"))
            {
                return;
            }
        }

        String name = readName("Trip name: ");
        currentTrip = new RoadTrip(name);

        output.println("Trip created.");
    }

    /**
     * Adds a destination to the end of the trip.
     */
    public void addDestination()
    {
        if (!requireTrip())
        {
            return;
        }

        String name = readName("Destination name: ");
        currentTrip.addDestination(new Destination(name));

        output.println("Destination added.");
    }

    /**
     * Inserts a destination at a selected travel position.
     */
    public void insertDestination()
    {
        if (!requireTrip())
        {
            return;
        }

        viewDestinations();

        int index = readPosition(
            currentTrip.getDestinationCount(), true);

        String name = readName("Destination name: ");

        currentTrip.insertDestination(
            index, new Destination(name));

        output.println("Destination inserted.");
    }

    /**
     * Removes a selected destination.
     */
    public void removeDestination()
    {
        if (!requireDestinations())
        {
            return;
        }

        viewDestinations();

        int index = readPosition(
            currentTrip.getDestinationCount(), false);

        Destination removed =
            currentTrip.removeDestination(index);

        output.println("Removed: " + removed.getName());
    }

    /**
     * Displays destinations in travel order.
     */
    public void viewDestinations()
    {
        if (requireTrip())
        {
            output.println(currentTrip);
        }
    }

    /**
     * Adds an activity to a selected destination.
     */
    public void addActivity()
    {
        Destination stop = selectDestination();

        if (stop == null)
        {
            return;
        }

        String name = readName("Activity name: ");
        stop.addActivity(new Activity(name));

        output.println("Activity added.");
    }

    /**
     * Removes an activity by its position.
     */
    public void removeActivity()
    {
        Destination stop = selectDestination();

        if (stop == null)
        {
            return;
        }

        displayActivities(stop);

        if (!stop.hasActivities())
        {
            return;
        }

        int index = readPosition(
            stop.getActivityCount(), false);

        Activity removed = stop.removeActivity(index);

        output.println("Removed: " + removed.getName());
    }

    /**
     * Displays activities at a selected destination.
     */
    public void viewActivities()
    {
        Destination stop = selectDestination();

        if (stop != null)
        {
            displayActivities(stop);
        }
    }

    /**
     * Displays the basic trip summary.
     */
    public void viewTripSummary()
    {
        if (!requireTrip())
        {
            return;
        }

        output.println(
            "Destinations: " + currentTrip.getDestinationCount());

        output.println(
            "Total activities: " + currentTrip.getTotalActivities());

        Destination mostActive =
            currentTrip.getMostActiveDestination();

        if (mostActive == null)
        {
            output.println("Most active destination: None");
        }
        else
        {
            output.println(
                "Most active destination: " + mostActive.getName());
        }
    }

    /**
     * Explains that route information is an optional stretch goal.
     */
    public void viewRouteInformation()
    {
        output.println(
            "Route information is an optional stretch goal "
                + "and is not implemented in this version.");
    }

    /**
     * Saves the trip before allowing the application to exit.
     *
     * @return true if exiting is allowed; false if saving failed
     */
    public boolean saveAndExit()
    {
        if (currentTrip == null)
        {
            output.println("No trip to save. Goodbye!");
            return true;
        }

        try
        {
            fileManager.saveTrip(currentTrip);
            output.println("Trip saved. Goodbye!");
            return true;
        }
        catch (Exception exception)
        {
            output.println(
                "Could not save trip: " + exception.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a trip exists.
     *
     * @return true if a trip exists
     */
    private boolean requireTrip()
    {
        if (currentTrip == null)
        {
            output.println("Create a trip first.");
            return false;
        }

        return true;
    }

    /**
     * Checks whether a destination exists.
     *
     * @return true if the trip contains destinations
     */
    private boolean requireDestinations()
    {
        if (!requireTrip())
        {
            return false;
        }

        if (currentTrip.isEmpty())
        {
            output.println("Add a destination first.");
            return false;
        }

        return true;
    }

    /**
     * Lets the user select a destination.
     *
     * @return the selected destination, or null if none exist
     */
    private Destination selectDestination()
    {
        if (!requireDestinations())
        {
            return null;
        }

        viewDestinations();

        int index = readPosition(
            currentTrip.getDestinationCount(), false);

        return currentTrip.getDestination(index);
    }

    /**
     * Displays a destination's numbered activities.
     *
     * @param stop the destination
     */
    private void displayActivities(Destination stop)
    {
        output.println("Activities in " + stop.getName() + ":");

        if (!stop.hasActivities())
        {
            output.println("No activities planned.");
        }

        for (int i = 0; i < stop.getActivityCount(); i++)
        {
            output.println(
                (i + 1) + ". " + stop.getActivity(i).getName());
        }
    }

    /**
     * Reads a valid name.
     *
     * @param prompt the displayed prompt
     * @return a trimmed valid name
     */
    private String readName(String prompt)
    {
        while (true)
        {
            output.print(prompt);
            String name = scanner.nextLine();

            if (InputValidator.isValidName(name))
            {
                return name.trim();
            }

            output.println("Name cannot be blank.");
        }
    }

    /**
     * Reads a valid menu choice.
     *
     * @return a menu choice from zero through ten
     */
    private int readMenuChoice()
    {
        while (true)
        {
            int choice = readInteger("Choose an option: ");

            if (InputValidator.isValidMenuChoice(choice, 0, 10))
            {
                return choice;
            }

            output.println("Choose an option from 0 to 10.");
        }
    }

    /**
     * Reads a displayed position and converts it to a zero-based index.
     *
     * @param size the number of existing entries
     * @param insertion whether insertion at the end is allowed
     * @return a valid zero-based index
     */
    private int readPosition(int size, boolean insertion)
    {
        int maximum = insertion ? size + 1 : size;

        while (true)
        {
            int position = readInteger(
                "Choose position 1-" + maximum + ": ");

            if (position >= 1 && position <= maximum)
            {
                int index = position - 1;
                boolean valid;

                if (insertion)
                {
                    valid =
                        InputValidator.isValidInsertionPosition(
                            index, size);
                }
                else
                {
                    valid =
                        InputValidator.isValidExistingPosition(
                            index, size);
                }

                if (valid)
                {
                    return index;
                }
            }

            output.println("Invalid position.");
        }
    }

    /**
     * Reads an integer, re-prompting after invalid input.
     *
     * @param prompt the displayed prompt
     * @return the entered integer
     */
    private int readInteger(String prompt)
    {
        while (true)
        {
            output.print(prompt);

            try
            {
                return Integer.parseInt(
                    scanner.nextLine().trim());
            }
            catch (NumberFormatException exception)
            {
                output.println("Enter a whole number.");
            }
        }
    }
}

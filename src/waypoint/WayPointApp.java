package waypoint;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Runs the WayPoint road-trip planning application.
 *
 * @author Jovany
 * @version 1.0
 */
public class WayPointApp
{
    private final Scanner scanner;
    private final TripFileManager fileManager;
    private RoadTrip currentTrip;
    private final PrintStream output;

    /**
     * Creates the application.
     */
    public WayPointApp()
    {
        scanner = new Scanner(System.in);
        output = System.out;
        fileManager = new TripFileManager();
    }


    public WayPointApp(
        Scanner scanner,
        PrintStream output,
        TripFileManager fileManager)
    {
        this.scanner = scanner;
        this.output = output;
        this.fileManager = fileManager;
    }


    /**
     * Loads saved data and runs the main menu.
     *
     * @param args
     *            command-line arguments
     */
    public static void main(String[] args)
    {
        WayPointApp app = new WayPointApp();
        app.run();
    }


    public void run()
    {
        try
        {
            currentTrip = fileManager.loadTrip();

            if (currentTrip != null)
            {
                output.println("Loaded trip: " + currentTrip.getName());
            }
        }
        catch (Exception exception)
        {
            output.println(
                "Could not load a saved trip: " + exception.getMessage());
        }

        boolean running = true;

        while (running)
        {
            displayMainMenu();
            int choice = readInt("Choose an option: ", 0, 10);

            switch (choice)
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
                    running = !saveAndExit();
                    break;
                case 0:
                    output.print("Exit without saving? Enter yes to confirm: ");

                    running =
                        !scanner.nextLine().trim().equalsIgnoreCase("yes");
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * Displays the available menu options.
     */
    public void displayMainMenu()
    {
        System.out.println("\n========== WAYPOINT ==========");

        if (currentTrip != null)
        {
            System.out.println("Trip: " + currentTrip.getName());
        }

        System.out.println("1. Create a trip");
        System.out.println("2. Add a destination");
        System.out.println("3. Insert a destination");
        System.out.println("4. Remove a destination");
        System.out.println("5. View destinations");
        System.out.println("6. Add an activity");
        System.out.println("7. Remove an activity");
        System.out.println("8. View activities");
        System.out.println("9. View route information");
        System.out.println("10. Save and exit");
        System.out.println("0. Exit without saving");
    }


    /**
     * Creates a new trip using a name entered by the user.
     */
    public void createTrip()
    {
        if (currentTrip != null)
        {
            System.out.print(
                "Replace the current trip? Unsaved changes will be "
                    + "lost. Enter yes to confirm: ");

            if (!scanner.nextLine().trim().equalsIgnoreCase("yes"))
            {
                return;
            }
        }

        String name = readName("Enter the trip name: ");
        currentTrip = new RoadTrip(name);
        System.out.println("Trip created.");
    }


    /**
     * Adds a destination to the end of the current trip.
     */
    public void addDestination()
    {
        if (!requireTrip())
        {
            return;
        }

        String name = readName("Enter the destination name: ");
        currentTrip.addDestination(new Destination(name));
        System.out.println("Destination added.");
    }


    /**
     * Inserts a destination at a selected position.
     */
    public void insertDestination()
    {
        if (!requireTrip())
        {
            return;
        }

        viewDestinations();

        int position = readInt(
            "Enter the insertion position: ",
            1,
            currentTrip.getDestinationCount() + 1);

        String name = readName("Enter the destination name: ");

        currentTrip.insertDestination(position, new Destination(name));

        System.out.println("Destination inserted.");
    }


    /**
     * Removes a selected destination from the current trip.
     */
    public void removeDestination()
    {
        if (!requireDestinations())
        {
            return;
        }

        viewDestinations();

        int position = readInt(
            "Enter the destination number to remove: ",
            1,
            currentTrip.getDestinationCount());

        Destination removed = currentTrip.removeDestination(position);

        System.out.println("Removed: " + removed.getName());
    }


    /**
     * Displays all destinations in travel order.
     */
    public void viewDestinations()
    {
        if (!requireTrip())
        {
            return;
        }

        System.out.println(currentTrip);
    }


    /**
     * Adds an activity to a selected destination.
     */
    public void addActivity()
    {
        Destination destination = selectDestination();

        if (destination == null)
        {
            return;
        }

        String name = readName("Enter the activity name: ");
        destination.addActivity(new Activity(name));

        System.out.println("Activity added.");
    }


    /**
     * Removes an activity from a selected destination.
     */
    public void removeActivity()
    {
        Destination destination = selectDestination();

        if (destination == null)
        {
            return;
        }

        if (!destination.hasActivities())
        {
            System.out.println("This destination has no activities.");
            return;
        }

        displayActivities(destination);

        int position = readInt(
            "Enter the activity number to remove: ",
            1,
            destination.getActivityCount());

        Activity removed = destination.removeActivity(position);

        System.out.println("Removed: " + removed.getName());
    }


    /**
     * Displays activities for a selected destination.
     */
    public void viewActivities()
    {
        Destination destination = selectDestination();

        if (destination != null)
        {
            displayActivities(destination);
        }
    }


    /**
     * Saves the current trip before allowing the program to exit.
     *
     * @return true if exiting is allowed; false if saving failed
     */
    public boolean saveAndExit()
    {
        if (currentTrip == null)
        {
            System.out.println("No trip to save. Goodbye!");
            return true;
        }

        try
        {
            fileManager.saveTrip(currentTrip);
            System.out.println("Trip saved. Goodbye!");
            return true;
        }
        catch (Exception exception)
        {
            System.out
                .println("Could not save the trip: " + exception.getMessage());
            System.out.println("Your trip is still open.");
            return false;
        }
    }


    /**
     * Checks whether a current trip exists.
     *
     * @return true if a trip exists
     */
    private boolean requireTrip()
    {
        if (currentTrip == null)
        {
            System.out.println("Create a trip first.");
            return false;
        }

        return true;
    }


    /**
     * Checks whether the current trip contains destinations.
     *
     * @return true if at least one destination exists
     */
    private boolean requireDestinations()
    {
        if (!requireTrip())
        {
            return false;
        }

        if (currentTrip.isEmpty())
        {
            System.out.println("Add a destination first.");
            return false;
        }

        return true;
    }


    /**
     * Lets the user select an existing destination.
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

        int position = readInt(
            "Enter the destination number: ",
            1,
            currentTrip.getDestinationCount());

        return currentTrip.getDestination(position);
    }


    /**
     * Displays a destination's numbered activities.
     *
     * @param destination
     *            the destination to display
     */
    private void displayActivities(Destination destination)
    {
        System.out.println("\nActivities in " + destination.getName());

        if (!destination.hasActivities())
        {
            System.out.println("No activities planned.");
            return;
        }

        for (int position = 1; position <= destination
            .getActivityCount(); position++)
        {
            System.out.println(
                position + ". " + destination.getActivity(position).getName());
        }
    }


    /**
     * Reads a nonblank name.
     *
     * @param prompt
     *            the prompt to display
     * @return the trimmed name
     */
    private String readName(String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String name = scanner.nextLine().trim();

            if (!name.isEmpty())
            {
                return name;
            }

            System.out.println("The name cannot be blank.");
        }
    }


    /**
     * Reads an integer within the given inclusive range.
     *
     * @param prompt
     *            the prompt to display
     * @param min
     *            the smallest valid value
     * @param max
     *            the largest valid value
     * @return a valid integer
     */
    private int readInt(String prompt, int min, int max)
    {
        while (true)
        {
            System.out.print(prompt);

            try
            {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value >= min && value <= max)
                {
                    return value;
                }
            }
            catch (NumberFormatException exception)
            {
                // Display the same guidance for all invalid input.
            }

            System.out.println(
                "Enter a whole number from " + min + " to " + max + ".");
        }
    }
}

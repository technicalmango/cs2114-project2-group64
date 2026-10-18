package waypoint;

import java.io.PrintStream;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

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
                output.print("Loaded trip: " + currentTrip.getName());
            }
        }
        catch (Exception exception)
        {
            output.print(
                "Could not load a saved trip: " + exception.getMessage());
        }

        boolean running = true;

        while (running)
        {
            displayMainMenu();
            int choice = readInt("Choose an option: ", 0, 11);

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
                    viewTripSummary();
                    break;

                case 10:
                    setDestinationDates();
                    break;
                case 11:
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
        output.println("\n========== WAYPOINT ==========");

        if (currentTrip != null)
        {
            output.println("Trip: " + currentTrip.getName());
        }

        output.println("1. Create a trip");
        output.println("2. Add a destination");
        output.println("3. Insert a destination");
        output.println("4. Remove a destination");
        output.println("5. View destinations");
        output.println("6. Add an activity");
        output.println("7. Remove an activity");
        output.println("8. View activities");
        output.println("9. View trip summary");
        output.println("10. Set destination dates");
        output.println("11. Save and exit");
        output.println("0. Exit without saving");
        output.println();
    }


    /**
     * Creates a new trip using a name entered by the user.
     */
    public void createTrip()
    {
        if (currentTrip != null)
        {
            output.print(
                "Replace the current trip? Unsaved changes will be "
                    + "lost. Enter yes to confirm: ");

            if (!scanner.nextLine().trim().equalsIgnoreCase("yes"))
            {
                return;
            }
        }

        String name = readName("Enter the trip name: ");
        currentTrip = new RoadTrip(name);
        output.println();
        output.print("Trip created.");
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
        output.println();
        output.print("Destination added.");
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

        currentTrip.insertDestination(position - 1, new Destination(name));
        output.println();
        output.println("Destination inserted.");
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

        Destination removed = currentTrip.removeDestination(position - 1);

        output.print("Removed: " + removed.getName());
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

        output.print(currentTrip);
        output.println();
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

        if (destination.getArrivalDate() == null
            || destination.getDepartureDate() == null)
        {
            output
                .println("Set the destination dates before adding activities.");
            return;
        }

        String name = readName("Enter the activity name: ");

        while (true)
        {
            LocalDate date = readDate("Enter activity date (YYYY-MM-DD): ");

            if (date.isBefore(destination.getArrivalDate())
                || date.isAfter(destination.getDepartureDate()))
            {
                output.println(
                    "Activity date must be within the destination stay.");
                continue;
            }

            LocalTime time = readTime("Enter activity time (HH:MM): ");

            double cost = readCost("Enter estimated activity cost: ");

            Activity activity = new Activity(name, date, time, cost);

            destination.addActivity(activity);

            output.println("Activity added.");
            return;
        }
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
            output.println("This destination has no activities.");
            return;
        }

        List<Activity> sorted = destination.getActivitiesChronologically();

        output.println("\nActivities in " + destination.getName());

        for (int i = 0; i < sorted.size(); i++)
        {
            Activity activity = sorted.get(i);

            output.println(
                (i + 1) + ". " + activity.getName() + " - " + activity.getDate()
                    + " at " + activity.getTime());
        }

        int position =
            readInt("Enter the activity number to remove: ", 1, sorted.size());

        Activity selected = sorted.get(position - 1);

        for (int i = 0; i < destination.getActivityCount(); i++)
        {
            if (destination.getActivity(i) == selected)
            {
                Activity removed = destination.removeActivity(i);

                output.println("Removed: " + removed.getName());
                return;
            }
        }
        ;
    }


    /**
     * Displays activities for a selected destination.
     */
    public void viewActivities()
    {
        Destination destination = selectDestination();

        if (destination == null)
        {
            return;
        }

        output.println("\nActivities in " + destination.getName());

        if (!destination.hasActivities())
        {
            output.println("No activities planned.");
            return;
        }

        List<Activity> sorted = destination.getActivitiesChronologically();

        for (int i = 0; i < sorted.size(); i++)
        {
            Activity activity = sorted.get(i);

            output.println((i + 1) + ". " + activity.getName());

            output.println("   Date: " + activity.getDate());

            output.println("   Time: " + activity.getTime());

            output.printf(
                "   Estimated cost: $%.2f%n",
                activity.getEstimatedCost());
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
            output.print("No trip to save. Goodbye!");
            return true;
        }

        try
        {
            fileManager.saveTrip(currentTrip);
            output.print("Trip saved. Goodbye!");
            return true;
        }
        catch (Exception exception)
        {
            output
                .println("Could not save the trip: " + exception.getMessage());
            output.println("Your trip is still open.");
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
            output.print("Create a trip first.");
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
            output.print("Add a destination first.");
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

        return currentTrip.getDestination(position - 1);
    }


    /**
     * Displays a destination's numbered activities.
     *
     * @param destination
     *            the destination to display
     */
    private void displayActivities(Destination destination)
    {
        output.println();
        output.println("\nActivities in " + destination.getName());

        if (!destination.hasActivities())
        {
            output.print("No activities planned.");
            output.println();
            return;
        }

        for (int i = 0; i < destination.getActivityCount(); i++)
        {
            output
                .println((i + 1) + ". " + destination.getActivity(i).getName());
        }
    }


    /**
     * Sets the arrival and departure dates for a destination.
     */
    public void setDestinationDates()
    {
        Destination destination = selectDestination();

        if (destination == null)
        {
            return;
        }

        while (true)
        {
            LocalDate arrival = readDate("Enter arrival date (YYYY-MM-DD): ");

            LocalDate departure =
                readDate("Enter departure date (YYYY-MM-DD): ");

            try
            {
                destination.setStayDates(arrival, departure);

                output.println("Destination dates updated.");
                return;
            }
            catch (IllegalArgumentException exception)
            {
                output.println(exception.getMessage());
                output.println("Please enter valid dates.");
            }
        }
    }


    public void viewTripSummary()
    {
        if (!requireTrip())
        {
            return;
        }

        output.println("\n===== TRIP SUMMARY =====");
        output.println("Trip: " + currentTrip.getName());

        output.println("Destinations: " + currentTrip.getDestinationCount());

        output.println("Activities: " + currentTrip.getTotalActivities());

        output.println("Duration: " + currentTrip.getTripDuration() + " days");

        output.printf(
            "Estimated activity cost: $%.2f%n",
            currentTrip.getTotalEstimatedCost());

        Destination mostActive = currentTrip.getMostActiveDestination();

        if (mostActive != null)
        {
            output.println("Most activities: " + mostActive.getName());
        }

        Destination longest = currentTrip.getLongestStayDestination();

        if (longest != null)
        {
            output.println("Longest stay: " + longest.getName());
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
            output.print(prompt);
            String name = scanner.nextLine().trim();

            output.println();

            if (!name.isEmpty())
            {
                return name;
            }

            output.print("The name cannot be blank.");
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
            output.print(prompt);

            try
            {
                int value = Integer.parseInt(scanner.nextLine().trim());

                output.println();

                if (value >= min && value <= max)
                {
                    return value;
                }
            }
            catch (NumberFormatException exception)
            {
                output.println();
            }

            output.println(
                "Enter a whole number from " + min + " to " + max + ".");
            output.println();
        }
    }


    private LocalDate readDate(String prompt)
    {
        while (true)
        {
            output.print(prompt);

            String input = scanner.nextLine().trim();

            try
            {
                return LocalDate.parse(input);
            }
            catch (DateTimeParseException exception)
            {
                output.println("Invalid date. Use YYYY-MM-DD.");
            }
        }
    }


    private LocalTime readTime(String prompt)
    {
        while (true)
        {
            output.print(prompt);

            String input = scanner.nextLine().trim();

            try
            {
                return LocalTime.parse(input);
            }
            catch (DateTimeParseException exception)
            {
                output.println("Invalid time. Use HH:MM (24-hour format).");
            }
        }
    }


    private double readCost(String prompt)
    {
        while (true)
        {
            output.print(prompt);

            try
            {
                double cost = Double.parseDouble(scanner.nextLine().trim());

                if (InputValidator.isValidCost(cost))
                {
                    return cost;
                }
            }
            catch (NumberFormatException exception)
            {
                // Invalid numeric input; show error below.
            }

            output.println("Enter a valid nonnegative cost.");
        }
    }

}

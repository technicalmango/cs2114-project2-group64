package waypoint;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Tests the console application using scripted input. FakeFileManager avoids
 * writing real CSV files. Run this class as a Java Application.
 *
 * @author Jovany
 * @version 2.0
 */
public class WayPointAppTest
{
    private static int checks;

    /**
     * Runs the test cases.
     *
     * @param args
     *            unused
     */
    public static void main(String[] args)
    {
        testInvalidMenu();
        testWithoutTrip();
        testEmptyTrip();
        testDestinationOperations();
        testActivityRemoval();
        testTripReplacement();
        testLoad();
        testLoadFailure();
        testSaveFailure();
        testEndOfInput();

        System.out.println("WayPointAppTest: " + checks + " checks passed.");
    }


    /**
     * Tests nonnumeric, overflowing, and out-of-range menu input.
     */
    private static void testInvalidMenu()
    {
        FakeFileManager files = new FakeFileManager();

        String output = session("oops\n999999999999999999\n-1\n10\n0\n", files);

        check(
            output.contains("Enter a whole number."),
            "Nonnumeric menu input is rejected");

        check(
            output.contains("Choose an option from 0 to 9."),
            "Out-of-range menu input is rejected");

        check(files.saveCalls == 0, "Exiting without a trip does not save");
    }


    /**
     * Tests operations before creating a trip.
     */
    private static void testWithoutTrip()
    {
        FakeFileManager files = new FakeFileManager();

        String output = session("2\n4\n5\n6\n7\n8\n9\n0\n", files);

        check(
            output.contains("Create a trip first."),
            "Operations require a trip");

        check(files.saveCalls == 0, "Invalid operations do not create a trip");
    }


    /**
     * Tests blank names and operations on an empty trip.
     */
    private static void testEmptyTrip()
    {
        FakeFileManager files = new FakeFileManager();

        String output = session("1\n\n  \nFall Trip\n4\n6\n9\n0\n", files);

        check(
            output.contains("Name cannot be blank."),
            "Blank names cause another prompt");

        check(
            output.contains("Add a destination first."),
            "Destination operations require a destination");

        check(
            output.contains("Total activities: 0")
                && output.contains("Most active destination: None"),
            "Empty summary is correct");

        check(files.saved.isEmpty(), "An empty trip can be saved");
    }


    /**
     * Tests insertion, appending, selection, summaries, and removal.
     */
    private static void testDestinationOperations()
    {
        FakeFileManager files = new FakeFileManager();

        String input = "1\nFall Trip\n" + "3\n0\n2\n1\nRichmond\n"
            + "2\nBoston\n" + "3\n2\nDC\n" + "6\n0\n4\n1\n\nMuseum\n"
            + "6\n2\nLunch\n" + "8\n1\n" + "9\n" + "4\n2\n" + "5\n" + "0\n";

        String output = session(input, files);

        check(
            output.contains("Invalid position."),
            "Invalid positions are rejected");

        check(output.contains("1. Museum"), "Activities are displayed");

        check(
            output.contains("Total activities: 2"),
            "Summary shows total activities");

        check(
            output.contains("Most active destination: Richmond"),
            "Summary resolves ties using travel order");

        check(
            files.saved.getDestinationCount() == 2,
            "The selected destination is removed");

        check(
            files.saved.getDestination(0).getName().equals("Richmond")
                && files.saved.getDestination(1).getName().equals("Boston"),
            "Displayed positions convert to zero-based indices");

        check(
            files.saved.getDestination(0).getActivityCount() == 1,
            "Activities belong to the selected destination");
    }


    /**
     * Tests empty activity lists and duplicate activity names.
     */
    private static void testActivityRemoval()
    {
        FakeFileManager files = new FakeFileManager();

        String input = "1\nTrip\n" + "2\nDC\n" + "7\n1\n" + "6\n1\nMuseum\n"
            + "6\n1\nMuseum\n" + "7\n1\n0\n3\n2\n" + "0\n";

        String output = session(input, files);

        check(
            output.contains("No activities planned."),
            "Empty activity lists are handled");

        check(
            files.saved.getDestination(0).getActivityCount() == 1,
            "Duplicate activities are removed by position");
    }


    /**
     * Tests canceling and confirming trip replacement.
     */
    private static void testTripReplacement()
    {
        FakeFileManager files = new FakeFileManager();

        session("1\nOriginal\n2\nDC\n1\nno\n0\n", files);

        check(
            files.saved.getName().equals("Original")
                && files.saved.getDestinationCount() == 1,
            "Canceling replacement preserves the trip");

        files = new FakeFileManager();

        session("1\nOriginal\n2\nDC\n1\nyes\nNew\n0\n", files);

        check(
            files.saved.getName().equals("New") && files.saved.isEmpty(),
            "Confirming replacement creates a new trip");
    }


    /**
     * Tests loading and modifying a saved trip.
     */
    private static void testLoad()
    {
        FakeFileManager files = new FakeFileManager();

        RoadTrip loaded = new RoadTrip("Saved Trip");
        loaded.addDestination(new Destination("Richmond"));
        files.loaded = loaded;

        String output = session("2\nDC\n0\n", files);

        check(
            output.contains("Loaded trip: Saved Trip"),
            "Loading is reported");

        check(
            files.saved == loaded && files.saved.getDestinationCount() == 2,
            "Loaded trip remains editable and is saved");
    }


    /**
     * Tests recovery after a load failure.
     */
    private static void testLoadFailure()
    {
        FakeFileManager files = new FakeFileManager();
        files.failLoad = true;

        String output = session("1\nRecovery\n0\n", files);

        check(
            output.contains("Could not load trip:"),
            "Load failure is reported");

        check(
            files.saved.getName().equals("Recovery"),
            "A new trip can be created after a load failure");
    }


    /**
     * Tests editing and retrying after a save failure.
     */
    private static void testSaveFailure()
    {
        FakeFileManager files = new FakeFileManager();
        files.failNextSave = true;

        String output = session("1\nRetry\n0\n2\nDC\n0\n", files);

        check(
            output.contains("Could not save trip:"),
            "Save failure is reported");

        check(
            files.saveCalls == 2 && files.saved.getDestinationCount() == 1,
            "Failed saves keep the menu running and allow retry");

        check(
            output.contains("Trip saved.") && output
                .indexOf("Trip saved.") == output.lastIndexOf("Trip saved."),
            "Only a successful save claims success");
    }


    /**
     * Tests that ending input attempts a save.
     */
    private static void testEndOfInput()
    {
        FakeFileManager files = new FakeFileManager();

        String output = session("1\nEOF Trip\n", files);

        check(
            output.contains("Input ended.") && files.saveCalls == 1,
            "Ending input attempts to save the trip");
    }


    /**
     * Runs a session with scripted input and captured output.
     *
     * @param input
     *            the scripted input
     * @param files
     *            the fake persistence service
     * @return the captured output
     */
    private static String session(String input, FakeFileManager files)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        PrintStream output = new PrintStream(buffer);

        WayPointApp app = new WayPointApp(new Scanner(input), output, files);

        app.run();

        output.flush();

        return buffer.toString();
    }


    /**
     * Checks a test condition.
     *
     * @param condition
     *            the expected true condition
     * @param message
     *            the failure description
     */
    private static void check(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }

        checks++;
    }

    /**
     * Simulates persistence without reading or writing files.
     */
    private static class FakeFileManager
        extends TripFileManager
    {
        private RoadTrip loaded;
        private RoadTrip saved;
        private int saveCalls;
        private boolean failLoad;
        private boolean failNextSave;

        /**
         * Returns the configured trip or simulates a load failure.
         *
         * @return the configured trip
         */
        @Override
        public RoadTrip loadTrip()
        {
            if (failLoad)
            {
                throw new IllegalStateException("Malformed CSV");
            }

            return loaded;
        }


        /**
         * Records the trip or simulates one failed save.
         *
         * @param trip
         *            the trip to save
         */
        @Override
        public void saveTrip(RoadTrip trip)
        {
            saveCalls++;

            if (failNextSave)
            {
                failNextSave = false;

                throw new IllegalStateException("Unwritable CSV");
            }

            saved = trip;
        }
    }
}

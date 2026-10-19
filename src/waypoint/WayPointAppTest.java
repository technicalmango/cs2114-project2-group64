
package waypoint;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests the WayPoint console application using scripted input. A fake file
 * manager prevents tests from accessing real CSV files.
 *
 * @author Jovany
 * @version October 2026
 */
public class WayPointAppTest
    extends TestCase
{
    private FakeFileManager files;

    /**
     * Sets up each test.
     */
    public void setUp()
    {
        files = new FakeFileManager();
    }


    /**
     * Tests creating and saving a new trip.
     */
    public void testCreateTrip()
    {
        String output = session("1\nFall Trip\n11\n");

        assertTrue(output.contains("Trip created."));
        assertTrue(output.contains("Trip saved."));
        assertEquals(1, files.saveCalls);
        assertNotNull(files.saved);
        assertEquals("Fall Trip", files.saved.getName());
        assertTrue(files.saved.isEmpty());
    }


    /**
     * Tests rejecting invalid menu choices.
     */
    public void testInvalidMenu()
    {
        String output = session("oops\n999999999999999999\n-1\n12\n11\n");

        assertTrue(output.contains("Enter a whole number from 0 to 11."));
        assertEquals(0, files.saveCalls);
    }


    /**
     * Tests operations before a trip exists.
     */
    public void testWithoutTrip()
    {
        String output = session("2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n");

        assertTrue(output.contains("Create a trip first."));
        assertEquals(0, files.saveCalls);
    }


    /**
     * Tests blank trip names and an empty trip.
     */
    public void testEmptyTrip()
    {
        String output = session("1\n\n  \nFall Trip\n4\n6\n11\n");

        assertTrue(output.contains("The name cannot be blank."));
        assertTrue(output.contains("Add a destination first."));
        assertNotNull(files.saved);
        assertTrue(files.saved.isEmpty());
        assertEquals(1, files.saveCalls);
    }


    /**
     * Tests adding and viewing destinations.
     */
    public void testAddDestinations()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "2\nBoston\n" + "5\n"
                + "11\n");

        assertTrue(output.contains("Destination added."));
        assertTrue(output.contains("Richmond"));
        assertTrue(output.contains("Boston"));

        assertEquals(2, files.saved.getDestinationCount());
        assertEquals("Richmond", files.saved.getDestination(0).getName());
        assertEquals("Boston", files.saved.getDestination(1).getName());
    }


    /**
     * Tests inserting a destination between existing stops.
     */
    public void testInsertDestination()
    {
        session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "2\nBoston\n"
                + "3\n2\nWashington DC\n" + "11\n");

        assertEquals(3, files.saved.getDestinationCount());
        assertEquals("Richmond", files.saved.getDestination(0).getName());
        assertEquals("Washington DC", files.saved.getDestination(1).getName());
        assertEquals("Boston", files.saved.getDestination(2).getName());
    }


    /**
     * Tests removing a destination.
     */
    public void testRemoveDestination()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "2\nWashington DC\n"
                + "2\nBoston\n" + "4\n2\n" + "11\n");

        assertTrue(output.contains("Removed: Washington DC"));
        assertEquals(2, files.saved.getDestinationCount());
        assertEquals("Richmond", files.saved.getDestination(0).getName());
        assertEquals("Boston", files.saved.getDestination(1).getName());
    }


    /**
     * Tests adding and viewing scheduled activities.
     */
    public void testAddAndViewActivities()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n"
                + "10\n1\n2026-10-20\n2026-10-25\n" + "6\n1\nWhite House\n"
                + "2026-10-21\n10:00\n20\n" + "6\n1\nLincoln Memorial\n"
                + "2026-10-22\n11:00\n0\n" + "8\n1\n" + "11\n");

        Destination destination = files.saved.getDestination(0);

        assertEquals(2, destination.getActivityCount());
        assertTrue(destination.hasActivities());

        assertTrue(output.contains("Activities in Washington DC"));
        assertTrue(output.contains("White House"));
        assertTrue(output.contains("Lincoln Memorial"));
        assertTrue(output.contains("2026-10-21"));
        assertTrue(output.contains("$20.00"));
    }


    /**
     * Tests removing a scheduled activity.
     */
    public void testRemoveActivity()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n"
                + "10\n1\n2026-10-20\n2026-10-25\n" + "6\n1\nWhite House\n"
                + "2026-10-21\n10:00\n20\n" + "7\n1\n1\n" + "11\n");

        Destination destination = files.saved.getDestination(0);

        assertTrue(output.contains("Removed: White House"));
        assertEquals(0, destination.getActivityCount());
        assertFalse(destination.hasActivities());
    }


    /**
     * Tests removing and viewing activities when none exist.
     */
    public void testRemoveFromEmptyActivities()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n" + "7\n1\n" + "8\n1\n"
                + "11\n");

        assertTrue(output.contains("This destination has no activities."));
        assertTrue(output.contains("No activities planned."));

        assertEquals(0, files.saved.getDestination(0).getActivityCount());
    }


    /**
     * Tests canceling trip replacement.
     */
    public void testCancelTripReplacement()
    {
        session("1\nOriginal\n" + "2\nRichmond\n" + "1\nno\n" + "11\n");

        assertEquals("Original", files.saved.getName());
        assertEquals(1, files.saved.getDestinationCount());
    }


    /**
     * Tests confirming trip replacement.
     */
    public void testConfirmTripReplacement()
    {
        session(
            "1\nOriginal\n" + "2\nRichmond\n" + "1\nyes\nNew Trip\n" + "11\n");

        assertEquals("New Trip", files.saved.getName());
        assertTrue(files.saved.isEmpty());
    }


    /**
     * Tests loading and modifying an existing trip.
     */
    public void testLoadTrip()
    {
        RoadTrip loaded = new RoadTrip("Saved Trip");
        loaded.addDestination(new Destination("Richmond"));
        files.loaded = loaded;

        String output = session("2\nBoston\n11\n");

        assertTrue(output.contains("Loaded trip: Saved Trip"));
        assertSame(loaded, files.saved);
        assertEquals(2, files.saved.getDestinationCount());
        assertEquals("Boston", files.saved.getDestination(1).getName());
    }


    /**
     * Tests recovering from a failed load.
     */
    public void testLoadFailure()
    {
        files.failLoad = true;

        String output = session("1\nRecovery\n11\n");

        assertTrue(output.contains("Could not load a saved trip:"));
        assertNotNull(files.saved);
        assertEquals("Recovery", files.saved.getName());
    }


    /**
     * Tests retrying a save after the first attempt fails.
     */
    public void testSaveFailure()
    {
        files.failNextSave = true;

        String output =
            session("1\nRetry Trip\n" + "11\n" + "2\nRichmond\n" + "11\n");

        assertTrue(output.contains("Your trip is still open."));
        assertTrue(output.contains("Trip saved."));
        assertEquals(2, files.saveCalls);
        assertNotNull(files.saved);
        assertEquals(1, files.saved.getDestinationCount());
    }


    /**
     * Tests exiting without saving.
     */
    public void testExitWithoutSaving()
    {
        session("1\nUnsaved Trip\n0\nyes\n");

        assertEquals(0, files.saveCalls);
        assertNull(files.saved);
    }


    /**
     * Tests canceling an exit without saving.
     */
    public void testCancelExit()
    {
        String output = session("1\nRoad Trip\n" + "0\nno\n" + "11\n");

        assertTrue(output.contains("Trip saved."));
        assertEquals(1, files.saveCalls);
        assertEquals("Road Trip", files.saved.getName());
    }


    /**
     * Tests setting destination dates.
     */
    public void testSetDestinationDates()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n"
                + "10\n1\n2026-10-20\n2026-10-25\n" + "11\n");

        Destination destination = files.saved.getDestination(0);

        assertEquals(LocalDate.of(2026, 10, 20), destination.getArrivalDate());

        assertEquals(
            LocalDate.of(2026, 10, 25),
            destination.getDepartureDate());

        assertTrue(output.contains("Destination dates updated."));
    }


    /**
     * Tests rejecting invalid destination dates.
     */
    public void testInvalidDestinationDates()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "10\n1\n" + "invalid\n"
                + "2026-10-25\n2026-10-20\n" + "2026-10-20\n2026-10-25\n"
                + "11\n");

        assertTrue(output.contains("Invalid date. Use YYYY-MM-DD."));
        assertTrue(output.contains("Please enter valid dates."));
        assertTrue(output.contains("Destination dates updated."));

        assertEquals(
            LocalDate.of(2026, 10, 20),
            files.saved.getDestination(0).getArrivalDate());
    }


    /**
     * Tests preventing activities before destination dates exist.
     */
    public void testAddActivityWithoutDates()
    {
        String output =
            session("1\nRoad Trip\n" + "2\nRichmond\n" + "6\n1\n" + "11\n");

        assertTrue(
            output.contains(
                "Set the destination dates before adding activities."));

        assertEquals(0, files.saved.getDestination(0).getActivityCount());
    }


    /**
     * Tests rejecting invalid activity dates, times, and costs.
     */
    public void testInvalidActivityInputs()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n"
                + "10\n1\n2026-10-20\n2026-10-25\n" + "6\n1\nMuseum\n"
                + "invalid\n" + "2026-10-30\n" + "2026-10-21\n" + "invalid\n"
                + "14:30\n" + "-10\n" + "15\n" + "11\n");

        assertTrue(output.contains("Invalid date. Use YYYY-MM-DD."));
        assertTrue(
            output.contains(
                "Activity date must be within the destination stay."));
        assertTrue(
            output.contains("Invalid time. Use HH:MM (24-hour format)."));
        assertTrue(output.contains("Enter a valid nonnegative cost."));

        Destination destination = files.saved.getDestination(0);

        assertEquals(1, destination.getActivityCount());

        Activity activity = destination.getActivity(0);

        assertEquals("Museum", activity.getName());
        assertEquals(LocalDate.of(2026, 10, 21), activity.getDate());
        assertEquals(LocalTime.of(14, 30), activity.getTime());
        assertEquals(15.0, activity.getEstimatedCost(), 0.001);
    }


    /**
     * Tests the trip summary.
     */
    public void testViewTripSummary()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n"
                + "10\n1\n2026-10-20\n2026-10-25\n" + "6\n1\nMuseum\n"
                + "2026-10-21\n10:00\n25\n" + "9\n" + "11\n");

        assertTrue(output.contains("TRIP SUMMARY"));
        assertTrue(output.contains("Trip: Road Trip"));
        assertTrue(output.contains("Destinations: 1"));
        assertTrue(output.contains("Activities: 1"));
        assertTrue(output.contains("Estimated activity cost: $25.00"));
    }


    /**
     * Tests the summary of an empty trip.
     */
    public void testEmptyTripSummary()
    {
        String output = session("1\nEmpty Trip\n9\n11\n");

        assertTrue(output.contains("TRIP SUMMARY"));
        assertTrue(output.contains("Destinations: 0"));
        assertTrue(output.contains("Activities: 0"));
    }


    /**
     * Tests rejecting invalid destination positions.
     */
    public void testInvalidDestinationPosition()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "4\n0\n5\n1\n" + "11\n");

        assertTrue(output.contains("Enter a whole number from 1 to 1."));
        assertTrue(output.contains("Removed: Richmond"));
        assertEquals(0, files.saved.getDestinationCount());
    }


    /**
     * Tests displaying the main menu.
     */
    public void testDisplayMainMenu()
    {
        String output = session("11\n");

        assertTrue(output.contains("WAYPOINT"));
        assertTrue(output.contains("1. Create a trip"));
        assertTrue(output.contains("9. View trip summary"));
        assertTrue(output.contains("11. Save and exit"));
    }


    /**
     * Runs the application with scripted input.
     *
     * @param input
     *            simulated user input
     * @return captured console output
     */
    private String session(String input)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        PrintStream output = new PrintStream(buffer);

        WayPointApp app = new WayPointApp(new Scanner(input), output, files);

        app.run();
        output.flush();

        return buffer.toString();
    }

    /**
     * Simulates file operations without accessing CSV files.
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
         * Simulates loading a saved trip.
         *
         * @return configured trip
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
         * Simulates saving a trip.
         *
         * @param trip
         *            trip to save
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

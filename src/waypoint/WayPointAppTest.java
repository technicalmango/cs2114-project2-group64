
package waypoint;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import student.TestCase;

/**
 * Tests the WayPoint console application using scripted input. A fake file
 * manager prevents tests from reading or writing CSV files.
 *
 * @author Jovany
 * @version 10.12.2026
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
        String output = session("1\nFall Trip\n9\n");

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
        String output = session("oops\n999999999999999999\n-1\n10\n9\n");

        assertTrue(output.contains("Enter a whole number from 0 to 9."));
        assertEquals(0, files.saveCalls);
    }


    /**
     * Tests operations before a trip exists.
     */
    public void testWithoutTrip()
    {
        String output = session("2\n3\n4\n5\n6\n7\n8\n9\n");

        assertTrue(output.contains("Create a trip first."));
        assertEquals(0, files.saveCalls);
    }


    /**
     * Tests blank trip names and an empty trip.
     */
    public void testEmptyTrip()
    {
        String output = session("1\n\n  \nFall Trip\n4\n6\n9\n");

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
            "1\nRoad Trip\n" + "2\nRichmond\n" + "2\nBoston\n" + "5\n" + "9\n");

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
                + "3\n2\nWashington DC\n" + "9\n");

        assertEquals(3, files.saved.getDestinationCount());
        assertEquals("Richmond", files.saved.getDestination(0).getName());
        assertEquals("Washington DC", files.saved.getDestination(1).getName());
        assertEquals("Boston", files.saved.getDestination(2).getName());
    }


    /**
     * Tests removing a destination by its displayed position.
     */
    public void testRemoveDestination()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nRichmond\n" + "2\nWashington DC\n"
                + "2\nBoston\n" + "4\n2\n" + "9\n");

        assertTrue(output.contains("Removed: Washington DC"));
        assertEquals(2, files.saved.getDestinationCount());
        assertEquals("Richmond", files.saved.getDestination(0).getName());
        assertEquals("Boston", files.saved.getDestination(1).getName());
    }


    /**
     * Tests adding and viewing activities.
     */
    public void testAddAndViewActivities()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n" + "6\n1\nWhite House\n"
                + "6\n1\nLincoln Memorial\n" + "8\n1\n" + "9\n");

        Destination destination = files.saved.getDestination(0);

        assertEquals(2, destination.getActivityCount());
        assertTrue(destination.hasActivities());
        assertTrue(output.contains("Activities in Washington DC"));
        assertTrue(output.contains("White House"));
        assertTrue(output.contains("Lincoln Memorial"));
    }


    /**
     * Tests removing an activity.
     */
    public void testRemoveActivity()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n" + "6\n1\nWhite House\n"
                + "7\n1\n1\n" + "9\n");

        Destination destination = files.saved.getDestination(0);

        assertTrue(output.contains("Removed: White House"));
        assertEquals(0, destination.getActivityCount());
        assertFalse(destination.hasActivities());
    }


    /**
     * Tests removing an activity when none exist.
     */
    public void testRemoveFromEmptyActivities()
    {
        String output = session(
            "1\nRoad Trip\n" + "2\nWashington DC\n" + "7\n1\n" + "8\n1\n"
                + "9\n");

        assertTrue(output.contains("This destination has no activities."));
        assertTrue(output.contains("No activities planned."));
        assertEquals(0, files.saved.getDestination(0).getActivityCount());
    }


    /**
     * Tests canceling trip replacement.
     */
    public void testCancelTripReplacement()
    {
        session("1\nOriginal\n" + "2\nRichmond\n" + "1\nno\n" + "9\n");

        assertEquals("Original", files.saved.getName());
        assertEquals(1, files.saved.getDestinationCount());
    }


    /**
     * Tests confirming trip replacement.
     */
    public void testConfirmTripReplacement()
    {
        session(
            "1\nOriginal\n" + "2\nRichmond\n" + "1\nyes\nNew Trip\n" + "9\n");

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

        String output = session("2\nBoston\n9\n");

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

        String output = session("1\nRecovery\n9\n");

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
            session("1\nRetry Trip\n" + "9\n" + "2\nRichmond\n" + "9\n");

        assertTrue(output.contains("Your trip is still open."));
        assertTrue(output.contains("Trip saved."));
        assertEquals(2, files.saveCalls);
        assertNotNull(files.saved);
        assertEquals(1, files.saved.getDestinationCount());
    }


    /**
     * Tests exiting without saving after confirmation.
     */
    public void testExitWithoutSaving()
    {
        session("1\nUnsaved Trip\n" + "0\nyes\n");

        assertEquals(0, files.saveCalls);
        assertNull(files.saved);
    }


    /**
     * Tests canceling an exit without saving.
     */
    public void testCancelExit()
    {
        String output = session("1\nRoad Trip\n" + "0\nno\n" + "9\n");

        assertTrue(output.contains("Trip saved."));
        assertEquals(1, files.saveCalls);
        assertEquals("Road Trip", files.saved.getName());
    }


    /**
     * Runs the application with scripted input.
     *
     * @param input
     *            the simulated user input
     * @return the application's console output
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
     * Simulates file operations without accessing real CSV files.
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
         * Simulates saving a trip.
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

package waypoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import student.TestCase;

public class TripFileManagerTest
    extends TestCase
{

    private Path file;
    private TripFileManager manager;

    public void setUp()
        throws IOException
    {
        file = Files.createTempFile("waypoint-test-", ".csv");
        Files.delete(file);
        manager = new TripFileManager(file);
    }


    public void tearDown()
        throws IOException
    {
        Files.deleteIfExists(file);
    }


    public void testSaveAndLoadTrip()
        throws IOException
    {
        RoadTrip trip = new RoadTrip("Summer Trip");

        Destination richmond = new Destination("Richmond");
        richmond.addActivity(new Activity("Museum"));
        richmond.addActivity(new Activity("Dinner"));

        Destination dc = new Destination("Washington DC");
        dc.addActivity(new Activity("Monuments"));

        trip.addDestination(richmond);
        trip.addDestination(dc);

        manager.saveTrip(trip);

        assertTrue(Files.exists(file));

        RoadTrip loaded = manager.loadTrip();

        assertNotNull(loaded);
        assertEquals("Summer Trip", loaded.getName());
        assertEquals(2, loaded.getDestinationCount());

        Destination first = loaded.getDestination(0);
        Destination second = loaded.getDestination(1);

        assertEquals("Richmond", first.getName());
        assertEquals("Washington DC", second.getName());

        assertEquals(2, first.getActivityCount());
        assertEquals("Museum", first.getActivity(0).getName());
        assertEquals("Dinner", first.getActivity(1).getName());

        assertEquals(1, second.getActivityCount());
        assertEquals("Monuments", second.getActivity(0).getName());
    }


    public void testEmptyTrip()
        throws IOException
    {
        RoadTrip trip = new RoadTrip("Empty Trip");

        manager.saveTrip(trip);

        RoadTrip loaded = manager.loadTrip();

        assertNotNull(loaded);
        assertEquals("Empty Trip", loaded.getName());
        assertEquals(0, loaded.getDestinationCount());
    }


    public void testMissingFile()
        throws IOException
    {
        assertFalse(Files.exists(file));
        assertNull(manager.loadTrip());
    }


    public void testSaveNullTrip()
        throws IOException
    {
        try
        {
            manager.saveTrip(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected exception
        }

        assertFalse(Files.exists(file));
    }


    public void testMalformedCSV()
        throws IOException
    {
        String content = "TRIP,\"Summer Trip\"\n" + "DESTINATION,\"Richmond\"\n"
            + "ACTIVITY,\"Unclosed\n";

        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        try
        {
            manager.loadTrip();
            fail("Expected IOException");
        }
        catch (IOException e)
        {
            // Expected exception
        }
    }


    public void testInvalidRecordOrder()
        throws IOException
    {
        String content = "TRIP,\"Summer Trip\"\n" + "ACTIVITY,\"Museum\"\n";

        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        try
        {
            manager.loadTrip();
            fail("Expected IOException");
        }
        catch (IOException e)
        {
            // Expected exception
        }
    }


    public void testInvalidSavePreservesFile()
        throws IOException
    {
        manager.saveTrip(new RoadTrip("Original Trip"));

        try
        {
            manager.saveTrip(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected exception
        }

        assertEquals("Original Trip", manager.loadTrip().getName());
    }


    public void testUnwritableDestination()
        throws IOException
    {
        Path directory = Files.createTempDirectory("waypoint-directory-");

        try
        {
            TripFileManager invalidManager = new TripFileManager(directory);

            try
            {
                invalidManager.saveTrip(new RoadTrip("Trip"));
                fail("Expected IOException");
            }
            catch (IOException e)
            {
                // Expected exception
            }
        }
        finally
        {
            Files.deleteIfExists(directory);
        }
    }


    public void testNullPath()
    {
        try
        {
            new TripFileManager(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected exception
        }
    }


    public void testSpecialCharacters()
        throws IOException
    {
        RoadTrip trip = new RoadTrip("Fall, Trip");

        Destination destination = new Destination("Washington \"DC\"");

        destination.addActivity(new Activity("Dinner, then a show"));

        trip.addDestination(destination);

        manager.saveTrip(trip);

        RoadTrip loaded = manager.loadTrip();

        assertEquals("Fall, Trip", loaded.getName());
        assertEquals("Washington \"DC\"", loaded.getDestination(0).getName());

        assertEquals(
            "Dinner, then a show",
            loaded.getDestination(0).getActivity(0).getName());
    }


    public void testMultilineNames()
        throws IOException
    {
        RoadTrip trip = new RoadTrip("Summer\nRoad Trip");

        Destination destination = new Destination("Richmond");
        destination.addActivity(new Activity("Lunch\nand Dinner"));
        trip.addDestination(destination);

        manager.saveTrip(trip);

        RoadTrip loaded = manager.loadTrip();

        assertEquals("Summer\nRoad Trip", loaded.getName());
        assertEquals(
            "Lunch\nand Dinner",
            loaded.getDestination(0).getActivity(0).getName());
    }
}

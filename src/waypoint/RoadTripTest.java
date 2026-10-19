
package waypoint;

import java.time.LocalDate;
import java.time.LocalTime;
import student.TestCase;

/**
 * Tests the RoadTrip class using student.TestCase.
 *
 * @author Jovany
 * @version October 2026
 */
public class RoadTripTest
    extends TestCase
{
    private RoadTrip trip;

    /**
     * Sets up a new trip before each test.
     */
    public void setUp()
    {
        trip = new RoadTrip("  Fall Trip  ");
    }


    /**
     * Tests the constructor and initial trip information.
     */
    public void testConstructor()
    {
        assertEquals("Fall Trip", trip.getName());
        assertTrue(trip.isEmpty());
        assertEquals(0, trip.getDestinationCount());
        assertEquals(0, trip.getTotalActivities());
        assertNull(trip.getMostActiveDestination());
        assertTrue(trip.toString().contains("No destinations"));
    }


    /**
     * Tests rejecting invalid trip names.
     */
    public void testInvalidConstructor()
    {
        Exception exception = null;

        try
        {
            new RoadTrip(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            new RoadTrip("  ");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }


    /**
     * Tests changing a trip's name.
     */
    public void testSetName()
    {
        trip.setName("Spring Trip");

        assertEquals("Spring Trip", trip.getName());
    }


    /**
     * Tests rejecting invalid name changes.
     */
    public void testSetNameInvalid()
    {
        Exception exception = null;

        try
        {
            trip.setName(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            trip.setName("\t");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Fall Trip", trip.getName());
    }


    /**
     * Tests adding destinations to a trip.
     */
    public void testAddDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);

        assertEquals(2, trip.getDestinationCount());
        assertFalse(trip.isEmpty());

        assertSame(a, trip.getDestination(0));
        assertSame(b, trip.getDestination(1));
    }


    /**
     * Tests rejecting a null destination.
     */
    public void testAddDestinationNull()
    {
        Exception exception = null;

        try
        {
            trip.addDestination(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(trip.isEmpty());
    }


    /**
     * Tests inserting destinations at different positions.
     */
    public void testInsertDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("DC");
        Destination c = new Destination("Boston");
        Destination duplicate = new Destination("Richmond");

        trip.insertDestination(0, a);
        trip.addDestination(c);
        trip.insertDestination(1, b);
        trip.insertDestination(3, duplicate);

        assertEquals(4, trip.getDestinationCount());
        assertSame(a, trip.getDestination(0));
        assertSame(b, trip.getDestination(1));
        assertSame(c, trip.getDestination(2));
        assertSame(duplicate, trip.getDestination(3));
    }


    /**
     * Tests rejecting invalid insertion positions and null values.
     */
    public void testInsertDestinationInvalid()
    {
        Destination a = new Destination("Richmond");
        trip.addDestination(a);

        Exception exception = null;

        try
        {
            trip.insertDestination(-1, a);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            trip.insertDestination(2, a);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            trip.insertDestination(0, null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(1, trip.getDestinationCount());
        assertSame(a, trip.getDestination(0));
    }


    /**
     * Tests getting destinations by index.
     */
    public void testGetDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);

        assertSame(a, trip.getDestination(0));
        assertSame(b, trip.getDestination(1));
    }


    /**
     * Tests rejecting invalid destination indices.
     */
    public void testGetDestinationInvalid()
    {
        trip.addDestination(new Destination("Richmond"));

        Exception exception = null;

        try
        {
            trip.getDestination(-1);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            trip.getDestination(1);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }


    /**
     * Tests removing destinations from different positions.
     */
    public void testRemoveDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("DC");
        Destination c = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);
        trip.addDestination(c);

        assertSame(b, trip.removeDestination(1));
        assertEquals(2, trip.getDestinationCount());
        assertSame(c, trip.getDestination(1));

        assertSame(c, trip.removeDestination(1));
        assertSame(a, trip.removeDestination(0));

        assertTrue(trip.isEmpty());
        assertEquals(0, trip.getDestinationCount());
    }


    /**
     * Tests removing destinations using invalid indices.
     */
    public void testRemoveDestinationInvalid()
    {
        trip.addDestination(new Destination("Richmond"));

        Exception exception = null;

        try
        {
            trip.removeDestination(-1);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            trip.removeDestination(1);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(1, trip.getDestinationCount());
    }


    /**
     * Tests counting activities across destinations.
     */
    public void testGetTotalActivities()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("DC");

        trip.addDestination(a);
        trip.addDestination(b);

        a.addActivity(new Activity("Museum"));
        a.addActivity(new Activity("Park"));
        b.addActivity(new Activity("Lunch"));

        assertEquals(3, trip.getTotalActivities());

        a.removeActivity(0);

        assertEquals(2, trip.getTotalActivities());

        trip.removeDestination(1);

        assertEquals(1, trip.getTotalActivities());
    }


    /**
     * Tests finding the destination with the most activities.
     */
    public void testGetMostActiveDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("DC");

        trip.addDestination(a);
        trip.addDestination(b);

        assertEquals(a, trip.getMostActiveDestination());

        a.addActivity(new Activity("Museum"));
        a.addActivity(new Activity("Park"));
        b.addActivity(new Activity("Lunch"));

        assertSame(a, trip.getMostActiveDestination());

        b.addActivity(new Activity("Walk"));

        // A tie should return the first destination.
        assertSame(a, trip.getMostActiveDestination());

        a.removeActivity(0);

        assertSame(b, trip.getMostActiveDestination());
    }


    /**
     * Tests the string representation of a trip.
     */
    public void testToString()
    {
        trip.setName("Spring Trip");

        trip.addDestination(new Destination("Richmond"));
        trip.addDestination(new Destination("DC"));
        trip.addDestination(new Destination("Boston"));

        String display = trip.toString();

        assertTrue(display.contains("Trip: Spring Trip"));
        assertTrue(display.contains("1. Richmond\n2. DC\n3. Boston"));
    }


    /**
     * Tests summary values after removing all destinations.
     */
    public void testEmptyTripAfterRemoval()
    {
        Destination destination = new Destination("Richmond");

        trip.addDestination(destination);
        destination.addActivity(new Activity("Museum"));

        trip.removeDestination(0);

        assertTrue(trip.isEmpty());
        assertEquals(0, trip.getTotalActivities());
        assertNull(trip.getMostActiveDestination());
    }


    public void testGetTotalEstimatedCost()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);

        // An empty trip has no activity costs.
        assertEquals(0.0, trip.getTotalEstimatedCost(), 0.001);

        // Set destination dates before adding activities.
        a.setStayDates(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 22));

        b.setStayDates(LocalDate.of(2026, 10, 23), LocalDate.of(2026, 10, 28));

        // Add an activity to Richmond.
        a.addActivity(
            new Activity(
                "Museum",
                LocalDate.of(2026, 10, 21),
                LocalTime.of(10, 0),
                25.0));

        // Add an activity to Boston.
        b.addActivity(
            new Activity(
                "Tour",
                LocalDate.of(2026, 10, 24),
                LocalTime.of(9, 0),
                30.0));

        // Total estimated cost should be $55.
        assertEquals(55.0, trip.getTotalEstimatedCost(), 0.001);

        // Removing Boston should remove its activity cost.
        trip.removeDestination(1);

        assertEquals(25.0, trip.getTotalEstimatedCost(), 0.001);
    }


    public void testGetLongestStayDestination()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);

        assertNull(trip.getLongestStayDestination());

        a.setStayDates(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 22));

        b.setStayDates(LocalDate.of(2026, 10, 23), LocalDate.of(2026, 10, 28));

        assertSame(b, trip.getLongestStayDestination());

        trip.removeDestination(1);

        assertSame(a, trip.getLongestStayDestination());
    }


    public void testGetTripDuration()
    {
        Destination a = new Destination("Richmond");
        Destination b = new Destination("Boston");

        trip.addDestination(a);
        trip.addDestination(b);

        assertEquals(0L, trip.getTripDuration());

        a.setStayDates(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 22));

        b.setStayDates(LocalDate.of(2026, 10, 23), LocalDate.of(2026, 10, 28));

        // October 20 through October 28, inclusive = 9 days.
        assertEquals(9L, trip.getTripDuration());

        trip.removeDestination(1);

        // October 20 through October 22, inclusive = 3 days.
        assertEquals(3L, trip.getTripDuration());
    }
}

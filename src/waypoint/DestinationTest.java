package waypoint;

import static org.junit.Assert.*;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/** Tests destination names, activity order, and optional stay dates. */
public class DestinationTest
{
    private Destination d;
    private Activity h;
    private Activity s;

    // JUnit 4 runs this before EVERY test because of @Before.
    @Before
    public void setUp()
    {
        d = new Destination("d");
        h = new Activity("h");
        s = new Activity("s");
    }

    @Test
    public void testCon()
    {
        assertEquals("d", d.getName());
        assertEquals(0, d.getActivityCount());
        assertFalse(d.hasActivities());
    }

    @Test
    public void testSetName()
    {
        d.setName(" r ");
        assertEquals("r", d.getName());
    }

    @Test
    public void testConstructorTrims()
    {
        assertEquals("a", new Destination(" a ").getName());
    }

    @Test
    public void testInvalidConsNames()
    {
        String[] invalidNames = { null, "", " " };
        for (String name : invalidNames)
        {
            expectException(IllegalArgumentException.class,
                () -> new Destination(name));
        }
    }

    @Test
    public void testInvalidSetName()
    {
        String[] invalidNames = { null, "", " " };
        for (String name : invalidNames)
        {
            expectException(IllegalArgumentException.class,
                () -> d.setName(name));
            assertEquals("d", d.getName());
        }
    }

    @Test
    public void testAddAndGetActivities()
    {
        d.addActivity(h);
        d.addActivity(s);
        assertEquals(2, d.getActivityCount());
        assertTrue(d.hasActivities());

        // addToEnd means the FIRST activity added is at index 0.
        assertSame(h, d.getActivity(0));
        assertSame(s, d.getActivity(1));
    }

    @Test
    public void testNullAdd()
    {
        expectException(IllegalArgumentException.class,
            () -> d.addActivity(null));
        assertEquals(0, d.getActivityCount());
    }

    @Test
    public void testRemoveActivity()
    {
        d.addActivity(h);
        d.addActivity(s);
        assertSame(h, d.removeActivity(0));
        assertEquals(1, d.getActivityCount());
        assertSame(s, d.getActivity(0));

        assertSame(s, d.removeActivity(0));
        assertEquals(0, d.getActivityCount());
        assertFalse(d.hasActivities());
    }

    @Test
    public void testInvalidActivityPositions()
    {
        d.addActivity(h);
        expectException(IndexOutOfBoundsException.class,
            () -> d.getActivity(-1));
        expectException(IndexOutOfBoundsException.class,
            () -> d.getActivity(1));
        expectException(IndexOutOfBoundsException.class,
            () -> d.removeActivity(-1));
        expectException(IndexOutOfBoundsException.class,
            () -> d.removeActivity(1));

        // Invalid operations must preserve the existing activity.
        assertEquals(1, d.getActivityCount());
        assertSame(h, d.getActivity(0));
    }

    @Test
    public void testEquals()
    {
        assertTrue(d.equals(d));
        assertTrue(d.equals(new Destination("d")));
        assertFalse(d.equals(new Destination("c")));
        assertFalse(d.equals(new Destination("D")));
        assertFalse(d.equals(null));
        assertFalse(d.equals("d"));
    }

    @Test
    public void testHashCode()
    {
        assertEquals(d.hashCode(), new Destination("d").hashCode());
    }

    @Test
    public void testToString()
    {
        assertEquals("d (0 activities)", d.toString());
        d.addActivity(h);
        assertEquals("d (1 activities)", d.toString());
    }

    // NEW: dates are optional until the user supplies them.
    @Test
    public void testDefaultStayDates()
    {
        assertNull(d.getArrivalDate());
        assertNull(d.getDepartureDate());
        assertFalse(d.hasStayDates());
        assertEquals(0L, d.getStayDays());
    }

    @Test
    public void testSetArrivalThenDeparture()
    {
        LocalDate arrival = LocalDate.of(2026, 11, 1);
        LocalDate departure = LocalDate.of(2026, 11, 3);

        d.setArrivalDate(arrival);
        assertEquals(arrival, d.getArrivalDate());
        assertFalse(d.hasStayDates());
        assertEquals(0L, d.getStayDays());

        d.setDepartureDate(departure);
        assertEquals(departure, d.getDepartureDate());
        assertTrue(d.hasStayDates());
        assertEquals(3L, d.getStayDays());
    }

    @Test
    public void testSetDepartureThenArrival()
    {
        LocalDate arrival = LocalDate.of(2026, 11, 1);
        LocalDate departure = LocalDate.of(2026, 11, 3);

        d.setDepartureDate(departure);
        assertEquals(departure, d.getDepartureDate());
        assertFalse(d.hasStayDates());
        assertEquals(0L, d.getStayDays());

        d.setArrivalDate(arrival);
        assertTrue(d.hasStayDates());
        assertEquals(arrival, d.getArrivalDate());
    }

    @Test
    public void testNullIndividualDates()
    {
        expectException(IllegalArgumentException.class,
            () -> d.setArrivalDate(null));
        expectException(IllegalArgumentException.class,
            () -> d.setDepartureDate(null));
        assertNull(d.getArrivalDate());
        assertNull(d.getDepartureDate());
    }

    @Test
    public void testInvalidIndividualDateUpdates()
    {
        LocalDate arrival = LocalDate.of(2026, 11, 1);
        LocalDate departure = LocalDate.of(2026, 11, 3);
        d.setStayDates(arrival, departure);

        expectException(IllegalArgumentException.class,
            () -> d.setArrivalDate(departure.plusDays(1)));
        expectException(IllegalArgumentException.class,
            () -> d.setDepartureDate(arrival.minusDays(1)));
        expectException(IllegalArgumentException.class,
            () -> d.setArrivalDate(null));
        expectException(IllegalArgumentException.class,
            () -> d.setDepartureDate(null));

        // Rejected edits must leave both original dates unchanged.
        assertEquals(arrival, d.getArrivalDate());
        assertEquals(departure, d.getDepartureDate());
    }

    @Test
    public void testSetStayDates()
    {
        LocalDate arrival = LocalDate.of(2026, 11, 1);
        LocalDate departure = LocalDate.of(2026, 11, 3);
        d.setStayDates(arrival, departure);

        assertEquals(arrival, d.getArrivalDate());
        assertEquals(departure, d.getDepartureDate());
        assertTrue(d.hasStayDates());
        assertEquals(3L, d.getStayDays());

        // Updating both dates together can move the entire stay later.
        d.setStayDates(arrival.plusDays(10), departure.plusDays(10));
        assertEquals(arrival.plusDays(10), d.getArrivalDate());
        assertEquals(departure.plusDays(10), d.getDepartureDate());
    }

    @Test
    public void testInvalidStayDatesPreserveExistingStay()
    {
        LocalDate arrival = LocalDate.of(2026, 11, 1);
        LocalDate departure = LocalDate.of(2026, 11, 3);
        d.setStayDates(arrival, departure);

        LocalDate[][] invalidRanges = {
            { null, departure },
            { arrival, null },
            { null, null },
            { departure, arrival }
        };

        for (LocalDate[] range : invalidRanges)
        {
            expectException(IllegalArgumentException.class,
                () -> d.setStayDates(range[0], range[1]));
            assertEquals(arrival, d.getArrivalDate());
            assertEquals(departure, d.getDepartureDate());
        }
    }

    @Test
    public void testSameDayStay()
    {
        LocalDate date = LocalDate.of(2026, 11, 1);
        d.setStayDates(date, date);
        assertTrue(d.hasStayDates());
        assertEquals(1L, d.getStayDays());

        // Individual setters also allow equal arrival and departure dates.
        d.setArrivalDate(date);
        d.setDepartureDate(date);
        assertEquals(1L, d.getStayDays());
    }

    @Test
    public void testStayAcrossMonthBoundary()
    {
        // Leap day must count: February 28, February 29, and March 1.
        d.setStayDates(LocalDate.of(2028, 2, 28), LocalDate.of(2028, 3, 1));
        assertEquals(3L, d.getStayDays());
    }

    /** Checks exceptions without requiring JUnit 4.13's assertThrows method. */
    private void expectException(
        Class<? extends RuntimeException> expected, Runnable operation)
    {
        try
        {
            operation.run();
            fail("Expected " + expected.getSimpleName());
        }
        catch (RuntimeException exception)
        {
            assertTrue("Unexpected exception: " + exception,
                expected.isInstance(exception));
        }
    }
}

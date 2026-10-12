package waypoint;

import student.TestCase;

public class DestinationTest
    extends TestCase
{
    private Destination destination;
    private Activity activity1;
    private Activity activity2;

    /**
     * Sets up the test fixtures.
     */
    public void setUp()
    {
        destination = new Destination("Washington D.C.");
        activity1 = new Activity("White House");
        activity2 = new Activity("Lincoln Memorial");
    }


    /**
     * Tests the constructor and getName method.
     */
    public void testConstructor()
    {
        assertEquals("Washington D.C.", destination.getName());
        assertEquals(0, destination.getActivityCount());
        assertFalse(destination.hasActivities());
    }


    /**
     * Tests setting a valid destination name.
     */
    public void testSetName()
    {
        destination.setName("New York");

        assertEquals("New York", destination.getName());

        destination.setName("  Boston  ");

        assertEquals("Boston", destination.getName());
    }


    /**
     * Tests invalid destination names.
     */
    public void testInvalidName()
    {
        Exception exception = null;

        try
        {
            destination.setName(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            destination.setName("   ");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        assertEquals("Washington D.C.", destination.getName());
    }


    /**
     * Tests creating a destination with an invalid name.
     */
    public void testInvalidConstructor()
    {
        Exception exception = null;

        try
        {
            new Destination(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);

        exception = null;

        try
        {
            new Destination("");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
    }


    /**
     * Tests adding activities to a destination.
     */
    public void testAddActivity()
    {
        destination.addActivity(activity1);

        assertEquals(1, destination.getActivityCount());
        assertTrue(destination.hasActivities());
        assertEquals(activity1, destination.getActivity(0));

        destination.addActivity(activity2);

        assertEquals(2, destination.getActivityCount());
    }


    /**
     * Tests adding a null activity.
     */
    public void testAddNullActivity()
    {
        Exception exception = null;

        try
        {
            destination.addActivity(null);
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(0, destination.getActivityCount());
    }


    /**
     * Tests retrieving activities by index.
     */
    public void testGetActivity()
    {
        destination.addActivity(activity1);
        destination.addActivity(activity2);

        assertEquals(activity2, destination.getActivity(0));
        assertEquals(activity1, destination.getActivity(1));
    }


    /**
     * Tests removing activities by index.
     */
    public void testRemoveActivity()
    {
        destination.addActivity(activity1);
        destination.addActivity(activity2);

        Activity removed = destination.removeActivity(0);

        assertEquals(activity2, removed);
        assertEquals(1, destination.getActivityCount());
        assertEquals(activity1, destination.getActivity(0));

        destination.removeActivity(0);

        assertEquals(0, destination.getActivityCount());
        assertFalse(destination.hasActivities());
    }


    /**
     * Tests activity counts and whether activities exist.
     */
    public void testHasActivities()
    {
        assertFalse(destination.hasActivities());
        assertEquals(0, destination.getActivityCount());

        destination.addActivity(activity1);

        assertTrue(destination.hasActivities());
        assertEquals(1, destination.getActivityCount());

        destination.removeActivity(0);

        assertFalse(destination.hasActivities());
        assertEquals(0, destination.getActivityCount());
    }


    /**
     * Tests equality between destinations.
     */
    public void testEquals()
    {
        Destination same = new Destination("Washington D.C.");
        Destination different = new Destination("Boston");

        assertTrue(destination.equals(destination));
        assertTrue(destination.equals(same));
        assertFalse(destination.equals(different));
        assertFalse(destination.equals(null));
        assertFalse(destination.equals("Washington D.C."));

        same.addActivity(activity1);

        assertTrue(destination.equals(same));
    }


    /**
     * Tests that equal destinations have equal hash codes.
     */
    public void testHashCode()
    {
        Destination same = new Destination("Washington D.C.");

        assertEquals(destination.hashCode(), same.hashCode());
        assertEquals("Washington D.C.".hashCode(), destination.hashCode());
    }


    /**
     * Tests the string representation of a destination.
     */
    public void testToString()
    {
        assertEquals("Washington D.C. (0 activities)", destination.toString());

        destination.addActivity(activity1);

        assertEquals("Washington D.C. (1 activities)", destination.toString());

        destination.addActivity(activity2);

        assertEquals("Washington D.C. (2 activities)", destination.toString());
    }
}

package waypoint;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests normal and edge cases for Activity. Name validation belongs to
 * InputValidator, not Activity.
 *
 * @author Alexander Kyle
 * @version October 2026
 */
public class ActivityTest
{

    /** Tests the constructor and getter. */
    @Test
    public void testConstructorAndGetName()
    {
        assertEquals("Hiking", new Activity("Hiking").getName());
    }


    /** Tests names before external validation. */
    @Test
    public void testConstructorAndGetNameEdgeCases()
    {
        assertNull(new Activity(null).getName());
        assertEquals("", new Activity("").getName());
        assertEquals("   ", new Activity("   ").getName());
    }


    /** Tests changing the name. */
    @Test
    public void testSetName()
    {
        Activity activity = new Activity("Hiking");
        activity.setName("Swimming");
        assertEquals("Swimming", activity.getName());
    }


    /** Tests updates before external validation. */
    @Test
    public void testSetNameEdgeCases()
    {
        Activity activity = new Activity("Hiking");
        activity.setName(null);
        assertNull(activity.getName());
        activity.setName("");
        assertEquals("", activity.getName());
        activity.setName("   ");
        assertEquals("   ", activity.getName());
    }


    /** Tests equality for matching names. */
    @Test
    public void testEquals()
    {
        Activity activity = new Activity("Hiking");
        Activity matching = new Activity("Hiking");
        assertTrue(activity.equals(activity));
        assertTrue(activity.equals(matching));
        assertTrue(matching.equals(activity));
    }


    /** Tests different names, null, and another type. */
    @Test
    public void testEqualsEdgeCases()
    {
        Activity activity = new Activity("Hiking");
        assertFalse(activity.equals(new Activity("Swimming")));
        assertFalse(activity.equals(new Activity("hiking")));
        assertFalse(activity.equals(null));
        assertFalse(activity.equals("Hiking"));
        assertFalse(activity.equals(new Activity(null)));
        assertFalse(new Activity(null).equals(activity));
        assertTrue(new Activity(null).equals(new Activity(null)));
    }


    /** Tests equality after a name change. */
    @Test
    public void testEqualsAfterNameChange()
    {
        Activity activity = new Activity("Hiking");
        Activity matching = new Activity("Hiking");
        activity.setName("Swimming");
        assertFalse(activity.equals(matching));
        matching.setName("Swimming");
        assertTrue(activity.equals(matching));
    }


    /** Tests hash codes for equal activities. */
    @Test
    public void testHashCode()
    {
        Activity first = new Activity("Hiking");
        Activity second = new Activity("Hiking");
        assertEquals(first.hashCode(), second.hashCode());
        first.setName("Swimming");
        second.setName("Swimming");
        assertEquals(first.hashCode(), second.hashCode());
    }


    /** Tests hash codes for null and empty names. */
    @Test
    public void testHashCodeEdgeCases()
    {
        assertEquals(
            new Activity(null).hashCode(),
            new Activity(null).hashCode());
        assertEquals(new Activity("").hashCode(), new Activity("").hashCode());
    }


    /** Tests displaying the current name. */
    @Test
    public void testToString()
    {
        Activity activity = new Activity("Hiking");
        assertEquals("Hiking", activity.toString());
        activity.setName("Swimming");
        assertEquals("Swimming", activity.toString());
    }


    /** Tests displaying empty and null stored names. */
    @Test
    public void testToStringEdgeCases()
    {
        assertEquals("", new Activity("").toString());
        assertNull(new Activity(null).toString());
    }
}

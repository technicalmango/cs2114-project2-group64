package waypoint;

import static org.junit.Assert.*;
import org.junit.Test;

public class DestinationTest
{
    private static final int f = 0;
    private Destination d;
    private Activity h;
    private Activity s;
    
    public void setUp() {
        d = new Destination("d");
        h = new Activity("h");
        s = new Activity("s");
        
    }
    //~ Fields ................................................................

    //~ Constructors ..........................................................

    //~Public  Methods ........................................................
    public void testCon() {
        assertEquals("d", d.getName());
        assertEquals(0, d.getActivityCount());
        assertFalse(d.hasActivities());
    }
    
    public void testSetName()
    {
        d.setName(" r ");
        assertEquals("r", d.getName());
    }
    
    public void testConstuctorTrims()
    {
        Destination o = new Destination(" a ");
        assertEquals("a", o.getName());
    }
    
    public void testInvalidConsNames()
    {
        String[] invalidNames = { null, "", " "};
        for (String name : invalidNames)
        {
            try 
            {
                new Destination(name);
                fail("An invalid name should throw a e");
            
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("Destination name cannot be null or blank",
                    e.getMessage());
            }
            
        }
    }
    
    public void testInvalidSetName()
    {
        String[] invalidNames = { null, "", " "};
        for (String name : invalidNames)
        {
            try 
            {
                d.setName(name);
                fail("An invalid name should throw a e");
            
            }
            catch (IllegalArgumentException e)
            {
                assertEquals("d", d.getName());
            }
            
        }
    }
    
    public void testAddAndGetActivities()
    {
        d.addActivity(h);
        d.addActivity(s);
        assertEquals(2, d.getActivityCount());
        assertSame(h, d.getActivity(f));
        assertSame(s, d.getActivity(f + 1));
        
    }
    public void testNullAdd()
    {
            try 
            {
                d.addActivity(null);
                fail("A null activity should throw an e");
            
            }
            catch (IllegalArgumentException e)
            {
                assertEquals(0, d.getActivityCount());
            }
            
        
    }
    
    public void testRemoveActivity()
    {
        d.addActivity(h);
        d.addActivity(s);
        assertSame(h, d.removeActivity(f));
        assertEquals(1, d.getActivityCount());
        
        assertSame(s, d.getActivity(f));
    }
    
    public void testEquals()
    {
        Destination p = new Destination("d");
        Destination c = new Destination("c");
        Destination q = new Destination("D");
        
        assertTrue(d.equals(d));
        assertTrue(d.equals(p));
        assertFalse(d.equals(c));
        assertFalse(d.equals(q));
    }
    
    public void testHashCode()
    {
        Destination b = new Destination("d");
        
        assertEquals(d.hashCode(), b.hashCode());
    }
    
    public void testToString()
    {
        assertEquals("d (0 activities)", d.toString());
        d.addActivity(h);
        assertEquals("d (1 activities)", d.toString());
    }
    
    
    
    
    
    
    
    
}

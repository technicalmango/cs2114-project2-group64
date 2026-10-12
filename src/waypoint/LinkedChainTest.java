package waypoint;

import student.TestCase;

/*
 * These tests are only for the add(int index, T entry) and remove(int index) as
 * those were added to the LinkedChain file
 */

public class LinkedChainTest
    extends TestCase
{
    private LinkedChain<String> chain;

    public void setUp()
    {
        chain = new LinkedChain<String>();
    }


    // Testing to see if destination gets added to correct index
    public void testAddAtEmptyIndex()
    {
        assertTrue(chain.add(0, "Richmond"));
        assertEquals("Richmond", chain.get(0));
        assertEquals(1, chain.size());
    }


    // Test to see if adding to beginning works correctly
    public void testAddAtBeginning()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("Washington DC");

        assertTrue(chain.add(0, "Blacksburg"));

        assertEquals(3, chain.size());
        assertEquals("Blacksburg", chain.get(0));
        assertEquals("Richmond", chain.get(1));
        assertEquals("Washington DC", chain.get(2));
    }


    // Test inserting destination in the middle of LinkedChain
    public void testAddAtMiddle()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("New York");

        assertTrue(chain.add(1, "Washington DC"));

        assertEquals(3, chain.size());
        assertEquals("Richmond", chain.get(0));
        assertEquals("Washington DC", chain.get(1));
        assertEquals("New York", chain.get(2));
    }


    // Test inserting destination in the end
    public void testAddAtEnd()
    {
        chain.addToEnd("Richmond");

        assertTrue(chain.add(1, "Washington DC"));

        assertEquals(2, chain.size());
        assertEquals("Richmond", chain.get(0));
        assertEquals("Washington DC", chain.get(1));
    }


    // Test inserting duplicate values
    public void testAddDuplicate()
    {
        chain.addToEnd("Richmond");
        chain.add(1, "Richmond");

        assertEquals(2, chain.size());
        assertEquals("Richmond", chain.get(0));
        assertEquals("Richmond", chain.get(1));
    }


    // Test inserting at a negative index
    public void testAddNegativeIndex()
    {
        try
        {
            chain.add(-1, "Richmond");
            fail("Expected IndexOutOfBoundsException.");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(0, chain.size());
        }
    }


    // Test inserting beyond the end
    public void testAddIndexTooLarge()
    {
        try
        {
            chain.add(1, "Richmond");
            fail("Expected IndexOutOfBoundsException.");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(0, chain.size());
        }
    }


    // Test inserting a null value
    public void testAddNull()
    {
        try
        {
            chain.add(0, null);
            fail("Expected exception for null entry.");
        }
        catch (IllegalArgumentException | NullPointerException e)
        {
            assertEquals(0, chain.size());
        }
    }


    // Test removing the first element
    public void testRemoveAtBeginning()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("Washington DC");
        chain.addToEnd("New York");

        assertEquals("Richmond", chain.remove(0));

        assertEquals(2, chain.size());
        assertEquals("Washington DC", chain.get(0));
        assertEquals("New York", chain.get(1));
    }


    // Test removing a middle element
    public void testRemoveAtMiddle()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("Washington DC");
        chain.addToEnd("New York");

        assertEquals("Washington DC", chain.remove(1));

        assertEquals(2, chain.size());
        assertEquals("Richmond", chain.get(0));
        assertEquals("New York", chain.get(1));
    }


    // Test removing the last element
    public void testRemoveAtEnd()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("Washington DC");

        assertEquals("Washington DC", chain.remove(1));

        assertEquals(1, chain.size());
        assertEquals("Richmond", chain.get(0));
    }


    // Test removing a specific duplicate by index
    public void testRemoveDuplicateByIndex()
    {
        chain.addToEnd("Richmond");
        chain.addToEnd("Richmond");
        chain.addToEnd("New York");

        assertEquals("Richmond", chain.remove(1));

        assertEquals(2, chain.size());
        assertEquals("Richmond", chain.get(0));
        assertEquals("New York", chain.get(1));
    }


    // Test removing from an empty chain
    public void testRemoveFromEmpty()
    {
        try
        {
            chain.remove(0);
            fail("Expected IndexOutOfBoundsException.");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertTrue(chain.isEmpty());
        }
    }


    // Test removing at a negative index
    public void testRemoveNegativeIndex()
    {
        chain.addToEnd("Richmond");

        try
        {
            chain.remove(-1);
            fail("Expected IndexOutOfBoundsException.");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(1, chain.size());
        }
    }


    // Test removing beyond the last index
    public void testRemoveIndexTooLarge()
    {
        chain.addToEnd("Richmond");

        try
        {
            chain.remove(1);
            fail("Expected IndexOutOfBoundsException.");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(1, chain.size());
            assertEquals("Richmond", chain.get(0));
        }
    }
}

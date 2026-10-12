package waypoint;

import java.time.LocalDate;
import student.TestCase;

public class InputValidatorTest
    extends TestCase
{
    public void testIsValidName()
    {
        assertTrue(InputValidator.isValidName("Richmond"));
        assertTrue(InputValidator.isValidName("New York"));

        assertFalse(InputValidator.isValidName(null));
        assertFalse(InputValidator.isValidName(""));
        assertFalse(InputValidator.isValidName("   "));
    }


    public void testIsValidMenuChoice()
    {
        assertTrue(InputValidator.isValidMenuChoice(2, 1, 5));
        assertTrue(InputValidator.isValidMenuChoice(1, 1, 5));
        assertTrue(InputValidator.isValidMenuChoice(5, 1, 5));

        assertFalse(InputValidator.isValidMenuChoice(0, 1, 5));
        assertFalse(InputValidator.isValidMenuChoice(6, 1, 5));
        assertFalse(InputValidator.isValidMenuChoice(2, 5, 1));
    }


    public void testIsValidExistingPosition()
    {
        assertTrue(InputValidator.isValidExistingPosition(0, 3));
        assertTrue(InputValidator.isValidExistingPosition(2, 3));

        assertFalse(InputValidator.isValidExistingPosition(-1, 3));
        assertFalse(InputValidator.isValidExistingPosition(3, 3));
        assertFalse(InputValidator.isValidExistingPosition(0, 0));
        assertFalse(InputValidator.isValidExistingPosition(0, -1));
    }


    public void testIsValidInsertionPosition()
    {
        assertTrue(InputValidator.isValidInsertionPosition(0, 0));
        assertTrue(InputValidator.isValidInsertionPosition(0, 3));
        assertTrue(InputValidator.isValidInsertionPosition(3, 3));

        assertFalse(InputValidator.isValidInsertionPosition(-1, 3));
        assertFalse(InputValidator.isValidInsertionPosition(4, 3));
        assertFalse(InputValidator.isValidInsertionPosition(0, -1));
    }


    public void testIsValidDateRange()
    {
        LocalDate arrival = LocalDate.of(2026, 10, 10);
        LocalDate departure = LocalDate.of(2026, 10, 15);

        assertTrue(InputValidator.isValidDateRange(arrival, departure));
        assertTrue(InputValidator.isValidDateRange(arrival, arrival));

        assertFalse(InputValidator.isValidDateRange(departure, arrival));
        assertFalse(InputValidator.isValidDateRange(null, departure));
        assertFalse(InputValidator.isValidDateRange(arrival, null));
    }


    public void testIsValidCost()
    {
        assertTrue(InputValidator.isValidCost(0));
        assertTrue(InputValidator.isValidCost(25.50));

        assertFalse(InputValidator.isValidCost(-1));
        assertFalse(InputValidator.isValidCost(Double.NaN));
        assertFalse(InputValidator.isValidCost(Double.POSITIVE_INFINITY));
    }
}

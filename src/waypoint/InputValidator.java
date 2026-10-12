package waypoint;

import java.time.LocalDate;

public class InputValidator
{
    public static boolean isValidName(String name)
    {
        return name != null && !name.trim().isEmpty();
    }


    public static boolean isValidMenuChoice(int choice, int min, int max)
    {
        return min <= max && choice >= min && choice <= max;
    }


    public static boolean isValidExistingPosition(int position, int size)
    {
        return size > 0 && position >= 0 && position < size;
    }


    public static boolean isValidInsertionPosition(int position, int size)
    {
        return size >= 0 && position >= 0 && position <= size;
    }


    public static
        boolean
        isValidDateRange(LocalDate arrival, LocalDate departure)
    {
        return arrival != null && departure != null
            && !arrival.isAfter(departure);
    }


    public static boolean isValidCost(double cost)
    {
        return Double.isFinite(cost) && cost >= 0;
    }
}

package waypoint;

/**
 * Tests RoadTrip without requiring JUnit. Run this class as a Java Application.
 *
 * @author Jovany
 * @version 2.0
 */
public class RoadTripTest
{
    private static int checks;

    /**
     * Runs the test cases.
     *
     * @param args
     *            unused
     */
    public static void main(String[] args)
    {
        RoadTrip trip = new RoadTrip("  Fall Trip  ");

        check("Fall Trip".equals(trip.getName()), "Constructor trims the name");

        check(
            trip.isEmpty() && trip.getDestinationCount() == 0,
            "New trip is empty");

        check(trip.getTotalActivities() == 0, "Empty trip has zero activities");

        check(
            trip.getMostActiveDestination() == null,
            "Empty trip has no most-active destination");

        check(
            trip.toString().contains("No destinations"),
            "Empty trip displays correctly");

        expect(IllegalArgumentException.class, () -> new RoadTrip(null));

        expect(IllegalArgumentException.class, () -> new RoadTrip("  "));

        expect(IllegalArgumentException.class, () -> trip.setName(null));

        expect(IllegalArgumentException.class, () -> trip.setName("\t"));

        check(
            "Fall Trip".equals(trip.getName()),
            "Invalid rename preserves the original name");

        trip.setName("Spring Trip");

        check("Spring Trip".equals(trip.getName()), "Valid rename succeeds");

        Destination a = new Destination("Richmond");
        Destination b = new Destination("DC");
        Destination c = new Destination("Boston");
        Destination duplicate = new Destination("Richmond");

        trip.insertDestination(0, a);
        trip.addDestination(c);
        trip.insertDestination(1, b);
        trip.insertDestination(3, duplicate);

        check(
            trip.getDestinationCount() == 4 && !trip.isEmpty(),
            "Destination count updates");

        check(trip.getDestination(0) == a, "Insertion at the beginning works");

        check(trip.getDestination(1) == b, "Insertion in the middle works");

        check(trip.getDestination(2) == c, "Appending preserves order");

        check(
            trip.getDestination(3) == duplicate,
            "End insertion allows duplicate names");

        check(
            trip.getMostActiveDestination() == null,
            "Stops with no activities return null");

        expect(IllegalArgumentException.class, () -> trip.addDestination(null));

        expect(
            IllegalArgumentException.class,
            () -> trip.insertDestination(0, null));

        expect(
            IndexOutOfBoundsException.class,
            () -> trip.insertDestination(-1, a));

        expect(
            IndexOutOfBoundsException.class,
            () -> trip.insertDestination(5, a));

        expect(IndexOutOfBoundsException.class, () -> trip.getDestination(-1));

        expect(IndexOutOfBoundsException.class, () -> trip.getDestination(4));

        expect(
            IndexOutOfBoundsException.class,
            () -> trip.removeDestination(-1));

        expect(
            IndexOutOfBoundsException.class,
            () -> trip.removeDestination(4));

        check(
            trip.getDestinationCount() == 4,
            "Invalid operations preserve the count");

        check(
            trip.getDestination(0) == a && trip.getDestination(1) == b
                && trip.getDestination(2) == c
                && trip.getDestination(3) == duplicate,
            "Invalid operations preserve order");

        a.addActivity(new Activity("Museum"));
        a.addActivity(new Activity("Park"));
        b.addActivity(new Activity("Lunch"));

        check(
            trip.getTotalActivities() == 3,
            "Activities are totaled across destinations");

        check(
            trip.getMostActiveDestination() == a,
            "Most-active destination is correct");

        b.addActivity(new Activity("Walk"));

        check(
            trip.getMostActiveDestination() == a,
            "A tie returns the first destination");

        a.removeActivity(0);

        check(
            trip.getMostActiveDestination() == b,
            "Most-active destination updates after removal");

        check(
            trip.getTotalActivities() == 3,
            "Total updates after activity removal");

        String display = trip.toString();

        check(
            display.contains("Trip: Spring Trip") && display
                .contains("1. Richmond\n2. DC\n3. Boston\n4. Richmond"),
            "Itinerary displays in travel order");

        check(
            trip.removeDestination(1) == b,
            "Removing the middle returns the correct destination");

        check(trip.getDestination(1) == c, "Indices shift after removal");

        check(
            trip.getTotalActivities() == 1,
            "Removed destinations are excluded from the total");

        check(
            trip.removeDestination(2) == duplicate,
            "Removing the last destination works");

        check(
            trip.removeDestination(0) == a,
            "Removing the first destination works");

        check(
            trip.removeDestination(0) == c && trip.isEmpty(),
            "Removing the only destination leaves an empty trip");

        expect(IndexOutOfBoundsException.class, () -> trip.getDestination(0));

        expect(
            IndexOutOfBoundsException.class,
            () -> trip.removeDestination(0));

        check(
            trip.getTotalActivities() == 0
                && trip.getMostActiveDestination() == null,
            "Summaries return to empty values");

        System.out.println("RoadTripTest: " + checks + " checks passed.");
    }


    /**
     * Checks a test condition.
     *
     * @param condition
     *            the expected true condition
     * @param message
     *            the failure description
     */
    private static void check(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }

        checks++;
    }


    /**
     * Checks that an operation throws the expected exception.
     *
     * @param type
     *            the expected exception type
     * @param operation
     *            the operation to execute
     */
    private static
        void
        expect(Class<? extends Throwable> type, Runnable operation)
    {
        try
        {
            operation.run();
        }
        catch (Throwable error)
        {
            check(type.isInstance(error), "Wrong exception: " + error);

            return;
        }

        throw new AssertionError("Expected " + type.getSimpleName());
    }
}

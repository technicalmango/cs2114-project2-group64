package waypoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads trips using CSV records. Assumes Destination provides:
 * Destination(String name) Destination(String name, LocalDate arrival,
 * LocalDate departure) getArrivalDate(), getDepartureDate() Assumes Activity
 * provides: Activity(String name) Activity(String name, LocalDate date,
 * LocalTime time, double cost) getDate(), getTime(), getCost()
 */
public class TripFileManager
{
    private final Path file;

    /**
     * Creates a file manager using the default CSV file.
     */
    public TripFileManager()
    {
        this(Paths.get("waypoint-trip.csv"));
    }


    /**
     * Creates a file manager for a specified file.
     */
    public TripFileManager(Path file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("File path cannot be null");
        }

        this.file = file;
    }


    /**
     * Saves a trip and its nested destinations and activities.
     */
    public void saveTrip(RoadTrip trip)
        throws IOException
    {
        if (trip == null || !InputValidator.isValidName(trip.getName()))
        {
            throw new IllegalArgumentException("A valid trip is required");
        }

        Path target = file.toAbsolutePath();
        Path temporary =
            Files.createTempFile(target.getParent(), "waypoint-", ".tmp");

        try
        {
            try (
                BufferedWriter writer =
                    Files.newBufferedWriter(temporary, StandardCharsets.UTF_8))
            {
                writeRecord(writer, "TRIP", trip.getName());
                saveDestinations(trip, writer);
            }

            try
            {
                Files.move(
                    temporary,
                    target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e)
            {
                Files.move(
                    temporary,
                    target,
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally
        {
            Files.deleteIfExists(temporary);
        }
    }


    /**
     * Writes destinations, including arrival and departure dates.
     */
    private void saveDestinations(RoadTrip trip, BufferedWriter writer)
        throws IOException
    {
        for (int i = 0; i < trip.getDestinationCount(); i++)
        {
            Destination destination = trip.getDestination(i);

            writeRecord(
                writer,
                "DESTINATION",
                destination.getName(),
                optionalText(destination.getArrivalDate()),
                optionalText(destination.getDepartureDate()));

            saveActivities(destination, writer);
        }
    }


    /**
     * Writes activities, including dates, times, and costs.
     */
    private void saveActivities(Destination destination, BufferedWriter writer)
        throws IOException
    {
        for (int i = 0; i < destination.getActivityCount(); i++)
        {
            Activity activity = destination.getActivity(i);
            double cost = activity.getEstimatedCost();

            if (!Double.isFinite(cost) || cost < 0)
            {
                throw new IOException(
                    "Invalid cost for activity: " + activity.getName());
            }

            writeRecord(
                writer,
                "ACTIVITY",
                activity.getName(),
                optionalText(activity.getDate()),
                optionalText(activity.getTime()),
                Double.toString(cost));
        }
    }


    /**
     * Loads both legacy two-field records and extended records.
     */
    public RoadTrip loadTrip()
        throws IOException
    {
        if (!Files.exists(file))
        {
            return null;
        }

        List<List<String>> records;

        try (
            BufferedReader reader =
                Files.newBufferedReader(file, StandardCharsets.UTF_8))
        {
            records = readRecords(reader);
        }

        if (records.isEmpty())
        {
            throw new IOException("CSV file is empty");
        }

        RoadTrip trip = null;
        Destination currentDestination = null;

        for (int i = 0; i < records.size(); i++)
        {
            List<String> record = records.get(i);

            try
            {
                if (record.size() < 2
                    || !InputValidator.isValidName(record.get(1)))
                {
                    throw new IOException("Missing or invalid record name");
                }

                String type = record.get(0);
                String name = record.get(1);

                if ("TRIP".equals(type))
                {
                    requireFieldCount(record, 2);

                    if (trip != null)
                    {
                        throw new IOException("Duplicate trip record");
                    }

                    trip = new RoadTrip(name);
                }
                else if ("DESTINATION".equals(type))
                {
                    requireFieldCount(record, 2, 4);

                    if (trip == null)
                    {
                        throw new IOException("Destination before trip");
                    }

                    if (record.size() == 2)
                    {
                        currentDestination = new Destination(name);
                    }
                    else
                    {
                        LocalDate arrival = parseOptionalDate(record.get(2));
                        LocalDate departure = parseOptionalDate(record.get(3));

                        if (arrival != null && departure != null
                            && departure.isBefore(arrival))
                        {
                            throw new IOException(
                                "Departure date cannot precede arrival date");
                        }

                        currentDestination =
                            new Destination(name, arrival, departure);
                    }

                    trip.addDestination(currentDestination);
                }
                else if ("ACTIVITY".equals(type))
                {
                    requireFieldCount(record, 2, 5);

                    if (currentDestination == null)
                    {
                        throw new IOException("Activity before destination");
                    }

                    Activity activity;

                    if (record.size() == 2)
                    {
                        activity = new Activity(name);
                    }
                    else
                    {
                        LocalDate date = parseOptionalDate(record.get(2));
                        LocalTime time = parseOptionalTime(record.get(3));
                        double cost = parseCost(record.get(4));

                        activity = new Activity(name, date, time, cost);
                    }

                    currentDestination.addActivity(activity);
                }
                else
                {
                    throw new IOException("Unknown record type: " + type);
                }
            }
            catch (IOException | IllegalArgumentException
                | DateTimeParseException e)
            {
                throw new IOException(
                    "Invalid CSV record " + (i + 1) + ": " + e.getMessage(),
                    e);
            }
        }

        if (trip == null)
        {
            throw new IOException("Missing trip record");
        }

        return trip;
    }


    /**
     * Writes a CSV record with proper quotation escaping.
     */
    private static
        void
        writeRecord(BufferedWriter writer, String type, String... fields)
            throws IOException
    {
        writer.write(type);

        for (String field : fields)
        {
            writer.write(',');
            writer.write('"');
            writer.write(field.replace("\"", "\"\""));
            writer.write('"');
        }

        writer.newLine();
    }


    /**
     * Represents an unset date or time as an empty CSV field.
     */
    private static String optionalText(Object value)
    {
        return value == null ? "" : value.toString();
    }


    /**
     * Checks that a record has a supported number of fields.
     */
    private static
        void
        requireFieldCount(List<String> record, int... allowedCounts)
            throws IOException
    {
        for (int count : allowedCounts)
        {
            if (record.size() == count)
            {
                return;
            }
        }

        throw new IOException(
            "Incorrect number of fields for " + record.get(0) + ": "
                + record.size());
    }


    /**
     * Reads an ISO date, or null when no date is set.
     */
    private static LocalDate parseOptionalDate(String value)
    {
        return value.isEmpty() ? null : LocalDate.parse(value);
    }


    /**
     * Reads an ISO time, or null when no time is set.
     */
    private static LocalTime parseOptionalTime(String value)
    {
        return value.isEmpty() ? null : LocalTime.parse(value);
    }


    /**
     * Reads a required, finite, nonnegative activity cost.
     */
    private static double parseCost(String value)
        throws IOException
    {
        double cost = Double.parseDouble(value);

        if (!Double.isFinite(cost) || cost < 0)
        {
            throw new IOException(
                "Activity cost must be a finite, nonnegative number");
        }

        return cost;
    }


    /**
     * Reads CSV records, including quoted fields.
     */
    private static List<List<String>> readRecords(BufferedReader reader)
        throws IOException
    {
        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        boolean quoted = false;
        boolean afterQuote = false;
        boolean started = false;

        int c;

        while ((c = reader.read()) != -1)
        {
            char ch = (char)c;

            if (quoted)
            {
                if (ch == '"')
                {
                    quoted = false;
                    afterQuote = true;
                }
                else
                {
                    field.append(ch);
                }
            }
            else if (afterQuote)
            {
                if (ch == '"')
                {
                    field.append('"');
                    quoted = true;
                    afterQuote = false;
                }
                else if (ch == ',' || ch == '\n' || ch == '\r')
                {
                    row.add(field.toString());
                    field.setLength(0);
                    afterQuote = false;

                    if (ch == ',')
                    {
                        started = true;
                    }
                    else
                    {
                        records.add(row);
                        row = new ArrayList<>();
                        started = false;

                        if (ch == '\r')
                        {
                            reader.mark(1);
                            int next = reader.read();

                            if (next != '\n' && next != -1)
                            {
                                reader.reset();
                            }
                        }
                    }
                }
                else
                {
                    throw new IOException("Unexpected character after quote");
                }
            }
            else if (ch == '"')
            {
                if (field.length() != 0)
                {
                    throw new IOException("Unexpected quote in field");
                }

                quoted = true;
                started = true;
            }
            else if (ch == ',')
            {
                row.add(field.toString());
                field.setLength(0);
                started = true;
            }
            else if (ch == '\n' || ch == '\r')
            {
                if (ch == '\r')
                {
                    reader.mark(1);
                    int next = reader.read();

                    if (next != '\n' && next != -1)
                    {
                        reader.reset();
                    }
                }

                if (started || field.length() != 0 || !row.isEmpty())
                {
                    row.add(field.toString());
                    records.add(row);
                    row = new ArrayList<>();
                    field.setLength(0);
                    started = false;
                }
            }
            else
            {
                field.append(ch);
                started = true;
            }
        }

        if (quoted)
        {
            throw new IOException("Unterminated quoted CSV field");
        }

        if (started || afterQuote || field.length() != 0 || !row.isEmpty())
        {
            row.add(field.toString());
            records.add(row);
        }

        return records;
    }
}

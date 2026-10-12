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
import java.util.ArrayList;
import java.util.List;

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


    /*
     * Writes all destinations and their associated activities to the specified
     * CSV writer
     */
    private void saveDestinations(RoadTrip trip, BufferedWriter writer)
        throws IOException
    {
        for (int i = 0; i < trip.getDestinationCount(); i++)
        {
            Destination destination = trip.getDestination(i);

            writeRecord(writer, "DESTINATION", destination.getName());

            saveActivities(destination, writer);
        }
    }


    /*
     * Writes all activities belonging to a destination
     */
    private void saveActivities(Destination destination, BufferedWriter writer)
        throws IOException
    {
        for (int i = 0; i < destination.getActivityCount(); i++)
        {
            Activity activity = destination.getActivity(i);

            writeRecord(writer, "ACTIVITY", activity.getName());
        }
    }


    /**
     * Loads a saved trip from CSV.
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

        for (List<String> record : records)
        {
            if (record.size() != 2
                || !InputValidator.isValidName(record.get(1)))
            {
                throw new IOException("Malformed CSV record");
            }

            String type = record.get(0);
            String name = record.get(1);

            if ("TRIP".equals(type))
            {
                if (trip != null)
                {
                    throw new IOException("Duplicate trip record");
                }

                trip = new RoadTrip(name);
            }
            else if ("DESTINATION".equals(type))
            {
                if (trip == null)
                {
                    throw new IOException("Destination before trip");
                }

                currentDestination = new Destination(name);
                trip.addDestination(currentDestination);
            }
            else if ("ACTIVITY".equals(type))
            {
                if (currentDestination == null)
                {
                    throw new IOException("Activity before destination");
                }

                currentDestination.addActivity(new Activity(name));
            }
            else
            {
                throw new IOException("Unknown record type: " + type);
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
        writeRecord(BufferedWriter writer, String type, String name)
            throws IOException
    {
        writer.write(type);
        writer.write(',');
        writer.write('"');
        writer.write(name.replace("\"", "\"\""));
        writer.write('"');
        writer.newLine();
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

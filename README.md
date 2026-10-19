# WayPoint — Road Trip Planner

## Project Overview

WayPoint is a Java-based console application that allows users to create and manage road trip itineraries. Users can organize destinations in travel order, add activities to individual destinations, and view a summary of their planned trip.

The application also supports scheduling activities with dates, times, and estimated costs, as well as saving and loading trip information between sessions using CSV persistence.

WayPoint was developed by Group 64 for **CS 2114: Software Design & Data Structures at Virginia Tech**.

**Team Members:** Connor Bo, Alexander Kyle, Essan Salem, and Jovany Velazquez.

## Implemented Features

### Minimum Viable Product (MVP)

The following core features have been implemented:

- Create and name a road trip.
- Add destinations to the end of an itinerary.
- Insert destinations at specific positions.
- Remove destinations from the itinerary.
- View destinations in travel order.
- Add activities to individual destinations.
- Remove activities from destinations.
- View activities associated with a destination.
- Display a trip summary showing the number of destinations and total number of activities.
- Validate user input and handle invalid menu selections, names, and positions.

The trip summary also identifies the destination with the most activities.

### Stretch Goal 1: CSV Persistence

WayPoint supports CSV file persistence, allowing users to save their road trip information and reload it between application sessions.

This functionality includes:

- Saving the trip name and destinations in travel order.
- Saving activities associated with each destination.
- Preserving destination arrival and departure dates.
- Preserving activity dates, times, and estimated costs.
- Attempting to load an existing saved trip when the application starts.
- Handling errors during file loading and saving.
- Allowing users to exit with or without saving changes.

The `TripFileManager` class manages CSV file operations, separating persistence logic from the application's main functionality.

### Stretch Goal 2: Activity Scheduling

WayPoint includes activity scheduling functionality that allows users to create more detailed travel itineraries.

This functionality includes:

- Setting arrival and departure dates for destinations.
- Scheduling activities with a name, date, time, and estimated cost.
- Validating that activities fall within their destination's scheduled stay.
- Rejecting invalid date ranges, times, and estimated costs.
- Displaying scheduled activities in chronological order.
- Incorporating scheduling information into the trip summary.

The expanded trip summary displays the total trip duration, total estimated activity cost, and destination with the longest stay.

Trip duration is calculated using the earliest destination arrival date and latest departure date, including both dates.

### Features Not Implemented

The team originally considered integrating a mapping API to provide route information, driving distances, and estimated travel times between destinations.

However, we decided not to implement this feature because of time constraints and the additional complexity involved in integrating an external API.

## Data Structures and Class Design

WayPoint uses the generic `LinkedChain<T>` data structure to manage destinations and activities.

The same generic class is reused for two different element types:

- `LinkedChain<Destination>` stores destinations in travel order within a `RoadTrip`.
- `LinkedChain<Activity>` stores activities associated with each `Destination`.

Each destination maintains its own linked chain of activities, creating a nested data structure.

Application classes interact with these chains through the public methods of `LinkedChain<T>` rather than directly accessing internal nodes.

### Main Classes

| Class | Responsibility |
|---|---|
| `RoadTrip` | Stores the trip name and ordered destinations, manages destination operations, and calculates trip summary information. |
| `Destination` | Represents a destination, manages its activities, and stores arrival and departure dates. |
| `Activity` | Represents an activity with a name, date, time, and estimated cost. |
| `LinkedChain<T>` | Generic linked-chain data structure used for storing destinations and activities. |
| `InputValidator` | Validates user input, including names, positions, dates, and costs. |
| `TripFileManager` | Handles saving and loading trip information using CSV files. |
| `WayPointApp` | Provides the console interface, processes user input, and coordinates interactions between classes. |

This design separates data storage, application logic, input validation, file persistence, and user interaction.

## How to Compile and Run

### Running the Application

1. Import the WayPoint project into Eclipse.
2. Confirm that the project uses Java 17 and that the required dependencies are configured.
3. Open `WayPointApp.java`.
4. Right-click the file and select **Run As → Java Application**.
5. Follow the console menu prompts to create and manage a trip.

When the application starts, it attempts to load an existing saved trip.

### Console Menu

The application provides the following menu options:

| Option | Action |
|---|---|
| 1 | Create a new trip |
| 2 | Add a destination |
| 3 | Insert a destination |
| 4 | Remove a destination |
| 5 | View destinations |
| 6 | Add an activity |
| 7 | Remove an activity |
| 8 | View activities |
| 9 | View trip summary |
| 10 | Set destination arrival and departure dates |
| 11 | Save and exit |
| 0 | Exit without saving |

Selecting **11. Save and exit** saves the current trip to `waypoint-trip.csv`.

Selecting **0. Exit without saving** allows the user to exit without saving changes, following the application's confirmation process.

### Saving and Loading

WayPoint uses CSV persistence to store trip information between sessions.

The saved information includes the trip name, destinations, destination dates, activities, activity dates and times, and estimated costs.

The `TripFileManager` class handles reading and writing the CSV file.

## Testing and Error Handling

WayPoint uses unit tests based on `student.TestCase` to verify application functionality and error handling.

Testing focuses on normal operations and invalid-input scenarios.

### Functionality Tested

Testing covers the following areas:

- Creating and renaming trips.
- Adding, inserting, retrieving, and removing destinations.
- Adding, viewing, and removing activities.
- Validating trip, destination, and activity names.
- Handling invalid menu choices and positions.
- Attempting operations when no trip or destination exists.
- Setting valid destination arrival and departure dates.
- Rejecting invalid destination date ranges.
- Scheduling activities within destination stays.
- Rejecting activities outside their destination's scheduled dates.
- Validating activity dates, times, and estimated costs.
- Displaying activities chronologically.
- Calculating trip summary information.
- Saving and loading trip information.
- Handling file-loading and saving errors.
- Exiting with or without saving.

### Testing Approach

Each test class focuses on a specific application component.

Tests use valid inputs to verify expected behavior and invalid inputs to check that errors are handled appropriately.

Application-level tests use simulated console input and a fake file manager to test user interactions without modifying real CSV files.

One important detail is that the console interface displays destination and activity positions beginning at 1, while the underlying linked chains use zero-based indices. Tests help verify that these positions are converted correctly.

The application also uses an injected output stream (`output.println()`) rather than relying exclusively on `System.out.println()`. This makes console output easier to capture and verify during testing.

### Running the Tests

1. Open the project in Eclipse.
2. Locate the test classes in the `waypoint` package.
3. Right-click a test class, such as `RoadTripTest.java` or `WayPointAppTest.java`.
4. Select **Run As → JUnit Test** using the configured course-supported testing environment.
5. Review the results and resolve any failures.

## Development Process and Reflection

### From Specification to Prototype

During the initial development stage, our team focused on translating the planned class design into a functioning Java application.

One of the main challenges was establishing how the `RoadTrip`, `Destination`, and `Activity` classes would communicate while using the generic linked-chain data structure.

We prioritized implementing the core MVP features before expanding the application with additional functionality.

We also moved the trip summary from an originally proposed stretch goal into the MVP because basic summary information could be calculated using the existing destination and activity data.

### From Prototype to Final Implementation

After implementing the MVP, we expanded the application with CSV persistence and activity scheduling.

CSV persistence required us to develop file-saving and loading functionality that could preserve the nested structure of trips, destinations, and activities.

Activity scheduling required additional fields, date validation, chronological activity display, and updates to the trip summary.

Integrating these features introduced challenges because changes to one class often affected other parts of the application.

One recurring issue involved inconsistencies between files developed by different group members, including mismatched method names, parameters, and expected behavior.

We addressed these issues by reviewing class interfaces, debugging integration errors, and testing interactions between components.

### What Went Well

Our team successfully implemented the MVP and the two selected stretch goals.

We also successfully reused the same generic linked-chain implementation for both destinations and activities.

Separating responsibilities between classes made the application easier to organize, maintain, and test.

### What Could Have Gone Better

One of our biggest challenges was coordinating the implementation of different classes across multiple team members.

Differences between method definitions and method calls caused integration errors that required additional debugging.

Introducing new functionality also required changes to existing classes and tests, making it important to verify that previously implemented features continued working.

In future projects, we would establish and agree on class contracts earlier, integrate changes more frequently, and test individual components before combining larger features.

### What We Learned About Generics and Reuse

At the beginning of the project, we understood generics primarily as a way to make classes work with different data types.

Through WayPoint, we developed a better understanding of how generics support reusable software design.

By using `LinkedChain<T>` for both destinations and activities, we were able to reuse one data structure while maintaining compile-time type safety.

We also learned that reusable code requires well-defined method contracts because multiple classes depend on the same implementation.

Overall, the project strengthened our understanding of generic data structures, object-oriented design, integration testing, input validation, file persistence, and collaborative development.

## Future Improvements

Although the MVP and the two selected stretch goals have been implemented, several features could be added in the future:

1. **Mapping API integration:** Calculate driving distances, travel times, and routes between destinations.
2. **Additional travel expenses:** Include transportation, lodging, and gas costs in trip estimates.
3. **Graphical user interface:** Replace or supplement the console interface with a more interactive design.
4. **Advanced activity scheduling:** Detect overlapping activities and improve scheduling conflict validation.
5. **Multiple saved trips:** Allow users to save and load multiple itineraries rather than relying on a single saved trip.
6. **Further testing:** Expand edge-case coverage and improve handling of malformed or incomplete CSV files.

These are potential future improvements and are not part of the current implemented functionality.

## Attribution

The following table documents the project files and their primary contributors.

| File | Contributor(s) | Contribution |
|---|---|---|
| `Activity.java` | Alex | Activity model, scheduling fields, and validation |
| `ActivityTest.java` | Alex | Unit tests for Activity |
| `Destination.java` | Connor | Destination model, activity management, and stay scheduling |
| `DestinationTest.java` | Connor | Unit tests for Destination |
| `RoadTrip.java` | Jovany | Road trip model, destination management, and trip summary calculations |
| `RoadTripTest.java` | Jovany | Unit tests for RoadTrip |
| `LinkedChain.java` | Course-provided | Generic linked-chain implementation and team modifications |
| `LinkedChainTest.java` | Essan | Linked-chain tests |
| `InputValidator.java` | Essan | Input validation methods |
| `InputValidatorTest.java` | Essan | Input validation tests |
| `TripFileManager.java` | Essan | CSV saving and loading |
| `TripFileManagerTest.java` | Essan | Persistence tests |
| `WayPointApp.java` | Jovany | Console interface, menu operations, and application interaction |
| `WayPointAppTest.java` | Jovany | Scripted console tests and fake file manager |

## Generative AI Usage

Generative AI was used throughout development as a tool for brainstorming, explaining Java concepts, troubleshooting bugs, and assisting with testing and documentation.

Specifically, our team used generative AI to:

- Brainstorm possible class designs and implementation approaches.
- Help identify and troubleshoot complex bugs.
- Assist in developing test methods for more complicated functionality.
- Suggest improvements to code organization and documentation.

Our team reviewed AI-generated suggestions against the project's existing class design and method signatures.

We also ran and reviewed tests, debugged integration errors, and verified expected application behavior.

One important lesson was recognizing the risks of relying too heavily on generative AI, particularly when generating test methods or implementing linked-chain operations before fully understanding the underlying concepts.

In future projects, we would use generative AI primarily for explanations, brainstorming, and debugging guidance while placing greater emphasis on independently understanding and implementing the code.

Team members remained responsible for reviewing, integrating, testing, and verifying the final implementation.

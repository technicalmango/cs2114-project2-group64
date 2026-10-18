# WayPoint — Road Trip Planner

## Project Overview

WayPoint is a Java-based, console application that allows users to create and manage a road trip itinerary. Users can organize destinations in travel order and add activities to individual destinations. The application also supports saving and loading trip information using a CSV file.

The project was developed for CS 2114: Software Design & Data Structures at Virginia Tech.

## Implemented Features

The following Minimum Viable Product (MVP) features have been implemented:

- Create and name a road trip.
- Add destinations to the itinerary.
- Insert destinations at specific positions.
- Remove destinations.
- View destinations in travel order.
- Add activities to destinations.
- Remove activities from destinations.
- View activities associated with a destination.
- Save and load trip information using CSV persistence.
- Validate user input and handle invalid menu selections and positions.

### Data Structures

WayPoint uses the generic `LinkedChain<T>` data structure for two different element types:

- `LinkedChain<Destination>` stores the ordered destinations in a `RoadTrip`.
- `LinkedChain<Activity>` stores the activities associated with each `Destination`.

Application classes interact with these chains through the public methods of `LinkedChain<T>` rather than directly accessing its internal nodes.

## How to Compile and Run

### Running the Application

1. Import the WayPoint project into Eclipse.
2. Confirm that the project uses Java 17 and that all required dependencies are configured.
3. Open `WayPointApp.java`.
4. Right-click the file and select **Run As → Java Application**.
5. Follow the console menu prompts to create and manage a trip.

The program attempts to load an existing saved trip when it starts. Selecting **9. Save and exit** saves the current trip to `waypoint-trip.csv`. Selecting **0. Exit without saving** requires confirmation.

### Running the Tests

1. Open the project in Eclipse.
2. Locate the test classes in the `waypoint` package.
3. Right-click a test class, such as `WayPointAppTest.java`.
4. Select the course-supported test runner (for example, **Run As → JUnit Test**).
5. Review the test results in Eclipse.

Tests use `student.TestCase` assertions. The application tests use simulated console input and a fake file manager to avoid modifying real CSV files.

## Testing and Error Handling

Testing covers normal operations and invalid-input scenarios, including:

- Invalid menu choices and nonnumeric input.
- Blank trip and destination names.
- Attempting operations before creating a trip.
- Attempting destination operations when no destinations exist.
- Adding, inserting, and removing destinations.
- Adding, viewing, and removing activities.
- Attempting to remove activities from an empty destination.
- Canceling or confirming trip replacement.
- Loading an existing trip.
- Recovering from file-loading errors.
- Handling save failures and retrying.
- Exiting with or without saving.

User-facing positions are converted to zero-based indices before accessing the underlying linked chains.

## Reflection

During implementation, our team focused on developing a functional console-based road trip planner and integrating the generic linked-chain data structure into the application.

One important change was simplifying the original project scope to prioritize the MVP. Instead of implementing more advanced scheduling or route-planning features immediately, we focused on reliable destination and activity management, user input validation, and CSV persistence.

We also refined how the application handles destination and activity positions. The user interface displays positions beginning at 1, while the underlying data structure uses zero-based indices. This required careful conversion when retrieving, inserting, and removing elements.

Testing revealed additional issues involving scripted console input, menu behavior, and output formatting. We updated the test cases to match the implemented application and used dependency injection to test the application without reading or writing actual CSV files.

Overall, this stage helped us better understand how to organize a Java application into separate classes, reuse a generic data structure, and test interactions between application components.

## Next Steps

Our next priorities are to:

1. Continue expanding unit tests and verifying edge cases.
2. Improve console output formatting.
3. Further test CSV persistence, including malformed or incomplete saved files.
4. Implement activity scheduling with dates and times, including validation of chronological order.
5. Add a trip summary displaying information such as total destinations, total activities, and the destination with the most activities.
6. Refine the user interface and improve overall usability.

The activity scheduling and trip summary features were considered as stretch goals and are not part of the current completed MVP.

## Attribution

The following table documents the project files and their contributors. **Replace the bracketed entries with the actual contributors before submitting.**

| File | Contributor(s) | Contribution |
|---|---|---|
| `Activity.java` | Alex | Activity model and validation |
| `ActivityTest.java` | Alex| Unit tests for Activity |
| `Destination.java` | Connor| Destination model and activity management |
| `DestinationTest.java` | Connor | Unit tests for Destination |
| `RoadTrip.java` | Jovany | Road trip model and destination management |
| `RoadTripTest.java` | Jovany | Unit tests for RoadTrip |
| `LinkedChain.java` | Course-provided | Generic linked-chain implementation and any team modifications |
| `LinkedChainTest.java` | Essan | Linked-chain tests |
| `InputValidator.java` | Essan | Input validation methods |
| `InputValidatorTest.java` | Essan | Input validation tests |
| `TripFileManager.java` | Essan | CSV saving and loading |
| `TripFileManagerTest.java` | Essan | Persistence tests |
| `WayPointApp.java` | Jovany | Console menu and application interaction |
| `WayPointAppTest.java` | Jovany | Scripted application tests and fake file manager |


## Generative AI Usage

Generative AI was used as a development aid for explaining Java concepts, identifying potential bugs, suggesting code improvements, and assisting with the creation and revision of test cases and documentation.

AI-generated suggestions were reviewed and adapted to the project's existing class design and requirements. Team members remained responsible for understanding, integrating, testing, and verifying the submitted implementation.

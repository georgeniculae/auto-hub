# Auto Hub

Auto Hub is a Java-based application designed to manage booking and invoicing for a car rental service. The project
leverages Spring Boot for building the application, Maven for dependency management, and SQL for database interactions.

## Features

### Booking Management

- **Booking Creation**: Allows users to create new bookings for car reservations, specifying details such as car type,
  rental period, and customer information.
- **Booking Updates**: Supports updating existing bookings with new details, such as changes in rental period or car
  type.
- **Booking Mapping**: Converts booking entities to DTOs (Data Transfer Objects) and vice versa for seamless data
  transfer between different layers of the application.

### Messaging

- **Kafka Integration**: Utilizes Kafka for sending booking update messages asynchronously to ensure real-time
  communication and updates.
- **Error Handling**: Implements robust error handling and logging for message sending operations to ensure reliability
  and traceability.

### Testing Utilities

- **Test Data Management**: Provides utilities for loading test data from JSON files, making it easier to set up and run
  tests with consistent data.
- **Assertions**: Includes custom assertion utilities for validating test results, ensuring that the application behaves
  as expected under various conditions.

### Exception Handling

- **Custom Exceptions**: Defines custom exceptions for handling specific error scenarios within the application,
  providing more meaningful error messages and improving debugging.

## Technologies Used

- **Java**: Core programming language.
- **Spring Boot**: Framework for building the application.
- **Maven**: Dependency management tool.
- **SQL**: Database interactions.
- **Kafka**: Messaging system for asynchronous communication.
- **JUnit**: Testing framework.
- **Mockito**: Mocking framework for unit tests.
- **Jackson**: Library for JSON processing.

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6 or higher
- Kafka (for messaging functionality)
- SQL Database

### Installation

Clone the repository:

```sh
   git clone https://github.com/georgeniculae/auto-hub.git

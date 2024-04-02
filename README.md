# Customer Statement Processor

Customer Statement records are delivered in two formats, CSV and XML. These records need to be validated.

# Building

You will need Java 21 installed on your machine.
To build the code, simply run in the root folder `./gradlew clean build` on Linux/MacOS or `gradlew.bat clean build` on Windows.

# Running

To run the app, execute in the root folder `./gradlew bootRun -Dspring.profiles.active=local` on Linux/MacOS or `gradlew.bat bootRun -Dspring.profiles.active=local` on Windows.
If you have Docker installed, you can instead run `docker-compose up` in the root folder.
The API will be available at `http://localhost:8080/`.

# Documentation

The API documentation is available at `http://localhost:8080/swagger-ui.html`.

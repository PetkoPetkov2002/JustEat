# JustEat Restaurant Finder

This project consists of a Kotlin backend using Ktor and a React frontend that allows users to search for restaurants by postcode using the JustEat API.

## Project Structure

- **Backend**: Kotlin application built with Ktor that serves as an API proxy for the JustEat API
- **Frontend**: React application that provides a user interface for searching restaurants by postcode

## Prerequisites

### Backend Requirements
- JDK 17 or higher
- Gradle 7.6 or higher

### Frontend Requirements
- Node.js 14.x or higher
- npm 6.x or higher

## Getting Started

### Cloning the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/yourusername/JustEat.git
cd JustEat
```

### Setting Up and Running the Backend

1. Navigate to the backend directory:
   ```bash
   cd ktor-sample
   ```

2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

3. Run the backend server:
   ```bash
   ./gradlew run
   ```

The backend server will start on `http://localhost:8080`.

### Setting Up and Running the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd FrontEnd/frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend development server will start on `http://localhost:5173`.

## API Endpoints

The backend exposes the following API endpoint:

- `GET /restaurants/{postcode}` - Fetches restaurants for the specified UK postcode

## How It Works

1. The user enters a UK postcode in the frontend search input
2. The frontend sends a request to the backend API
3. The backend validates the postcode format
4. If valid, the backend queries the JustEat API to retrieve restaurant data
5. The backend processes and returns the restaurant data to the frontend
6. The frontend displays the restaurant information

## Backend Components

- **Models** (`JustEatModel.kt`): Data classes for parsing the JustEat API response
- **Service** (`JustEatService.kt`): Handles API calls to the JustEat API
- **Validator** (`PostcodeValidator.kt`): Validates UK postcode format
- **Routing** (`Routing.kt`): Defines API endpoints and request handling

##Testing File
I have included a test file under ktor-sample->src->test. In intellij IDEA you can run these independently ro just the whole file, feel free to alter parameters.

## Frontend Components

- **SearchPage**: Main component for the search interface
- **Restaurant**: Component for displaying individual restaurant information

## Dependencies

### Backend Dependencies
- Ktor: Web framework for Kotlin
- kotlinx.serialization: JSON serialization/deserialization
- Ktor Client: HTTP client for API requests

### Frontend Dependencies
- React: UI library
- Axios: HTTP client for API requests
- Vite: Build tool and development server

## Troubleshooting

### Backend Issues

- If you get port conflicts, modify the port in `application.yaml`
- For JVM issues, ensure you have JDK 17+ installed and configured

### Frontend Issues

- If API calls fail, make sure the backend is running and check the proxy settings in `vite.config.js`
- For dependency issues, try deleting `node_modules` and reinstalling with `npm install`

## Development Setup

For development, you'll need:

1. An IDE with Kotlin support (IntelliJ IDEA recommended for backend)
2. A code editor for frontend (VS Code recommended)
3. Git for version control

## Quick Start Script

You can use the following script to install dependencies and start both servers:

```bash
#!/bin/bash

# Install backend dependencies and start the server
echo "Setting up backend..."
cd ktor-sample
./gradlew build
./gradlew run &
cd ..

# Wait for backend to start
sleep 5

# Install frontend dependencies and start the server
echo "Setting up frontend..."
cd FrontEnd/frontend
npm install
npm run dev
```

Save this as `start.sh`, make it executable with `chmod +x start.sh`, and run with `./start.sh`.

## Assumptions
- The application is intended for UK market only, given the postcode validation(As stated in the requirement document)
- Frontend will be accessed through modern web browsers that support ES6+ features


## Improvements

- Add caching layer for frequently searched postcodes to reduce API calls
- Implement error retry mechanism for failed API requests
- Add unit tests for frontend components
- Implement restaurant filtering by cuisine type and rating
- Consider adding geolocation to auto-fill user's postcode
- Add accessibility features (ARIA labels, keyboard navigation)
- Implement request throttling to prevent API abuse
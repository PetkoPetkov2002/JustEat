# JustEat Restaurant Finder

This project fulfills the Early Careers Mobile Engineering Program coding assignment. It features a primary Android application (written in Kotlin) and a supplementary Ktor backend with a React frontend, all designed to search for and display restaurant information based on a UK postcode using the JustEat API.

## Assignment Fulfilment

This project meets the assignment criteria as follows:

*   **Data Points:** The Android application displays the required four data points for each restaurant: Name, Cuisines, Rating (as a number), and Address.
*   **Interface Focus:** The Android UI (built with Jetpack Compose) focuses on clearly presenting these four data points for the first 10 restaurants returned by the API.
*   **Build/Run Instructions:** Detailed steps for building and running the Android application, Ktor backend, and React frontend are provided below.
*   **Assumptions:** Key assumptions made during development are listed.
*   **Improvements:** Potential improvements beyond the current implementation are outlined.
*   **Language:** The core Android application logic and UI are implemented entirely in Kotlin.
*   **API Usage:** The Ktor backend proxies requests to the specified JustEat API endpoint (`https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/{postcode}`), and the Android app consumes this backend.

## Project Structure

*   **Android App (`app2/`)**: The primary solution. A native Android application built with Kotlin and Jetpack Compose, following MVVM architecture. It fetches data from the Ktor backend.
*   **Backend (`ktor-sample/`)**: A Kotlin application built with Ktor serving as an API proxy for the JustEat API. It includes postcode validation and caching.
*   **Frontend (`FrontEnd/frontend/`)**: A supplementary React application providing an alternative web-based UI for searching restaurants via the Ktor backend.

## Prerequisites

### Android App Requirements
*   Android Studio (latest stable version recommended)
*   Android SDK Platform corresponding to the `compileSdk` version in `app/build.gradle.kts`
*   An Android Virtual Device (AVD) or a physical Android device (Android 8.0+ recommended)

### Backend Requirements
*   JDK 17 or higher
*   Gradle 7.6 or higher

### React Frontend Requirements
*   Node.js 14.x or higher
*   npm 6.x or higher

## Getting Started

### Cloning the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/PetkoPetkov2002/JustEat.git # Replace with your actual repo URL if different
cd JustEat
```

### Setting Up and Running the Backend (Required for Android App & React Frontend)

1.  Navigate to the backend directory:
    ```bash
    cd ktor-sample
    ```
2.  Build the project using Gradle:
    ```bash
    ./gradlew build
    ```
3.  Run the backend server:
    ```bash
    ./gradlew run
    ```
The backend server will start on `http://localhost:8080`. **Leave this running** while using the Android app or React frontend.

### Setting Up and Running the Android Application (Primary Solution)

1.  Open the `app2` directory as an existing project in Android Studio.
2.  Allow Android Studio to sync Gradle and download any necessary dependencies. This might take a few moments.
3.  Ensure the Ktor backend server is running (see previous step). The Android app connects to `http://10.0.2.2:8080` by default, which is the standard alias for the host machine's localhost from the Android emulator.
    *   **Note:** If running on a physical device, you'll need to:
        *   Ensure the device is on the same Wi-Fi network as the machine running the Ktor backend.
        *   Find your machine's local network IP address (e.g., `192.168.1.100`).
        *   Update the `BASE_URL` constant in `app2/app/src/main/java/com/example/app/data/repository/RestaurantRepository.kt` to use this IP address (e.g., `http://192.168.1.100:8080/`).
4.  Select an emulator or connected physical device as the deployment target in Android Studio.
5.  Click the "Run 'app'" button (usually a green triangle) in Android Studio.
6.  The app will build, install, and launch on the selected device/emulator. Enter a valid UK postcode (e.g., EC4M 7RF) and tap "Search".

### Setting Up and Running the React Frontend (Supplementary)

1.  Ensure the Ktor backend server is running.
2.  Navigate to the frontend directory:
    ```bash
    cd FrontEnd/frontend
    ```
3.  Install dependencies:
    ```bash
    npm install
    ```
4.  Start the development server:
    ```bash
    npm run dev
    ```
The frontend development server will start on `http://localhost:5173`. Open this URL in your browser.

## Android Application Details

The Android application serves as the primary interface for this assignment.

*   **Architecture:** Follows the Model-View-ViewModel (MVVM) pattern for separation of concerns and testability.
*   **UI:** Built entirely with Jetpack Compose, Android's modern declarative UI toolkit.
*   **Data Fetching:** Uses Retrofit to communicate with the Ktor backend API.
*   **Data Persistence:** Implements a local caching layer using Room Persistence Library. Search results are cached for 15 minutes to reduce redundant API calls and improve user experience for repeated searches.
*   **State Management:** Uses `ViewModel` and Compose `State` holders (`mutableStateOf`, `mutableStateListOf`) to manage UI state effectively.
*   **Error Handling:** Displays user-friendly error messages for network issues or invalid postcodes retrieved from the backend. 
*   **Asynchronous Operations:** Uses Kotlin Coroutines for handling background tasks like network requests and database operations smoothly without blocking the main thread.

## Ktor Backend Components

*   **Models** (`JustEatModel.kt`): Data classes for parsing the JustEat API response.
*   **Service** (`JustEatService.kt`, `CacheService.kt`): Handles API calls to JustEat and manages server-side caching logic.
*   **Validator** (`PostcodeValidator.kt`): Validates UK postcode format. And returns appropriatte detailed error messages based on the type of error, e.g postcode is too long, postcode is too short, postcode is wrong format etc
*   **Routing** (`Routing.kt`): Defines API endpoints and request handling.

## React Frontend Components

*   **SearchPage**: Main component for the search interface.
*   **Restaurant**: Component for displaying individual restaurant information.

## Testing

*   **Backend:** Unit tests for the Ktor backend can be found in `ktor-sample/src/test`. These can be run via IntelliJ IDEA or using `./gradlew test` in the `ktor-sample` directory.
*   **Android:** Standard Android instrumentation and unit tests can be added under `app2/app/src/androidTest` and `app2/app/src/test` respectively.

## Assumptions

*   The application is intended for the UK market only, given the postcode validation and API limitations.
*   Restaurant information (name, address, cuisines, rating) doesn't change frequently minute-to-minute. Therefore, a **client-side cache (in the Android app) with a 15-minute lifetime** was implemented to optimize performance and reduce API load for repeated searches within that window.
*   Users have a stable internet connection for fetching data when not cached.

## Improvements

*   **Real-time Updates (Subscriber Model):** Instead of a fixed cache duration, implement a subscriber model. The Android app could subscribe to the backend. The backend could periodically (or based on triggers) send heartbeat calls to the JustEat API to check for changes in restaurant information for postcodes the app is interested in. Upon detecting a change, the backend notifies the app, which can then invalidate the appropriate cache record(s) and fetch fresh data.
*   **Android Unit/Integration Tests:** Add comprehensive unit tests for ViewModel logic and integration tests for the Repository layer in the Android app.
*   **Android UI Tests:** Implement UI tests using Espresso or Compose testing libraries to verify user interactions and screen states.
*   **Filtering/Sorting:** Allow users to filter or sort the displayed restaurants (e.g., by rating, cuisine type).
*   **Accessibility:** Enhance accessibility features in the Android app (e.g., content descriptions for Compose elements).

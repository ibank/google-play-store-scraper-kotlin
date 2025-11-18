# Maven Central Consumer Example

This example demonstrates how to use the Google Play Store Scraper library from Maven Central in your own project.

## Prerequisites

- JDK 17 or higher
- Gradle 8.0 or higher

## Project Structure

```
maven-consumer/
├── build.gradle.kts          # Gradle build configuration with Maven Central dependency
├── settings.gradle.kts       # Gradle settings
└── src/main/kotlin/
    └── com/example/
        └── TestScraper.kt    # Example usage code
```

## How It Works

### 1. Add Dependency (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.ibank:play-store-scraper:1.0.0")
}
```

### 2. Use the Library (TestScraper.kt)

```kotlin
// Initialize Koin DI
startKoin {
    modules(PlayStoreModule.module)
}

// Get repository
val repository = getKoin().get<PlayStoreRepository>()

// Fetch app details
val appDetails = repository.getAppDetails("com.android.chrome").firstOrNull()

// Fetch reviews
val reviews = repository.getReviews("com.android.chrome", count = 5).firstOrNull()
```

## Configuration

The example uses environment variables for configuration to avoid hardcoding app IDs:

```bash
# Copy the example configuration
cp .env.example .env

# Edit .env with your test app details
nano .env
```

**Environment Variables:**
- `TEST_APP_ID` - App package name to test (default: com.example.testapp)
- `TEST_LANGUAGE` - Language code (default: en)
- `TEST_COUNTRY` - Country code (default: us)
- `TEST_MAX_REVIEWS` - Max reviews to fetch (default: 5)

**Example `.env` file:**
```bash
TEST_APP_ID=com.spotify.music
TEST_LANGUAGE=en
TEST_COUNTRY=us
TEST_MAX_REVIEWS=10
```

## Running the Example

### Option 1: Using Gradle Run Task

```bash
cd examples/maven-consumer

# Set environment variables (or use .env file)
export TEST_APP_ID=com.spotify.music
export TEST_LANGUAGE=en
export TEST_COUNTRY=us
export TEST_MAX_REVIEWS=5

./gradlew run
```

### Option 2: Using Gradle Application Plugin

```bash
cd examples/maven-consumer
./gradlew installDist
./build/install/maven-consumer/bin/maven-consumer
```

### Option 3: Build and Run JAR

```bash
cd examples/maven-consumer
./gradlew build
java -jar build/libs/maven-consumer-1.0.0.jar
```

## Expected Output

```
================================================================================
Google Play Store Scraper - Maven Central Integration Test
================================================================================

📱 Example 1: Fetching App Details
--------------------------------------------------------------------------------
Fetching details for: com.android.chrome

Title: Google Chrome
Developer: Google LLC
Rating: 4.2
Installs: 10,000,000,000+
Price: Free
Category: Communication
Version: 120.0.6099.43
Updated: November 29, 2023

Description (first 200 chars):
Browse fast and type less. Choose Google Chrome, the browser built by Google...

⭐ Example 2: Fetching Reviews
--------------------------------------------------------------------------------
Fetching up to 5 reviews for: com.android.chrome

Found 5 reviews:

Review #1
  Author: John Doe
  Rating: ⭐⭐⭐⭐⭐
  Date: 2023-11-30
  Text: Great browser! Fast and reliable...

✅ All tests completed successfully!
================================================================================
```

## What This Example Demonstrates

1. **Maven Central Integration**: Shows how to add the library as a dependency from Maven Central
2. **Library Initialization**: Demonstrates Koin DI setup
3. **App Details Fetching**: Example of fetching and displaying app metadata
4. **Reviews Fetching**: Example of fetching and displaying user reviews
5. **Error Handling**: Proper use of Kotlin Result type for error handling
6. **Kotlin Coroutines**: Usage of Flow and suspend functions

## Troubleshooting

### Library Not Found

If you get "Could not find io.github.ibank:play-store-scraper:1.0.0":

1. **Wait for Maven Central sync**: After publishing, it takes 30 minutes to 2 hours for the library to be available
2. **Check Maven Central search**: https://central.sonatype.com/search?q=io.github.ibank
3. **Use local Maven**: For testing before Maven Central sync, publish to local Maven:

```bash
# In the main project directory
./gradlew publishToMavenLocal

# In build.gradle.kts, add mavenLocal() before mavenCentral()
repositories {
    mavenLocal()
    mavenCentral()
}
```

### Network Errors

If you get network errors when fetching data:
- This is expected as the scraper accesses Google Play Store
- Network issues or rate limiting may occur
- Try again after a few minutes

## Legal Notice

⚠️ **IMPORTANT**: This library is for educational and research purposes only.

Web scraping Google Play Store may violate:
- Google Play Terms of Service
- DMCA §1201 (Anti-circumvention)
- CFAA (Computer Fraud and Abuse Act)
- GDPR (if processing personal data)

**Use at your own risk.** The authors are not responsible for any legal consequences.

For production use, consider:
- [Google Play Developer API](https://developers.google.com/android-publisher) (official)
- Commercial data providers (App Annie, Sensor Tower, etc.)

## License

This example is released under the MIT License.

# Quick Start - Maven Central Consumer Example

This example demonstrates using the Google Play Store Scraper library directly from Maven Central.

## Published Library

**Maven Coordinates:**
```kotlin
implementation("io.github.ibank:play-store-scraper:1.0.0")
```

**Maven Central:**
https://central.sonatype.com/artifact/io.github.ibank/play-store-scraper/1.0.0

## Running the Example

### Option 1: Quick Test (Verify Library Access)
```bash
./gradlew run --args="com.example.SimpleTestKt"
```

This runs a simple test that verifies the library is accessible from Maven Central.

### Option 2: Full Integration Test (Recommended)
```bash
./gradlew run
```

Or use the helper script:
```bash
./RUN_TEST.sh
```

This runs the complete example that:
- Fetches real app details from Google Play Store
- Retrieves and displays app reviews
- Demonstrates Flow-based streaming

### Option 3: Build and Run JAR
```bash
./gradlew build
java -jar build/libs/maven-consumer-1.0.0.jar
```

## What You'll See

The example fetches information about Chrome (com.android.chrome):

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
Updated: 2023-11-29

⭐ Example 2: Fetching Reviews
--------------------------------------------------------------------------------
Fetching up to 5 reviews for: com.android.chrome

Review #1
  Author: John Doe
  Rating: ⭐⭐⭐⭐⭐
  ...

✅ All tests completed successfully!
================================================================================
```

## Dependencies

The example automatically fetches from Maven Central:
- `io.github.ibank:play-store-scraper:1.0.0` (your library)
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3`
- `io.insert-koin:koin-core:3.5.0` (required by the scraper)
- `ch.qos.logback:logback-classic:1.4.11`

## Source Code

- **SimpleTest.kt** - Basic library verification
- **TestScraper.kt** - Full API usage example

## Troubleshooting

**Library not found?**
- Wait 30min-2h after publishing for Maven Central sync
- Check: https://central.sonatype.com/search?q=io.github.ibank

**Network errors?**
- Google Play Store may rate-limit requests
- Try again after a few minutes

## Next Steps

After running this example, you can:
1. Use the library in your own projects
2. Explore the full API documentation
3. Customize the scraper configuration

For more information, see the main README:
https://github.com/ibank/google-play-store-scraper-kotlin

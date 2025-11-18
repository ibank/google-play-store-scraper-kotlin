#!/bin/bash

echo "================================================================================"
echo "Testing Google Play Store Scraper from Maven Central"
echo "================================================================================"
echo ""
echo "This will test the library published at:"
echo "https://central.sonatype.com/artifact/io.github.ibank/play-store-scraper/1.0.0"
echo ""

cd "$(dirname "$0")"

# Load .env file if it exists
if [ -f .env ]; then
    echo "Loading configuration from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
    echo ""
fi

# Display current configuration
echo "Test Configuration:"
echo "  APP_ID: ${TEST_APP_ID:-com.example.testapp (default)}"
echo "  LANGUAGE: ${TEST_LANGUAGE:-en (default)}"
echo "  COUNTRY: ${TEST_COUNTRY:-us (default)}"
echo "  MAX_REVIEWS: ${TEST_MAX_REVIEWS:-5 (default)}"
echo ""
echo "To customize, create .env file from .env.example"
echo ""

# Build the project
echo "Building project..."
./gradlew clean build --no-daemon --console=plain

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful! Running the test..."
    echo ""
    echo "================================================================================"
    
    # Run the test
    ./gradlew run --no-daemon --console=plain
    
    echo ""
    echo "================================================================================"
    echo "Test completed!"
    echo "================================================================================"
else
    echo "Build failed! Please check the error messages above."
    exit 1
fi

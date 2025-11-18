#!/bin/bash

set -e  # Exit on error

echo "================================================================================"
echo "Maven Central Local Publishing Script"
echo "================================================================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Get GPG Key ID
KEY_ID=$(gpg --list-secret-keys --keyid-format=short | grep "sec" | awk '{print $2}' | cut -d'/' -f2)

if [ -z "$KEY_ID" ]; then
    echo -e "${RED}✗${NC} No GPG key found. Please generate a GPG key first."
    exit 1
fi

echo -e "${GREEN}✓${NC} GPG Key ID: $KEY_ID"
echo ""

# Check gradle.properties exists
if [ ! -f "$HOME/.gradle/gradle.properties" ]; then
    echo -e "${RED}✗${NC} ~/.gradle/gradle.properties not found"
    echo ""
    echo "Please create ~/.gradle/gradle.properties with:"
    echo ""
    echo "  mavenCentralUsername=your_token_username"
    echo "  mavenCentralPassword=your_token_password"
    echo "  signing.keyId=$KEY_ID"
    echo "  signing.password=your_gpg_passphrase"
    echo "  signing.secretKeyRingFile=$HOME/.gnupg/secring.gpg"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓${NC} gradle.properties configured"
echo ""

# Check if secring.gpg exists
SECRING_FILE="$HOME/.gnupg/secring.gpg"

if [ ! -f "$SECRING_FILE" ]; then
    echo -e "${YELLOW}⚠${NC}  secring.gpg not found. Creating it now..."
    echo ""
    echo "This will prompt for your GPG passphrase."
    echo ""

    # Export secret keys
    gpg --export-secret-keys > "$SECRING_FILE"
    echo -e "${GREEN}✓${NC} secring.gpg created successfully"
    echo ""
else
    echo -e "${GREEN}✓${NC} secring.gpg exists: $SECRING_FILE"
    echo ""
fi

# Confirm before proceeding
echo "================================================================================"
echo "Ready to publish to Maven Central Portal"
echo "================================================================================"
echo ""
echo "  Group ID:    io.github.ibank"
echo "  Artifact ID: play-store-scraper"
echo "  Version:     $(grep 'version = ' build.gradle.kts | head -1 | cut -d'"' -f2)"
echo ""
echo "  Target:      https://central.sonatype.com"
echo ""

read -p "Do you want to continue? (y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Publishing cancelled."
    exit 0
fi

echo ""
echo "================================================================================"
echo "Publishing to Maven Central..."
echo "================================================================================"
echo ""

# Run Gradle publish with vanniktech plugin
./gradlew clean publishToMavenCentral \
  --no-daemon \
  --stacktrace

if [ $? -eq 0 ]; then
    echo ""
    echo "================================================================================"
    echo -e "${GREEN}✓ Publishing completed successfully!${NC}"
    echo "================================================================================"
    echo ""
    echo "Next steps:"
    echo ""
    echo "  1. Login to Maven Central Portal: https://central.sonatype.com"
    echo "  2. Go to 'Deployments' tab"
    echo "  3. Check your deployment status"
    echo "  4. It will automatically validate and publish (takes ~30 min)"
    echo ""
    echo "  After publishing:"
    echo "  - Maven Central sync: 30 minutes ~ 2 hours"
    echo "  - Search visibility: 4 hours ~ 24 hours"
    echo ""
else
    echo ""
    echo "================================================================================"
    echo -e "${RED}✗ Publishing failed!${NC}"
    echo "================================================================================"
    echo ""
    echo "Check the error messages above for details."
    echo ""
    exit 1
fi

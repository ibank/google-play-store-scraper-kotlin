.PHONY: help build clean test run install check docs publish

# Default target
help:
	@echo "Google Play Store Scraper - Kotlin"
	@echo ""
	@echo "Available targets:"
	@echo "  make build              - Build the project"
	@echo "  make clean              - Clean build artifacts"
	@echo "  make test               - Run all tests"
	@echo "  make test-unit          - Run unit tests only"
	@echo "  make check              - Run all checks (build + test)"
	@echo "  make install            - Install dependencies"
	@echo "  make docs               - Generate documentation"
	@echo "  make jar                - Build JAR file"
	@echo "  make publish-local      - Publish to local Maven repository"
	@echo "  make publish-central    - Publish to Maven Central (requires credentials)"
	@echo "  make publish-github     - Publish to GitHub Packages (requires token)"
	@echo "  make publish-info       - Show publication information"
	@echo "  make ci                 - Run CI pipeline (clean + check + build)"
	@echo "  make dev                - Setup development environment"
	@echo "  make release-prepare    - Prepare for release (build + test + docs)"

# Build the project
build:
	./gradlew build --no-daemon

# Clean build artifacts
clean:
	./gradlew clean --no-daemon
	@echo "Cleaned build artifacts"

# Run all tests
test:
	./gradlew test --no-daemon

# Run unit tests only
test-unit:
	./gradlew test --no-daemon --tests "*Test"

# Run all quality checks
check: build test
	@echo "All checks passed"

# Install dependencies
install:
	./gradlew dependencies --no-daemon
	@echo "Dependencies installed"

# Generate documentation
docs:
	./gradlew dokkaHtml --no-daemon
	@echo "Documentation: build/dokka/html/index.html"

# Build JAR file
jar:
	./gradlew jar --no-daemon
	@echo "JAR file: build/libs/*.jar"

# Publish to local Maven repository
publish-local:
	./gradlew publishToMavenLocal --no-daemon
	@echo "Published to local Maven repository (~/.m2/repository)"
	@echo "Check: ~/.m2/repository/com/modern/playscraper/play-store-scraper/"

# Publish to Maven Central (Central Portal)
publish-central:
	@echo "Publishing to Maven Central (Central Portal)..."
	@echo "Make sure you have configured credentials in ~/.gradle/gradle.properties"
	./gradlew publishAllPublicationsToCentralPortalRepository --no-daemon
	@echo "Published to Maven Central (will be available in 10-30 minutes)"

# Publish to GitHub Packages
publish-github:
	@echo "Publishing to GitHub Packages..."
	./gradlew publishAllPublicationsToGitHubPackagesRepository --no-daemon
	@echo "Published to GitHub Packages"

# Show publication information
publish-info:
	./gradlew publicationInfo --no-daemon

# Prepare for release
release-prepare: clean build test jar docs
	@echo "Release preparation completed"
	@echo "  JAR: build/libs/*.jar"
	@echo "  Docs: build/dokka/html/"
	@echo ""
	@echo "Next steps:"
	@echo "  1. Review CHANGELOG.md"
	@echo "  2. Update version in build.gradle.kts"
	@echo "  3. Create git tag: git tag v1.0.0"
	@echo "  4. Push tag: git push origin v1.0.0"
	@echo "  5. Publish: make publish-central"

# Run CI pipeline
ci: clean build test
	@echo "CI pipeline completed successfully"

# Setup development environment
dev:
	@echo "Setting up development environment..."
	chmod +x gradlew
	./gradlew wrapper --gradle-version 8.5 --no-daemon
	./gradlew dependencies --no-daemon
	@echo "Development environment ready"

# Quick development cycle
quick: test
	@echo "Quick checks passed"

# Watch mode for tests (requires entr or similar)
watch:
	@echo "Watching for changes... (requires 'entr' installed)"
	find src/main src/test -name "*.kt" | entr -c make test

# Show project info
info:
	@echo "Project: Google Play Store Scraper - Kotlin"
	@echo "Kotlin version: 1.9.20"
	@echo "JVM target: 17"
	@echo ""
	./gradlew -version

# Dependency updates check
deps-update:
	./gradlew dependencyUpdates --no-daemon
	@echo "Dependency updates report: build/dependencyUpdates/report.txt"

# Generate release
release: ci jar docs
	@echo "Release build completed"
	@echo "  JAR: build/libs/*.jar"
	@echo "  Docs: build/dokka/html/"

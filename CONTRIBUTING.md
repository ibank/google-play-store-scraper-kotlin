# Contributing to Google Play Store Scraper - Kotlin

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to this project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inclusive experience for everyone. We expect all contributors to:

- Use welcoming and inclusive language
- Be respectful of differing viewpoints and experiences
- Gracefully accept constructive criticism
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, trolling, or discriminatory comments
- Publishing others' private information
- Other conduct which could reasonably be considered inappropriate

## Getting Started

### Prerequisites

- JDK 17 or higher
- Kotlin 1.9.20 or higher
- Gradle 8.5 or higher
- Git

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:

```bash
git clone https://github.com/ibank/google-play-store-scraper-kotlin.git
cd google-play-store-scraper-kotlin
```

3. Add the upstream repository:

```bash
git remote add upstream https://github.com/ibank/google-play-store-scraper-kotlin.git
```

## Development Setup

### Build the Project

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

### Run Examples

```bash
./gradlew run -PmainClass=com.modern.playscraper.examples.BasicUsageExampleKt
```

### IDE Setup

#### IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. File → Open → Select the project directory
3. Wait for Gradle sync to complete
4. Install Kotlin plugin if not already installed

#### VS Code

1. Install Kotlin and Gradle extensions
2. Open the project folder
3. Run Gradle tasks from the Gradle panel

## How to Contribute

### Types of Contributions

1. **Bug Fixes** - Fix reported bugs or issues
2. **New Features** - Add new functionality
3. **Documentation** - Improve or add documentation
4. **Examples** - Create new usage examples
5. **Tests** - Add or improve test coverage
6. **Performance** - Optimize existing code
7. **Refactoring** - Improve code quality without changing functionality

### Before You Start

1. Check existing issues and pull requests to avoid duplicates
2. Create an issue to discuss major changes before implementing
3. Keep changes focused and atomic
4. Update documentation for any user-facing changes

## Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class AppDetailsParser(
    private val scriptDataParser: ScriptDataParser,
    private val jsonPathProcessor: JsonPathProcessor
) {
    suspend fun parse(html: String): Result<AppDetails> {
        // Implementation
    }
}

// ❌ Bad
class appDetailsParser(scriptDataParser: ScriptDataParser,jsonPathProcessor: JsonPathProcessor){
suspend fun Parse(HTML:String):Result<AppDetails>{
//implementation
}}
```

### Clean Architecture Principles

- **Domain Layer**: Pure business logic, no framework dependencies
- **Data Layer**: Repository implementations, data sources
- **Infrastructure Layer**: Framework-specific code (HTTP, parsers, etc.)

```
domain/     ← No dependencies on other layers
   ↑
data/       ← Depends on domain
   ↑
infrastructure/  ← Depends on domain and data
```

### Naming Conventions

- **Classes**: PascalCase (`AppDetailsParser`)
- **Functions**: camelCase (`getAppDetails`)
- **Constants**: UPPER_SNAKE_CASE (`DEFAULT_TIMEOUT`)
- **Packages**: lowercase (`com.modern.playscraper`)

### Documentation

- Add KDoc comments for public APIs:

```kotlin
/**
 * Fetches detailed information about a Google Play Store app.
 *
 * @param appId The package name of the app (e.g., "com.example.app")
 * @param language ISO 639-1 language code (e.g., "en", "ko")
 * @param country ISO 3166 country code (e.g., "us", "kr")
 * @return Result containing AppDetails on success or error on failure
 */
suspend fun getAppDetails(
    appId: String,
    language: String = "en",
    country: String = "us"
): Result<AppDetails>
```

### Error Handling

- Use Kotlin's `Result` type for operations that can fail
- Provide meaningful error messages
- Log errors appropriately

```kotlin
// ✅ Good
return runCatching {
    val response = httpClient.get(url)
    parser.parse(response)
}.onFailure { error ->
    logger.error("Failed to fetch app details for $appId", error)
}

// ❌ Bad
try {
    val response = httpClient.get(url)
    return parser.parse(response)
} catch (e: Exception) {
    return null
}
```

## Testing Guidelines

### Test Structure

```kotlin
class AppDetailsParserTest {
    @Test
    fun `should parse app title correctly`() {
        // Given
        val html = loadTestHtml("sample_app.html")
        val parser = AppDetailsParser(scriptDataParser, jsonPathProcessor)

        // When
        val result = parser.parse(html)

        // Then
        result.onSuccess { details ->
            assertEquals("Sample App Name", details.title)
        }
    }
}
```

### Test Coverage

- Aim for 70%+ code coverage
- Focus on business logic and critical paths
- Test edge cases and error conditions

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AppDetailsParserTest

# Run with coverage
./gradlew test jacocoTestReport
```

## Pull Request Process

### Before Submitting

1. ✅ Code compiles without errors
2. ✅ All tests pass
3. ✅ Code follows style guidelines
4. ✅ Documentation is updated
5. ✅ Commit messages are clear
6. ✅ No unnecessary files included

### PR Title Format

Use conventional commits format:

```
feat: Add support for app version history
fix: Correct review parsing for ds:11
docs: Update README with new examples
refactor: Simplify HTTP client configuration
test: Add tests for AppReviewsParser
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Related Issue
Fixes #123

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Refactoring
- [ ] Performance improvement

## Testing
- [ ] Added new tests
- [ ] All existing tests pass
- [ ] Manually tested

## Checklist
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Changelog updated
```

### Review Process

1. Maintainers will review your PR
2. Address feedback and requested changes
3. Once approved, your PR will be merged
4. Your contribution will be credited in the changelog

## Issue Guidelines

### Bug Reports

Use the bug report template:

```markdown
**Describe the bug**
A clear description of the bug

**To Reproduce**
Steps to reproduce:
1. Create scraper with...
2. Call getAppDetails with...
3. See error

**Expected behavior**
What you expected to happen

**Actual behavior**
What actually happened

**Environment**
- OS: [e.g., macOS 13.0]
- Kotlin version: [e.g., 1.9.20]
- Library version: [e.g., 1.0.0]

**Additional context**
Any other relevant information
```

### Feature Requests

```markdown
**Feature Description**
Clear description of the proposed feature

**Use Case**
Why this feature would be useful

**Proposed API**
Example of how the feature would be used

**Alternatives Considered**
Other approaches you've considered
```

## Development Workflow

### Branch Naming

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation
- `refactor/` - Code refactoring
- `test/` - Test improvements

Examples:
- `feature/add-app-version-history`
- `fix/review-parsing-ds11`
- `docs/update-readme-examples`

### Commit Messages

Follow conventional commits:

```
type(scope): subject

body (optional)

footer (optional)
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Tests
- `chore`: Build/tooling

Example:
```
feat(reviews): add pagination support for reviews

Implement pagination to fetch more than 20 reviews by making
multiple requests with continuation tokens.

Closes #45
```

### Sync with Upstream

Keep your fork up to date:

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

## Questions?

- Open an issue with the `question` label
- Join our discussions on GitHub Discussions
- Contact maintainers via email

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing! 🎉

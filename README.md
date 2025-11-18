# Google Play Store Scraper - Kotlin

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![JVM](https://img.shields.io/badge/JVM-17-orange.svg)](https://openjdk.java.net/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A.svg?logo=gradle)](https://gradle.org)

Modern, robust Kotlin library for scraping Google Play Store data with coroutines, Flow, and Clean Architecture principles.

> **📦 Maven Central**: Publishing guide available for contributors - see [claudedocs/CENTRAL_PORTAL_MIGRATION.md](claudedocs/CENTRAL_PORTAL_MIGRATION.md)

> **🚨 CRITICAL LEGAL WARNING**: This library is intended for **educational and research purposes ONLY**. Automated access to Google Play Store **VIOLATES** Google's Terms of Service and may violate DMCA, CFAA, and other laws. Users face potential **criminal prosecution** and **civil liability**. You are **solely responsible** for all legal consequences. Read [LEGAL_NOTICE.md](LEGAL_NOTICE.md) before use. The authors assume **NO liability**.

> **⚠️ Trademark Notice**: Google Play and the Google Play logo are trademarks of Google LLC. This project is **NOT affiliated with, endorsed by, or sponsored by Google LLC**. All trademarks are the property of their respective owners.

## ✨ Features

- 🎯 **App Details**: Fetch comprehensive app information (ratings, reviews, developer info, etc.)
- 💬 **Reviews**: Get user reviews with full metadata and developer replies
- 🔍 **Search**: Search apps by query
- 📱 **Developer Apps**: List all apps from a specific developer
- 🏷️ **Categories**: Browse apps by category and collection
- 🌍 **Multi-region Support**: Works with any country/language combination
- ⚡ **Modern Architecture**: Built with Kotlin Coroutines and Clean Architecture
- 🔄 **Request Throttling**: Configurable delays to minimize server load
- 📊 **Updated for 2025**: Supports latest Google Play structure changes (ds:5, ds:11)

## 🚀 Quick Start

### Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.modern.playscraper:play-store-scraper:1.0.0")
}
```

### Basic Usage

```kotlin
import com.modern.playscraper.PlayStoreScraper
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val scraper = PlayStoreScraper()

    // Get app details
    scraper.getAppDetails("com.example.myapp").onSuccess { details ->
        println("App: ${details.title}")
        println("Rating: ${details.score}")
        println("Downloads: ${details.minInstalls}+")
    }

    // Get reviews
    scraper.getAppReviews(
        appId = "com.example.myapp",
        limit = 10
    ).onSuccess { reviews ->
        reviews.forEach { review ->
            println("${review.userName}: ${review.text}")
        }
    }

    scraper.close()
}
```

### Multi-region Support

```kotlin
// Get app details for different regions and languages
scraper.getAppDetails(
    appId = "com.example.myapp",
    language = "ko",  // Korean
    country = "kr"     // South Korea
).onSuccess { details ->
    println("App: ${details.title}")
}

// Or fetch in Traditional Chinese for Taiwan region
scraper.getAppDetails(
    appId = "com.example.myapp",
    language = "zh-TW",
    country = "tw"
).onSuccess { details ->
    println("App: ${details.title}")
}
```

## 📊 What's New in This Version

### Google Play Structure Updates

This library is updated for the latest Google Play Store structure (as of November 2025):

| Feature | Old Structure | New Structure | Status |
|---------|---------------|---------------|--------|
| App Data | `ds:4` | `ds:5` | ✅ Updated |
| Review Data | `ds:2` | `ds:11` | ✅ Updated |
| HTTP Headers | Multiple headers | Standard headers | ✅ Implemented |
| Request Delay | 5-8s | 1-2s (configurable) | ✅ Optimized |

## 🏗️ Architecture

This project follows Clean Architecture principles:

```
├── domain/          # Business logic and models
│   ├── model/      # Domain entities
│   └── usecase/    # Use cases
├── data/           # Data layer
│   ├── datasource/ # Remote data sources
│   └── repository/ # Repository implementations
└── infrastructure/ # Framework & drivers
    ├── http/       # HTTP client & throttling
    └── parser/     # HTML/JSON parsers
```

## 🔧 Advanced Configuration

### Custom Configuration

```kotlin
val scraper = PlayStoreScraper(
    config = PlayStoreScraper.Config(
        minimalHeaders = true,  // Use standard HTTP headers
        throttler = HumanBehaviorRequestThrottler(
            baseDelayMs = 1000,
            jitterMinMs = 0,
            jitterMaxMs = 1000
        ),
        retryPolicy = RetryPolicy.Conservative,
        enableLogging = false
    )
)
```

### Technical Implementation

This library uses standard HTTP requests with configurable settings:

- **Standard Headers**: Uses common browser User-Agent strings for compatibility
- **Request Throttling**: Configurable delays (1-2 seconds default) to reduce server load
- **Retry Logic**: Exponential backoff for failed requests
- **Clean Architecture**: Separation of concerns for maintainability

⚠️ **WARNING**: Automated access may violate Google Play Terms of Service. See [LEGAL_NOTICE.md](LEGAL_NOTICE.md) for details.

## 📝 API Reference

### Get App Details

```kotlin
suspend fun getAppDetails(
    appId: String,
    language: String = "en",
    country: String = "us"
): Result<AppDetails>
```

### Get App Reviews

```kotlin
suspend fun getAppReviews(
    appId: String,
    sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
    language: String = "en",
    country: String = "us",
    limit: Int = 20
): Result<List<AppReview>>
```

### Search Apps

```kotlin
suspend fun searchApps(
    query: String,
    language: String = "en",
    country: String = "us",
    limit: Int = 20
): Result<List<App>>
```

### Get Developer Apps

```kotlin
suspend fun getDeveloperApps(
    developerId: String,
    language: String = "en",
    country: String = "us",
    limit: Int = 20
): Result<List<App>>
```

### Get Category Apps

```kotlin
suspend fun getCategoryApps(
    category: Category,
    collection: Collection = Collection.TOP_FREE,
    language: String = "en",
    country: String = "us",
    limit: Int = 20
): Result<List<App>>
```

## 🛠️ Development

### Prerequisites

- JDK 17 or higher
- Kotlin 1.9.20 or higher
- Gradle 8.5 or higher

### Build Commands

This project includes a Makefile for convenient management:

```bash
# Show all available commands
make help

# Build the project
make build

# Run tests
make test

# Run all checks (build + test)
make check

# Build JAR file
make jar

# Generate documentation
make docs

# Run CI pipeline (clean + build + test)
make ci

# Setup development environment
make dev

# Prepare for release (clean + build + test + jar + docs)
make release-prepare

# Publish to local Maven repository (testing)
make publish-local

# Publish to Maven Central (requires credentials)
make publish-central

# Publish to GitHub Packages
make publish-github
```

## 📚 Documentation

### Primary Documentation
- **[README.md](README.md)** - This file: Project overview and quick start
- **[LEGAL_NOTICE.md](LEGAL_NOTICE.md)** - **Required reading**: Comprehensive legal warnings (600+ lines)
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and detailed changes
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Contribution guidelines and development workflow
- **[LICENSE](LICENSE)** - MIT License

### Publishing & Deployment
For information on publishing to Maven Central, see:
- Maven Central Portal migration guide (2025 process)
- GitHub Actions automated publishing
- GPG signing and credentials setup

All publishing documentation is included in the project for contributors.

## ⚖️ Legal & Ethical Considerations

### 🚨 Critical Legal Risks

**READ [LEGAL_NOTICE.md](LEGAL_NOTICE.md) BEFORE USE**

This library performs automated web scraping which:
- ❌ **VIOLATES** Google Play Terms of Service
- ⚠️ May violate DMCA Section 1201 (Anti-Circumvention) - **Federal Crime**
- ⚠️ May violate CFAA (Computer Fraud and Abuse Act) - **Federal Crime**
- ⚠️ May violate GDPR and privacy laws
- ⚠️ May result in criminal prosecution, fines up to $500,000, and imprisonment

**Users are SOLELY responsible** for all legal consequences.

### Legal Alternatives (Recommended)

Before using this library, consider these **legal alternatives**:

1. **Google Play Developer API** (Official, Legal)
   - For managing your own apps
   - https://developers.google.com/android-publisher
   - ✅ Legal, ✅ Free, ❌ Limited to your apps only

2. **Commercial API Services** (Legal, Paid)
   - SerpApi: https://serpapi.com/google-play-api
   - DataForSEO: https://dataforseo.com/apis/app-data-api
   - SearchAPI: https://www.searchapi.io/google-play
   - ✅ Legal, ✅ Full data, 💰 $50-500+/month

3. **This Library** (Educational/Research Only)
   - ❌ Violates ToS, ⚠️ Criminal risk, ✅ Free, ✅ Full features
   - **ONLY for genuine academic research with institutional oversight**

### GDPR and Privacy Compliance

⚠️ **Personal Data Warning**: App reviews contain user names and personal information.

When collecting review data, you must:
- Document your legitimate interest basis
- Implement data minimization
- Provide privacy notices
- Comply with GDPR/CCPA requirements
- Delete data when no longer needed

**Failure to comply may result in fines up to €20M or 4% global revenue.**

### Recommended Use Cases (Educational Only)

✅ **Potentially defensible uses:**
- Academic research with IRB approval
- Educational coursework (computer science)
- Personal learning (non-commercial)
- Testing your own apps (use official API instead)

❌ **High-risk uses:**
- Any commercial purpose
- Competitive intelligence
- Market research for business
- Data resale or aggregation
- Automated monitoring services

### Responsible Use Guidelines

If you must use this library despite the risks:

- ✅ Use conservative delays (minimum 5-10 seconds between requests)
- ✅ Limit to 10-50 requests per day maximum
- ✅ Respect robots.txt (if checking manually)
- ✅ Use honest User-Agent identification when possible
- ✅ Document legitimate research purpose
- ✅ Obtain institutional/legal approval first
- ❌ Never use for commercial purposes
- ❌ Never resell or redistribute collected data

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for:

- Code of Conduct
- Development setup
- Coding standards
- Pull request process
- Issue guidelines

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

This library was inspired by:

- [google-play-scraper](https://github.com/facundoolano/google-play-scraper) - Node.js module for Google Play scraping

## 🔗 Related Projects

- [google-play-scraper (Node.js)](https://github.com/facundoolano/google-play-scraper) - Original inspiration
- [Google Play Developer API](https://developers.google.com/android-publisher) - Official API (recommended for production)

## ⚠️ Disclaimer

This software is provided "as is", without warranty of any kind, express or implied. The authors or copyright holders shall not be liable for any claim, damages, or other liability arising from the use of this software.

**Use at your own risk and responsibility.**

---

**Made with ❤️ using Kotlin and Clean Architecture**

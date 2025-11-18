# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-18

### Added
- App details fetching with comprehensive metadata
- User reviews with full metadata and developer replies
- Search functionality for apps
- Developer apps listing
- Category and collection browsing
- Multi-region support (200+ language/country combinations)
- Clean Architecture with Kotlin Coroutines and Flow
- Request throttling with configurable delays (1-2s default)
- Retry policies (Default, Aggressive, Conservative)
- Maven Central Portal publishing support (2025 process)
- GitHub Actions CI/CD (multi-platform testing, automated publishing)
- Makefile for build automation
- GPG signing for Maven Central
- Comprehensive legal documentation (LEGAL_NOTICE.md)
- GDPR compliance guidelines

### Technical
- Updated for Google Play Store structure (November 2025)
  - App data: `ds:5` (from `ds:4`)
  - Review data: `ds:11` (from `ds:2`)
- Kotlin 1.9.20, JVM 17, Gradle 8.5
- Ktor 2.3.5, Koin 3.5.0, Gson 2.10.1, Jsoup 1.16.2

### Changed
- Migrated from OSSRH to Maven Central Portal
- Updated publishing tasks and credentials

### Fixed
- Removed anti-circumvention language from code and docs
- Removed real app references from documentation
- Fixed GitHub Actions workflows for Central Portal
- Fixed Makefile task references

### Security
- Enhanced .gitignore for sensitive files (GPG keys, credentials)
- GitHub Secrets for secure CI/CD

### Legal Notice
⚠️ **This library is for educational/research purposes ONLY.** Violates Google Play ToS and may violate DMCA §1201, CFAA, GDPR. Users are solely responsible for legal consequences. See [LEGAL_NOTICE.md](LEGAL_NOTICE.md).

## [Unreleased]

### Planned Features
- Pagination support for large result sets
- App version history fetching
- Developer information detailed extraction
- What's New (changelog) parsing from app pages
- Similar apps suggestions with reasoning
- App screenshots and video URLs extraction
- Data Safety information parsing
- App ranking history tracking
- Batch operations for multiple apps
- Advanced caching with TTL
- Proxy rotation support
- Rate limiting analytics
- WebSocket-based real-time updates

### Under Consideration
- GraphQL API layer
- REST API server wrapper
- Docker containerization
- Kubernetes deployment manifests
- Prometheus metrics integration
- ELK stack logging integration
- Circuit breaker pattern for resilience
- Database persistence layer
- Admin dashboard
- Scheduled scraping jobs

---

## Version History

### Pre-release Development
- Research and analysis of Google Play Store structure
- HTTP client configuration and optimization
- Clean Architecture design and implementation
- Testing across multiple regions (US, Taiwan, Korea)
- Performance optimization and throttling fine-tuning
- Documentation and example creation

---

## Migration Guides

### From google-play-scraper (JavaScript)

```javascript
// JavaScript (google-play-scraper)
const gplay = require('google-play-scraper');
const result = await gplay.app({appId: 'com.example.app'});
```

```kotlin
// Kotlin (this library)
val scraper = PlayStoreScraper()
scraper.getAppDetails("com.example.app").onSuccess { result ->
    // Use result
}
```

### Key Differences
- **Language**: JavaScript → Kotlin
- **Async**: Promises/async-await → Coroutines
- **Error Handling**: try-catch → Result type
- **Type Safety**: Runtime types → Compile-time types
- **Architecture**: Functional → Clean Architecture
- **Data Structures**: `ds:4` → `ds:5`, `ds:2` → `ds:11`

---

## Breaking Changes

### 1.0.0
- None (initial release)

---

## Known Issues

### Current Limitations
1. **Review Pagination**: Currently limited to the first page of reviews (~20 reviews per request)
2. **Rate Limiting**: Google may still detect and rate-limit aggressive scraping
3. **Structure Changes**: Google Play structure may change without notice, requiring library updates
4. **Captcha**: No support for solving captchas if triggered

### Workarounds
1. Use conservative throttling (8-10 second delays)
2. Implement IP rotation if available
3. Monitor for structure changes and update accordingly
4. Avoid captcha triggers by mimicking human behavior

---

## Security

### Reporting Security Issues

Please report security vulnerabilities by creating a private security advisory on GitHub:

**GitHub Security Advisories**: https://github.com/ibank/google-play-store-scraper-kotlin/security/advisories

Do **not** report security issues publicly on GitHub issues to prevent exploitation before a fix is available.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

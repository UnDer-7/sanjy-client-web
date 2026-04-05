# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> **Note:** Version 0.x is considered pre-alpha. The project is under active development and testing.
> Breaking changes may occur in any release, including minor versions.

<br>

> **Important:** Follow the standard [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) structure (`## [X.Y.Z] - YYYY-MM-DD`).
> The CI/CD pipeline automatically extracts the changelog section for each version to create the GitHub Release notes.

---

## [0.2.0] - 2026-04-05

### Added

- Settings page with a Runtime Configuration section displaying active environment values
- Backend endpoint (`/api/maintenance/frontend-runtime-configuration`) exposing runtime config to the frontend
- `DateTimePickerSanjy`, `DatePickerSanjy`, and `TimePickerSanjy` components that automatically apply the user's preferred date/time format from local storage
- `APP_TITLE_REDIRECT_PATH` — configurable redirect path when clicking the application title in the header

### Fixed

- Logout not working correctly
- Meal type options displayed out of order in the frontend form
- Clear/reset action missing or broken on several form buttons
- Default landing page now configurable at runtime via environment variable (defaults to diet-plan)
- Custom meal record search filter being reset when navigating to "New Record" and returning to the search page

---

## [0.1.1] - 2026-03-23

### Changed

- Upgraded Spring Boot from 3.5.x to 4.0.4, including all necessary API and configuration adaptations
- Upgraded Spring AI from 1.0.3 to 2.0.0-M3
- Upgraded all dependencies to their latest versions (Lombok, MapStruct, iText, OkHttp, Spotless, Checkstyle, and others)
- Migrated OkHttp from 4.x to 5.x to maintain compatibility with the Anthropic AI client under Spring Boot 4
- Reorganized frontend pages into feature-based folders (`diet-plan/`, `meal/`, `settings/`)
- Extracted `ErrorLogsComponent` into its own file within the settings folder

### Fixed

- Removed security vulnerability overrides for `commons-lang3` and `commons-fileupload` that became unnecessary with Spring Boot 4
- Added SonarCloud CPD exclusion for `timezones.ts` to suppress false-positive duplicate code warnings

---

## [0.1.0] - 2026-02-06

First release. Full project restructure into a Single Page Application architecture ([#3](https://github.com/UnDer-7/sanjy-client-web/pull/3)).

### Changed

- Migrated to an SPA architecture: React (Vite) frontend with a Spring Boot Backend for Frontend (BFF), all in a single repository
- Spring Boot serves both the React SPA and the BFF API endpoints that proxy to the backend service


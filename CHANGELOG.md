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

## [0.1.0] - 2026-02-06

First release. Full project restructure into a Single Page Application architecture ([#3](https://github.com/UnDer-7/sanjy-client-web/pull/3)).

### Changed

- Migrated to an SPA architecture: React (Vite) frontend with a Spring Boot Backend for Frontend (BFF), all in a single repository
- Spring Boot serves both the React SPA and the BFF API endpoints that proxy to the backend service


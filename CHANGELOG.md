# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

### Added

### Changed

## 3.0.1 - 2026-08-26

### Fixed
- Restored the Java YAML/JSON rule body and `IAction` API to the 2.0.1-compatible contract for existing Java gateway deployments.
- Fixed serialize/read round-trip data loss of `conditionValueId`, `dateFormat`, `regexFlags`, and other legacy condition-value metadata.

### Changed
- Removed Java CEL and compact/new-spec rule-body support; CEL remains supported by the Rust rule runtime.
- Treat 3.0.0 as a withdrawn compatibility experiment. Version 3.0.1 deliberately restores the 2.0.1 Java API and is binary-incompatible with code compiled only against the removed 3.0.0 API; rebuild any such integrations before upgrading.
- Portal-published CEL bodies must not target Java instances. A CEL field in any rule rejects the entire `ruleBodies` document; initial Java gateway startup fails, while an updated light-4j reload retains the last-known-good rules and rejects that configuration snapshot.
- Retained post-2.0.1 concurrency fixes, dependency/build updates, and native operator improvements.

## 3.0.0 - 2026-04-27

### Added
- fixes #29 update rule handlers to support new spec

## 2.0.1 - 2025-02-13

### Added
- update logback to 1.5.16
- fix a typo
- fixes #21 update Rule class to reflect the latest changes

## 2.0.0 - 2024-12-25

### Added
- change the specification and add a lot of new features.

## 1.0.5 - 2024-10-22

### Added
- fixes #19 break the execution of multiple rules if one rule returns false

## 1.0.4 - 2024-10-18

### Added
- fixes #17 update rule action execution to add postPerformAction

## 1.0.3 - 2023-12-18

### Added
- fixes #15 add cache for the action object

## 1.0.2 - 2023-06-07

### Added
- fixes #11 upgrade version to 1.0.2 and dependencies

## 1.0.0 - 2021-11-16
### Added
- First version

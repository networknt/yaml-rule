# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

### Added

### Changed

## 3.0.1 - 2026-08-28

### Fixed
- Restored the 2.0.1-compatible Java YAML/JSON rule model and `IAction` API for existing Java gateway deployments ([#33](https://github.com/networknt/yaml-rule/issues/33)).
- Preserved legacy condition-value metadata, including `conditionValueId`, `dateFormat`, and `regexFlags`, when rule bodies are serialized and read back.
- Kept resolved action values local to each execution so concurrent requests do not mutate shared rule definitions.
- Made action-class initialization atomic and report missing `actionClassName` values with the affected rule and action IDs.

### Changed
- Withdrew the Java CEL and compact/new-spec rule model introduced by 3.0.0. CEL rule conditions remain available in the Rust rule runtime, but CEL rule bodies must not be deployed to Java instances.
- Restored the 2.0.1 Java API rather than retaining source or binary compatibility with the withdrawn 3.0.0 API. Integrations compiled against 3.0.0-only APIs must be updated and rebuilt before upgrading.
- Raised the build target from Java 21 to Java 25.
- Upgraded Jackson to 2.22.1 and Logback to 1.5.37.

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

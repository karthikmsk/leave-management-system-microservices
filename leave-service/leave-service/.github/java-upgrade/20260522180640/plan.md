# Upgrade Plan: leave-service (20260522180640)

- **Generated**: 2026-05-22 18:12
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 17: not available (baseline will be skipped)
- JDK 21: **<TO_BE_INSTALLED>** (required by step 1)

**Build Tools**
- Maven 3.9.9: D:\apache-maven-3.9.9\bin
- Maven Wrapper: 3.9.15 (current version compatible with Java 21)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260522180640
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java runtime to Java 21 (latest LTS)

## Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --------------------- | ------- | -------------- | ---------------- |
| Java | 17 | 21 | User requested latest LTS runtime upgrade |
| Spring Boot | 3.2.5 | 3.2.5 | Already compatible with Java 21 |
| Maven Wrapper | 3.9.15 | 3.9.0 | Compatible with Java 21 |
| maven-compiler-plugin | 3.11.0 | 3.11.0 | Supports Java 21 compilation |
| Spring Cloud Dependencies | 2023.0.1 | 2023.0.1 | Compatible with Spring Boot 3.2.x |

## Derived Upgrades

- Java source and target levels must be raised from 17 to 21 in `pom.xml` to match the new runtime.
- The `java.version` property must be updated from 17 to 21 so build plugins and Spring Boot use the correct runtime level.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|-----------|---------|--------|--------|--------|
| pom.xml | `java.version` property | 17 | upgrade | 21 | Align build and runtime with Java 21 |
| pom.xml | `maven-compiler-plugin` `<source>` | 17 | upgrade | 21 | Compile source code for Java 21 |
| pom.xml | `maven-compiler-plugin` `<target>` | 17 | upgrade | 21 | Generate Java 21 bytecode |

### Source Code Changes

No source code changes are required for this runtime-only upgrade. A scan did not identify any `javax.*`, internal JDK API, or reflection compatibility issues in `src/`.

### Configuration Changes

No additional application or configuration changes are required.

### CI/CD Changes

No CI/CD files were detected in the repository, so no CI/CD changes are required as part of this plan.

### Risks & Warnings

- **Baseline skip**: The current JDK 17 installation was not found on the machine, so baseline verification must be skipped. The upgrade plan therefore includes an explicit JDK 21 install and validation under the target runtime.
- **Runtime compatibility**: Spring Boot 3.2.5 is compatible with Java 21, but the upgrade should be validated with full test execution to catch any Java 21-specific issues in annotation processing or runtime behavior.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Java 21 is required for the target runtime and current local JDKs are unavailable.
  - **Changes to Make**: Install JDK 21 and verify the Maven Wrapper can run under it.
  - **Verification**: `java -version && ./mvnw -v` on Windows using the installed JDK 21; expected result is Java 21 output and Maven Wrapper startup.

- Step 2: Setup Baseline (Skipped)
  - **Rationale**: The base JDK 17 is not available on the machine, so a current-state baseline cannot be executed.
  - **Changes to Make**: None.
  - **Verification**: skipped due to unavailable base JDK.

- Step 3: Upgrade Project Java Runtime
  - **Rationale**: Raise build and runtime Java levels to the target LTS version.
  - **Changes to Make**: Update `pom.xml` `java.version` to 21 and set `maven-compiler-plugin` `<source>`/`<target>` to 21.
  - **Verification**: `./mvnw clean test-compile -q` under JDK 21; expected result is successful compilation of main and test source.

- Step 4: Final Validation
  - **Rationale**: Ensure the upgraded runtime works end to end with the existing test suite.
  - **Changes to Make**: No additional code changes unless tests reveal Java 21 compatibility issues.
  - **Verification**: `./mvnw clean test -q` under JDK 21; expected result is all tests passing.

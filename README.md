# Selenium Cucumber Test Automation Framework

A comprehensive **BDD Test Automation Framework** built with Selenium WebDriver, Cucumber, and Java. This framework demonstrates industry best practices including Page Object Model, data-driven testing, and parallel execution support.

## Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Test Reports](#test-reports)
- [Key Features](#key-features)
- [Framework Architecture](#framework-architecture)

## Project Overview

This framework automates testing for the OrangeHRM demo application, demonstrating:
- Login functionality testing
- User search and filter operations
- Personal information management (My Info)
- File upload/attachment handling
- Data-driven testing with Excel integration

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11+ | Programming language |
| Selenium WebDriver | 4.15+ | Browser automation |
| Cucumber | 7.x | BDD framework |
| JUnit 5 | 5.10+ | Test runner |
| Maven | 3.6+ | Build and dependency management |
| Apache POI | 5.2+ | Excel file handling |
| iText PDF | 7.2+ | PDF report generation |
| WebDriverManager | 5.6+ | Automatic driver management |

## Project Structure

```
src/
├── test/
│   ├── java/com/automation/
│   │   ├── drivers/          # WebDriver management (ThreadLocal)
│   │   │   └── DriverFactory.java
│   │   ├── pages/            # Page Object Model classes
│   │   │   ├── BasePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── SearchAndVerifyPage.java
│   │   │   └── MyInfoPage.java
│   │   ├── stepdefinitions/  # Cucumber step definitions
│   │   │   ├── LoginSteps.java
│   │   │   ├── SearchAndVerifySteps.java
│   │   │   └── MyInfoSteps.java
│   │   ├── hooks/            # Before/After hooks
│   │   │   └── ApplicationHooks.java
│   │   ├── helpers/          # Reusable helper methods
│   │   │   └── ElementHelper.java
│   │   ├── utilities/        # Utility classes
│   │   │   ├── ConfigReader.java
│   │   │   ├── ExcelReader.java
│   │   │   ├── EmailHelper.java
│   │   │   └── ScreenshotHelper.java
│   │   ├── reports/          # Report generators
│   │   │   ├── PdfReportGenerator.java
│   │   │   ├── HtmlReportGenerator.java
│   │   │   └── TestResultCollector.java
│   │   └── runners/          # Test runner
│   │       └── TestRunner.java
│   └── resources/
│       ├── features/         # Cucumber feature files
│       │   ├── Login.feature
│       │   ├── SearchAndVerify.feature
│       │   └── MyInfo.feature
│       ├── testdata/         # Test data files
│       │   ├── Test Data.xlsx
│       │   └── TestDocument.pdf
│       └── config.properties
└── pom.xml
```

## Prerequisites

- **Java JDK 11** or higher
- **Maven 3.6** or higher
- **IDE**: Eclipse, IntelliJ IDEA, or VS Code
- **Browser**: Chrome, Firefox, or Edge

## Setup Instructions

### Option 1: Eclipse IDE

1. Open Eclipse
2. Go to **File → Import**
3. Select **Maven → Existing Maven Projects**
4. Browse to the project directory
5. Click **Finish**
6. Wait for Maven to download dependencies

### Option 2: Command Line

```bash
# Clone or download the project
cd "path/to/project"

# Install dependencies
mvn clean install -DskipTests
```

## Running Tests

### Run All Tests

```bash
mvn clean test
```

### Run by Tags

```bash
# Run smoke tests
mvn test -Dcucumber.filter.tags="@Smoke"

# Run regression tests
mvn test -Dcucumber.filter.tags="@Regression"

# Run specific feature
mvn test -Dcucumber.filter.tags="@Login"
mvn test -Dcucumber.filter.tags="@MyInfo"
mvn test -Dcucumber.filter.tags="@Search"

# Combine tags
mvn test -Dcucumber.filter.tags="@Smoke and @Login"

# Exclude tags
mvn test -Dcucumber.filter.tags="not @Ignore"
```

### Run Specific Feature File

```bash
mvn test -Dcucumber.features="src/test/resources/features/Login.feature"
```

### Run in Headless Mode

```bash
mvn test -Dbrowser=headless
```

## Configuration

Edit `src/test/resources/config.properties`:

```properties
# Browser: chrome, firefox, edge, headless
browser=chrome

# Base URL
baseUrl=https://opensource-demo.orangehrmlive.com

# Timeouts (seconds)
defaultTimeout=10
pageLoadTimeout=30
implicitWait=5

# Headless mode
headlessMode=false

# Email reports (optional)
email.enabled=false
```

## Test Reports

After test execution, reports are generated in:

| Report Type | Location |
|-------------|----------|
| HTML Report | `target/reports/TestReport_[timestamp].html` |
| PDF Report | `target/reports/TestReport_[timestamp].pdf` |
| Cucumber Report | `target/cucumber-reports/cucumber.html` |

## Key Features

### Page Object Model (POM)
- Clean separation of test logic and page elements
- Reusable page methods
- Maintainable locator management

### Data-Driven Testing
- Excel integration using Apache POI
- External test data management
- Easy data updates without code changes

### Robust Element Handling
- Auto-wait mechanisms
- Retry logic for flaky elements
- Multiple locator strategies

### Parallel Execution
- ThreadLocal WebDriver management
- Thread-safe test execution
- Reduced execution time

### Comprehensive Reporting
- Custom HTML reports with screenshots
- PDF reports for stakeholders
- Email notification support

### Cross-Browser Support
- Chrome, Firefox, Edge
- Headless execution option
- WebDriverManager for automatic driver setup

## Framework Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Test Runner (JUnit 5)                │
├─────────────────────────────────────────────────────────┤
│                  Cucumber Feature Files                  │
├─────────────────────────────────────────────────────────┤
│                   Step Definitions                       │
├─────────────────────────────────────────────────────────┤
│                    Page Objects                          │
├─────────────────────────────────────────────────────────┤
│          ElementHelper / Utilities / Helpers             │
├─────────────────────────────────────────────────────────┤
│               DriverFactory (ThreadLocal)                │
├─────────────────────────────────────────────────────────┤
│                  Selenium WebDriver                      │
└─────────────────────────────────────────────────────────┘
```

## Test Tags Reference

| Tag | Description |
|-----|-------------|
| `@Smoke` | Quick smoke tests |
| `@Regression` | Full regression suite |
| `@Login` | Login functionality |
| `@MyInfo` | Personal info page tests |
| `@Search` | Search functionality |
| `@Attachment` | File upload tests |
| `@ExcelData` | Data-driven tests |
| `@Negative` | Negative test cases |
| `@Ignore` | Skipped tests |

## Test Application

This framework tests the **OrangeHRM Demo** application:
- URL: https://opensource-demo.orangehrmlive.com
- Username: `Admin`
- Password: `admin123`

---

**Author**: Vishal Lodhiya  
**Framework Version**: 1.0

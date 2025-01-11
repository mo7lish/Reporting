# Android Reporting Application

## Overview
This Android application is designed for submitting and managing reports. It provides functionality for users to create, view, and manage reports with features including location tagging, image attachments, and detailed report information.

## Features
- Report submission with type selection
- Location attachment using GPS coordinates
- Image attachment capabilities
- Report details viewing
- Report management (including deletion)
- Status tracking for reports

## Project Structure
```
app/
├── java/com/example/reporting/
│   ├── ReportPage.java              # Main entry point/landing page
│   ├── ReportSubmissionActivity.java # Handles report creation
│   ├── ReportDetailsActivity.java    # Displays report details
│   ├── Report.java                  # Report data model
│   └── Various utility classes
├── res/
│   └── Resource files (layouts, values, etc.)
└── AndroidManifest.xml              # App configuration
```

## Required Permissions
- ACCESS_FINE_LOCATION: For precise location tracking
- ACCESS_COARSE_LOCATION: For approximate location tracking
- READ_MEDIA_IMAGES: For accessing and attaching images

## Main Components

### ReportSubmissionActivity
- Handles creation of new reports
- Manages image attachments
- Handles location services
- Implements form validation
- Manages report submission process

### ReportDetailsActivity
- Displays detailed report information
- Provides report management options
- Handles report deletion

### Report Model
- Stores report information including:
  - Report ID
  - Report Type
  - Report Details
  - Location Data
  - Image URIs
  - User Information
  - Date and Status

## Libraries and Dependencies
From the build configuration:
- AndroidX Core KTX
- AppCompat
- Material Design Components
- ConstraintLayout
- Firebase libraries for backend services
- Location services
- Image processing libraries

## Build and Configuration
The project uses Gradle with Kotlin DSL for build configuration. It targets modern Android devices and implements Material Design principles for the user interface.
# Report Management App Implementation Guide

The app consists of two main activities that work together to manage reports:

## ReportPage Activity
- Main activity that displays the list of reports using RecyclerView
- Uses ActivityResultLauncher to handle report submission results
- Implements persistent storage using SharedPreferences and Gson
- Updates the UI automatically when new reports are added

## ReportSubmissionActivity
- Handles the creation of new reports
- Returns the created report to ReportPage using setResult
- Validates input before submission

## Data Flow
1. ReportPage loads existing reports from SharedPreferences on creation
2. When "Add Report" is clicked, ReportSubmissionActivity is launched
3. After report submission, the result is handled by ReportPage's ActivityResultLauncher
4. New reports are added to the list, displayed in RecyclerView, and saved to SharedPreferences

## Important Components
- `ReportAdapter`: Handles the display of reports in the RecyclerView
- `Report`: Model class that must implement Serializable
- SharedPreferences: Used for persistent storage of reports
- Gson: Used for JSON serialization/deserialization

## Required Dependencies
Make sure these dependencies are in your app's build.gradle:
```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.recyclerview:recyclerview:1.3.0'
implementation 'com.google.code.gson:gson:2.10.1'
```
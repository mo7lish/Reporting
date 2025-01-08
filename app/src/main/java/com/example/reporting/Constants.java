package com.example.reporting;

public class Constants {
    // Permission request codes
    public static final int LOCATION_PERMISSION_REQUEST = 1000;
    public static final int IMAGE_PERMISSION_REQUEST = 1001;
    public static final int IMAGE_PICK_CODE = 1002;

    // Image size limits
    public static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1MB

    // Report status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
}
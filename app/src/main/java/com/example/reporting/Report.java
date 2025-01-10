package com.example.reporting;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Report implements Serializable {
    private ArrayList<String> imageUris = new ArrayList<>();
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getLocation() {
        return location;
    }
    
    public List<String> getMediaUrls() {
        return mediaUrls;
    }
    
    public void addMediaUrl(String url) {
        if (mediaUrls == null) {
            mediaUrls = new ArrayList<>();
        }
        mediaUrls.add(url);
    }

    private String reportId;
    private String location;
    private List<String> mediaUrls = new ArrayList<>();
    private String reportNumber;
    private String address;
    private String reportType;
    private String reportDetails;
    private String name;
    private String username;
    private String date;
    private String phoneNumber;
    private ArrayList<String> attachedImages;

    private String status;

    // Constructor to initialize the Report object
    public Report(String reportId, String reportType, String reportDetails, String name, String username, String date, String phoneNumber, String status) {
        this.mediaUrls = new ArrayList<>();
        this.reportId = reportId;
        this.reportType = reportType;
        this.reportDetails = reportDetails;
        this.name = name;
        this.username = username;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.status = "Pending"; // Default status
        this.location = String.valueOf(new Location("0", "0"));
        this.mediaUrls = new ArrayList<>();
    }

    // Getters and setters for each field
    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getImageUris() {
        return imageUris != null ? imageUris : new ArrayList<>();
    }

    public void setImageUris(ArrayList<String> imageUris) {
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public ArrayList<String> getMediaUris() {
        return attachedImages;
    }
    
    public void setMediaUris(ArrayList<String> mediaUris) {
        this.attachedImages = mediaUris;
    }
    
    public ArrayList<String> getAttachedImages() {
        return attachedImages;
    }
    
    public void setAttachedImages(ArrayList<String> attachedImages) {
        this.attachedImages = attachedImages;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {  // Getter for status
        return status;
    }

    public void setStatus(String status) {  // Setter for status
        this.status = status;
    }
    
    // Removed duplicate and unused methods

    // You can add any other methods if necessary, like toString() for debugging
    @Override
    public String toString() {
        return "Report ID: " + reportId + "\n" +
               "Type: " + reportType + "\n" +
               "Details: " + reportDetails + "\n" +
               "Name: " + name + "\n" +
               "Username: " + username + "\n" +
               "Date: " + date + "\n" +
               "Phone: " + phoneNumber;
    }

    // Serialize the Report object to JSON using Gson
    public static String toJson(Report report) {
        return new Gson().toJson(report);
    }

    // Deserialize JSON to Report object
    public static Report fromJson(String json) {
        return new Gson().fromJson(json, Report.class);
    }
}

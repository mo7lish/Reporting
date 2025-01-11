package com.example.reporting;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;

public class Report implements Serializable {
    private String reportId;
    private String reportType;
    private String reportDetails;
    private String name;
    private String username;
    private String date;
    private String phoneNumber;
    private String status;
    private Location location;
    private String address;
    private ArrayList<String> mediaUris;
    private ArrayList<String> mediaUrls;

    public Report(String reportId, String reportType, String reportDetails, String name, 
                 String username, String date, String phoneNumber, String status) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.reportDetails = reportDetails;
        this.name = name;
        this.username = username;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.mediaUris = new ArrayList<>();
        this.mediaUrls = new ArrayList<>();
    }

    // Location methods
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Media handling methods
    public ArrayList<String> getImageUris() {
        return mediaUris;
    }

    public void setImageUris(ArrayList<String> uris) {
        this.mediaUris = uris != null ? uris : new ArrayList<>();
    }

    public void addMediaUri(String uri) {
        if (uri != null) {
            mediaUris.add(uri);
        }
    }

    public ArrayList<String> getMediaUrls() {
        return mediaUrls;
    }

    public void addMediaUrl(String url) {
        if (url != null) {
            mediaUrls.add(url);
        }
    }

    // Other getters and setters remain unchanged
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reportType='" + reportType + '\'' +
                ", reportDetails='" + reportDetails + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", date='" + date + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static String toJson(Report report) {
        return new Gson().toJson(report);
    }

    public static Report fromJson(String json) {
        return new Gson().fromJson(json, Report.class);
    }
}
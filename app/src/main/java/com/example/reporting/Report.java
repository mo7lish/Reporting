package com.example.reporting;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;



public class Report implements Serializable {

    private int reportId;
    private String reportType;
    private String reportDetails;
    private String name;
    private String username;
    private String date;
    private String phoneNumber;

    private String status;

    // Constructor to initialize the Report object
    public Report(int reportId, String reportType, String reportDetails, String name, String username, String date, String phoneNumber, String status) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.reportDetails = reportDetails;
        this.name = name;
        this.username = username;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    // Getters and setters for each field
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
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

    public String getStatus() {  // Getter for status
        return status;
    }

    public void setStatus(String status) {  // Setter for status
        this.status = status;
    }

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

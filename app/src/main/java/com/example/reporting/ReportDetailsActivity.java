package com.example.reporting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class ReportDetailsActivity extends AppCompatActivity {

    private Report currentReport;
    private SharedPreferences sharedPreferences;
    private static final String REPORTS_PREF_KEY = "reports";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ReportPrefs", Context.MODE_PRIVATE);
        
        // Get the report from intent
        currentReport = (Report) getIntent().getSerializableExtra("report");
        
        // Initialize delete button
        ImageView deleteButton = findViewById(R.id.deleteReportButton);
        deleteButton.setOnClickListener(v -> deleteReport());
        
        // Initialize back button
        ImageView backButton = findViewById(R.id.reportIconImageView);
        backButton.setOnClickListener(v -> finish());

        if (currentReport != null) {
            setupReportDetails();
        } else {
            Toast.makeText(this, "Error: No report data found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupReportDetails() {
        try {
            TextView reportCodeText = findViewById(R.id.reportCodeTextView);
            TextView reportDetailsText = findViewById(R.id.reportDetailsTextView);
            TextView reportText = findViewById(R.id.reportTextTextView);
            TextView responseText = findViewById(R.id.responseTextTextView);
            
            if (reportCodeText != null) {
                reportCodeText.setText(currentReport.getReportType());
            }
            
            if (reportDetailsText != null) {
                reportDetailsText.setText(String.format("Name: %s\nUsername: %s\nReport Date: %s\nPhone Number: %s",
                        currentReport.getName(), currentReport.getUsername(), currentReport.getDate(), currentReport.getPhoneNumber()));
            }
            
            if (reportText != null) {
                reportText.setText(currentReport.getReportDetails());
            }
            
            if (responseText != null) {
                responseText.setText("Pending response...");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading report details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void deleteReport() {
        try {
            // Get existing reports
            String reportsJson = sharedPreferences.getString(REPORTS_PREF_KEY, "[]");
            Type listType = new TypeToken<ArrayList<Report>>(){}.getType();
            List<Report> reports = new Gson().fromJson(reportsJson, listType);
            
            // Remove current report
            reports.removeIf(report -> report.getReportId().equals(currentReport.getReportId()));
            
            // Save the updated reports list
            String updatedReportsJson = new Gson().toJson(reports);
            sharedPreferences.edit().putString(REPORTS_PREF_KEY, updatedReportsJson).apply();
            
            // Show success message
            Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
            
            // Return to report page
            finish();
            
            // Save updated list
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(REPORTS_PREF_KEY, new Gson().toJson(reports));
            editor.apply();
            
            Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

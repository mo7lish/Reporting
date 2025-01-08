package com.example.reporting;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    private TextView reportCodeTextView;
    private TextView reportDetailsTextView;
    private TextView reportTextTextView;
    private ImageView deleteReportButton;
    private List<Report> reportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        
        // Initialize views
        reportCodeTextView = findViewById(R.id.reportCodeTextView);
        reportDetailsTextView = findViewById(R.id.reportDetailsTextView);
        reportTextTextView = findViewById(R.id.reportTextTextView);
        deleteReportButton = findViewById(R.id.deleteReportButton);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ReportPrefs", Context.MODE_PRIVATE);
        
        // Get report data from intent
        if (getIntent().hasExtra("report")) {
            currentReport = (Report) getIntent().getSerializableExtra("report");
            setupReportDetails();
        } else {
            Toast.makeText(this, "Error: No report data found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up delete button click listener
        deleteReportButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        
        // Set up back button click listener
        ImageView backButton = findViewById(R.id.reportIconImageView);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupReportDetails() {
        TextView reportNumberView = findViewById(R.id.reportCodeTextView);
        if (currentReport != null && currentReport.getReportId() != null) {
            reportNumberView.setText(currentReport.getReportId() + " " + currentReport.getReportType());
        }
        
        TextView reportDetailsView = findViewById(R.id.reportDetailsTextView);
        String details = String.format("Name: %s\nUsername: %s\nReport Date: %s\nLocation: %s\nPhone Number: %s",
            currentReport.getName(),
            currentReport.getUsername(),
            currentReport.getDate(),
            currentReport.getAddress() != null ? currentReport.getAddress() : "Not specified",
            currentReport.getPhoneNumber());
        reportDetailsView.setText(details);
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

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this report?")
               .setPositiveButton("Delete", (dialog, id) -> deleteReport())
               .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteReport() {
        Report report = (Report) getIntent().getSerializableExtra("report");
        if (report == null) {
            Toast.makeText(this, "Error: Report not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SharedPreferences prefs = getSharedPreferences("ReportsPrefs", MODE_PRIVATE);
            String reportsJson = prefs.getString("reports", "[]");
            Type type = new TypeToken<ArrayList<Report>>(){}.getType();
            ArrayList<Report> reports = new Gson().fromJson(reportsJson, type);

            if (!reports.removeIf(r -> r.getReportId().equals(report.getReportId()))) {
                Toast.makeText(this, "Error: Report not found in the list", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save updated list
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("reports", new Gson().toJson(reports));
            editor.apply();

            Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
    }
}

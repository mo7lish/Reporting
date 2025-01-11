package com.example.reporting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportPage extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private ActivityResultLauncher<Intent> reportSubmissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        // Load initial sample reports if no reports exist
        if (loadReports().isEmpty()) {
            List<Report> sampleReports = new ArrayList<>();
            sampleReports.add(new Report("1", "Child Marriage", "Sample report for child marriage case",
                "John Doe", "johndoe", "23/11/2023 14:30", "+1234567890", "Pending"));
            sampleReports.add(new Report("2", "Forced Marriage", "Sample report for forced marriage case",
                "Jane Smith", "janesmith", "24/11/2023 15:45", "+1234567890", "Pending"));
            sampleReports.add(new Report("3", "Sexual Harassment", "Sample report for harassment case",
                "Maria Amir", "maramir", "25/11/2023 16:15", "+1234567890", "Completed"));
            saveReports(sampleReports);
        }

        // Initialize RecyclerView with proper context
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(ReportPage.this));
        reportList = loadReports();
        reportAdapter = new ReportAdapter(this, reportList);
        reportsRecyclerView.setAdapter(reportAdapter);

        // Load saved reports from SharedPreferences
        reportList = loadReports();
        if (reportList == null) {
            reportList = new ArrayList<>();
        }

        // Initialize adapter with loaded reports
        reportAdapter = new ReportAdapter(this, reportList);
        reportsRecyclerView.setAdapter(reportAdapter);

        // Set up Activity Result handling
        reportSubmissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Report newReport = (Report) result.getData().getSerializableExtra("newReport");
                    if (newReport != null) {
                        reportList.add(newReport);
                        reportAdapter.notifyDataSetChanged();
                        saveReports(reportList);
                    }
                }
            }
        );

        FloatingActionButton addReportButton = findViewById(R.id.addReportFab);
        if (addReportButton != null) {
            addReportButton.setOnClickListener(v -> {
                // Open ReportSubmissionActivity to submit a new report
                Intent intent = new Intent(ReportPage.this, ReportSubmissionActivity.class);
                reportSubmissionLauncher.launch(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the report list when the activity is resumed (after adding a new report)
        List<Report> loadedReports = loadReports();
        // Initialize list before setting adapter
        if (loadedReports != null) {
            reportList = loadedReports;
            if (reportAdapter != null) {
                reportAdapter.updateReports(reportList);
                reportAdapter.notifyDataSetChanged();
            }
        } else {
            reportList = new ArrayList<>();
        }
    }

     // Activity result handling is now done through reportSubmissionLauncher

// Removed commented out code

    // Handle item click to show report details
    public void showReportDetails(Report report) {
        Intent intent = new Intent(this, ReportDetailsActivity.class);
        intent.putExtra("report", report);
        startActivity(intent);
    }

    // Save the reports to SharedPreferences
    private void saveReports(List<Report> reports) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("ReportsPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save reports as a JSON string (using Gson to convert to JSON)
            String reportsJson = new Gson().toJson(reports != null ? reports : new ArrayList<>());
            editor.putString("reports", reportsJson);
            editor.apply();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving reports: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Load reports from SharedPreferences
    private List<Report> loadReports() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("ReportsPrefs", MODE_PRIVATE);
            String reportsJson = sharedPreferences.getString("reports", "[]");  // Default to an empty list
            List<Report> reports = new Gson().fromJson(reportsJson, new TypeToken<List<Report>>(){}.getType());
            return reports != null ? reports : new ArrayList<>();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading reports: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }
}



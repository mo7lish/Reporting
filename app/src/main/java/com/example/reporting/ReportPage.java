package com.example.reporting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class ReportPage extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private ActivityResultLauncher<Intent> reportSubmissionLauncher;
    private static final int REQUEST_CODE_REPORT_SUBMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);
        
        // Initialize RecyclerView
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Load saved reports from SharedPreferences
        reportList = new ArrayList<>();
        reportList.addAll(loadReports());
        
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

        ImageView addReportButton = findViewById(R.id.appIconImageView);
        addReportButton.setOnClickListener(v -> {
            // Open ReportSubmissionActivity to submit a new report
            Intent intent = new Intent(ReportPage.this, ReportSubmissionActivity.class);
            reportSubmissionLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the report list when the activity is resumed (after adding a new report)
        reportList = loadReports();
        reportAdapter.updateReports(reportList);
        reportAdapter.notifyDataSetChanged();
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == REQUEST_CODE_REPORT_SUBMISSION && resultCode == RESULT_OK) {
             // Retrieve the new report from the Intent
             Report newReport = (Report) data.getSerializableExtra("newReport");

             if (newReport != null) {
                 // Add the new report to the list and update the UI
                 reportList.add(newReport);
                 reportAdapter.notifyDataSetChanged();

                 // Save the updated report list
                 saveReports(reportList);
             }
         }
     }

//    private final ActivityResultLauncher<Intent> reportSubmissionLauncher =
//        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                Report newReport = result.getData().getParcelableExtra("newReport"); // import android.os.Parcelable
//                if (newReport != null) {
//                    reportList.add(newReport);
//                    reportAdapter.notifyDataSetChanged();
//                    saveReports(reportList);
//                }
//            }
//        });

    // Handle item click to show report details
    public void showReportDetails(Report report) {
        Intent intent = new Intent(ReportPage.this, ReportDetailsActivity.class);
        intent.putExtra("report", report);
        startActivity(intent);
    }

    // Save the reports to SharedPreferences
    private void saveReports(List<Report> reports) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReportsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save reports as a JSON string (using Gson to convert to JSON)
        String reportsJson = new Gson().toJson(reports);
        editor.putString("reports", reportsJson);
        editor.apply();
    }

    // Load reports from SharedPreferences
    private List<Report> loadReports() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReportsPrefs", MODE_PRIVATE);
        String reportsJson = sharedPreferences.getString("reports", "[]");  // Default to an empty list
        return new Gson().fromJson(reportsJson, new TypeToken<List<Report>>(){}.getType());
    }
}

package com.example.reporting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class ReportDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        
        // Setup back button
        ImageView backButton = findViewById(R.id.reportIconImageView);
        backButton.setOnClickListener(v -> finish());
        
        try {
            // Get report from intent
            Report report = (Report) getIntent().getSerializableExtra("report");
            if (report != null) {
                // Set report details
                TextView reportCodeText = findViewById(R.id.reportCodeTextView);
                TextView reportDetailsText = findViewById(R.id.reportDetailsTextView);
                TextView reportText = findViewById(R.id.reportTextTextView);
                TextView responseText = findViewById(R.id.responseTextTextView);
                
                if (reportCodeText != null) {
                    reportCodeText.setText(report.getReportType());
                }
                
                if (reportDetailsText != null) {
                    reportDetailsText.setText(String.format("Name: %s\nUsername: %s\nReport Date: %s\nPhone Number: %s",
                            report.getName(), report.getUsername(), report.getDate(), report.getPhoneNumber()));
                }
                
                if (reportText != null) {
                    reportText.setText(report.getReportDetails());
                }
                
                // Set response text if available
                if (responseText != null) {
                    responseText.setText("Pending response...");
                }
            } else {
                Toast.makeText(this, "Error: No report data found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading report details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

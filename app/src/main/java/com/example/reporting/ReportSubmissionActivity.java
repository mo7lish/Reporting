package com.example.reporting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportSubmissionActivity extends AppCompatActivity {

    private EditText reportDetailsEditText;
    private Spinner spinnerReportType;  // Changed to match XML
    private String selectedReportType;
    
    // Predefined report types
    private final String[] reportOptions = {
            "Physical Violence",
            "Sexual Harassment",
            "Sexual Assault",
            "Domestic Violence",
            "Psychological Abuse",
            "Stalking",
            "Discrimination at Workplace",
            "Forced Marriage",
            "Child Marriage"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submission);
        
        // Initialize views
        reportDetailsEditText = findViewById(R.id.reportTextTextView);
        spinnerReportType = findViewById(R.id.reportTypeSpinner);
        Button submitButton = findViewById(R.id.submitReportButton);
        
        // Set initial report type
        selectedReportType = reportOptions[0];
        
        // Setup back button
        ImageView backButton = findViewById(R.id.appIconImageView);
        backButton.setOnClickListener(v -> finish());
        
        // Setup spinner with reportOptions array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, reportOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerReportType != null) {
            spinnerReportType.setAdapter(adapter);
            spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedReportType = reportOptions[position];
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedReportType = null;
                }
            });
        }
        
        // Setup submit button
        submitButton.setOnClickListener(v -> submitReport());

        // Remove duplicate initialization code
    }

    private void submitReport() {
        if (reportDetailsEditText == null || spinnerReportType == null) {
            Toast.makeText(this, "Error: Form not properly initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String reportDetails = reportDetailsEditText.getText().toString().trim();
        
        if (reportDetails.isEmpty()) {
            Toast.makeText(this, "Please fill in the report details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedReportType == null) {
            Toast.makeText(this, "Please select a report type", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Report newReport = new Report(
                (int) (new Date().getTime() % Integer.MAX_VALUE),  // Use timestamp as ID
                selectedReportType,
                reportDetails,
                "John Doe",  // Static user info for demo
                "johndoe",   // Static username for demo
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()),
                "+1234567890",  // Static phone number for demo
                "Pending"
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra("newReport", newReport);
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

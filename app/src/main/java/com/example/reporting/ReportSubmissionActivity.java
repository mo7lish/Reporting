package com.example.reporting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportSubmissionActivity extends AppCompatActivity {

    private EditText reportDetailsEditText;
    private Spinner reportTypeSpinner;
    private TextView nameEditText, usernameEditText, phoneNumberEditText; // Using TextView since they're not editable
    private Button submitButtonTextView;

    // Predefined static values for name, username, and phone number
    private final String name = "Mohammed";
    private final String username = "Mohd123";
    private final String phoneNumber = "014-8794906";
    private final String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

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

    private String selectedReportType;
    private int reportId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submission);

        // Initialize views
        reportDetailsEditText = findViewById(R.id.reportTextTextView);
        reportTypeSpinner = findViewById(R.id.reportTypeSpinner);
        submitButtonTextView = findViewById(R.id.submitReportButton);

        // Set static values
        nameEditText.setText(name);
        usernameEditText.setText(username);
        phoneNumberEditText.setText(phoneNumber);

        // Set up Spinner with an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(adapter);

        reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReportType = reportOptions[position];
                reportId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up the submit button
        submitButtonTextView.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String reportDetails = reportDetailsEditText.getText().toString();

        if (reportDetails.isEmpty() || selectedReportType == null) {
            Toast.makeText(this, "Please select a report type and fill in the details", Toast.LENGTH_SHORT).show();
            return;
        }

        Report newReport = new Report(reportId, selectedReportType, reportDetails, name, username, currentDate, phoneNumber, "pending");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newReport", newReport);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

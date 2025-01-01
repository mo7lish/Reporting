package com.example.reporting;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class ReportSubmissionActivity extends AppCompatActivity {
    private void setupSpinnerListener() {
        if (spinnerReportType != null) {
            spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedReportType = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedReportType = null;
                }
            });
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == MEDIA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Multiple images selected
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    mediaUrls.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                mediaUrls.add(imageUri.toString());
            }
            
            updateAttachmentStatus();
            Toast.makeText(this, mediaUrls.size() + " image(s) attached", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String generateReportId(String reportType) {
        // Get the initials from the report type
        String initials = "";
        if (reportType != null && !reportType.isEmpty()) {
            initials = Arrays.stream(reportType.split(" "))
                    .filter(word -> !word.isEmpty())
                    .map(word -> word.substring(0, 1).toUpperCase())
                    .collect(Collectors.joining());
        }
        
        // Generate a random 4-digit number
        Random random = new Random();
        int number = random.nextInt(10000);
        
        // Format the number to ensure it's 4 digits with leading zeros if needed
        return String.format("%s%04d", initials, number);
    }
    private static final int LOCATION_PERMISSION_REQUEST = 1002;
    private static final int MEDIA_PERMISSION_REQUEST = 1001;
    
    private EditText reportDetailsEditText;
    private Button submitButton;
    private TextView attachmentStatusTextView;
    private ImageButton mediaButton;
    private ImageButton locationButton;
    private Spinner spinnerReportType;
    private FusedLocationProviderClient fusedLocationClient;
    private List<String> mediaUrls;
    private String currentLocation;
    private String selectedReportType;
    private ActivityResultLauncher<Intent> mediaPickerLauncher;
    
    // Predefined report types
    private void saveReport(Report report) {
        // Get existing reports
        SharedPreferences prefs = getSharedPreferences("ReportPrefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String reportsJson = prefs.getString("reports", "[]");
        Type type = new TypeToken<ArrayList<Report>>(){}.getType();
        ArrayList<Report> reports = gson.fromJson(reportsJson, type);
        
        // Add new report
        reports.add(report);
        
        // Save updated list
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("reports", gson.toJson(reports));
        editor.apply();
    }

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

        // Initialize UI components
        reportDetailsEditText = findViewById(R.id.reportTextTextView);
        submitButton = findViewById(R.id.submitReportButton);
        attachmentStatusTextView = findViewById(R.id.attachmentStatusTextView);
        mediaButton = findViewById(R.id.mediaButton);
        locationButton = findViewById(R.id.locationButton);
        spinnerReportType = findViewById(R.id.reportTypeSpinner);
        setupSpinnerListener();
        
        // Initialize other members
        mediaUrls = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup report type spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(adapter);
        
        // Setup listeners
        setupSpinnerListener();
        
        // Setup button click listeners
        submitButton.setOnClickListener(v -> submitReport());
        mediaButton.setOnClickListener(v -> attachMedia());
        locationButton.setOnClickListener(v -> attachLocation());
        
        // Set initial report type
        selectedReportType = reportOptions[0];
        
        // Setup back button
        ImageView backButton = findViewById(R.id.appIconImageView);
        backButton.setOnClickListener(v -> finish());
        
        // Setup spinner with reportOptions array
        ArrayAdapter<String> reportTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, reportOptions);
        reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerReportType != null) {
            spinnerReportType.setAdapter(reportTypeAdapter);
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

    private void setupMediaPicker() {
        mediaPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        mediaUrls.add(selectedImage.toString());
                        updateAttachmentStatus();
                    }
                }
            });
    }

    private void attachLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
            return;
        }
        
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = location.getLatitude() + "," + location.getLongitude();
                    updateAttachmentStatus();
                } else {
                    Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(this,
                "Error getting location", Toast.LENGTH_SHORT).show());
    }
    
    private void attachMedia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MEDIA_PERMISSION_REQUEST);
            return;
        }
        
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), MEDIA_PERMISSION_REQUEST);
    }
    
    private void updateAttachmentStatus() {
        StringBuilder statusBuilder = new StringBuilder();
        
        if (currentLocation != null && !currentLocation.isEmpty()) {
            statusBuilder.append("📍 Location attached\n");
        }
        
        if (!mediaUrls.isEmpty()) {
            statusBuilder.append("📎 ").append(mediaUrls.size()).append(" media files attached\n");
        }
        
        String statusText = statusBuilder.toString().trim();
        if (!statusText.isEmpty()) {
            attachmentStatusTextView.setText(statusText);
            attachmentStatusTextView.setVisibility(View.VISIBLE);
        } else {
            attachmentStatusTextView.setVisibility(View.GONE);
        }
        
        if (!mediaUrls.isEmpty()) {
            statusBuilder.append("📎 ").append(mediaUrls.size()).append(" media file(s) attached");
        } else {
            statusBuilder.append("No media attached");
        }
        
        attachmentStatusTextView.setText(statusBuilder.toString());
        attachmentStatusTextView.setVisibility(View.VISIBLE);
    }
    
    private void submitReport() {
        if (reportDetailsEditText == null || spinnerReportType == null) {
            Toast.makeText(this, "Error: Form not properly initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String reportDetails = StringUtils.capitalizeWords(reportDetailsEditText.getText().toString());
        
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
                generateReportId(selectedReportType),  // Generate ID based on report type
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

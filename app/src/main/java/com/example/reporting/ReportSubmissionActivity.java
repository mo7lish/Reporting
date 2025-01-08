package com.example.reporting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

public class ReportSubmissionActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = Constants.LOCATION_PERMISSION_REQUEST;
    private static final int IMAGE_PERMISSION_REQUEST = Constants.IMAGE_PERMISSION_REQUEST;
    private static final int IMAGE_PICK_CODE = Constants.IMAGE_PICK_CODE;
    private static final int MAX_IMAGE_SIZE = Constants.MAX_IMAGE_SIZE;

    private ImageView previewImageView;
    private TextView locationTextView;
    private ProgressBar loadingIndicator;
    private RecyclerView imagePreviewRecyclerView;
    private List<Uri> selectedImageUris;
    private String currentAddress;
    private ImagePreviewAdapter imagePreviewAdapter;
    private TextView attachmentStatusTextView;
    private Spinner spinnerReportType;
    private EditText reportDetailsEditText;
    private Button submitButton;
    private ImageButton mediaButton;
    private ImageButton locationButton;
    private FusedLocationProviderClient fusedLocationClient;
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
        
        if (requestCode == MEDIA_PERMISSION_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Multiple images selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    handleSingleImage(imageUri);
                }
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                handleSingleImage(imageUri);
            }

            refreshAttachmentStatus();
            Toast.makeText(this, selectedImageUris.size() + " image(s) attached", Toast.LENGTH_SHORT).show();
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
    private static final int MEDIA_PERMISSION_REQUEST = 1001;

    private List<String> mediaUrls;
    private String currentLocation;
    private String selectedReportType;
    private ActivityResultLauncher<Intent> mediaPickerLauncher;
    
    // Predefined report types
    private void saveReport(Report report) {
        // Get existing reports
        SharedPreferences prefs = getSharedPreferences("ReportsPrefs", Context.MODE_PRIVATE);
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

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submission);
        initializeViews();
        setupUI();
        setupListeners();
    }

    private void initializeViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        reportDetailsEditText = findViewById(R.id.reportTextTextView);
        submitButton = findViewById(R.id.submitReportButton);
        attachmentStatusTextView = findViewById(R.id.attachmentStatusTextView);
        mediaButton = findViewById(R.id.mediaButton);
        locationButton = findViewById(R.id.locationButton);
        spinnerReportType = findViewById(R.id.reportTypeSpinner);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        locationTextView = findViewById(R.id.locationTextView);
        imagePreviewRecyclerView = findViewById(R.id.imagePreviewRecyclerView);
        selectedImageUris = new ArrayList<>();
    }

    private void setupUI() {
        // Setup RecyclerView
        imagePreviewRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagePreviewAdapter = new ImagePreviewAdapter(this, selectedImageUris, position -> {
            selectedImageUris.remove(position);
            imagePreviewAdapter.notifyItemRemoved(position);

        });
        imagePreviewRecyclerView.setAdapter(imagePreviewAdapter);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, reportOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportType.setAdapter(adapter);
        selectedReportType = reportOptions[0];
        setupSpinnerListener();
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> submitReport());
        mediaButton.setOnClickListener(v -> attachMedia());
        locationButton.setOnClickListener(v -> attachLocation());

        // Setup back button
        ImageView backButton = findViewById(R.id.appIconImageView);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupMediaPicker() {
        mediaPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        mediaUrls.add(selectedImage.toString());

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
        
        loadingIndicator.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = location.getLatitude() + "," + location.getLongitude();
                    try {
                        currentAddress = LocationUtils.getAddressFromLocation(this, 
                            location.getLatitude(), location.getLongitude());
                        if (currentAddress != null) {
                            locationTextView.setText(currentAddress);
                            locationTextView.setVisibility(View.VISIBLE);
                        }

                    } catch (IOException e) {
                        Toast.makeText(this, "Could not get address from location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
                }
                loadingIndicator.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
            });
    }
    
    private void attachMedia() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                IMAGE_PERMISSION_REQUEST);
            return;
        }
        
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), IMAGE_PICK_CODE);
    }
    
    

    private void handleSingleImage(Uri imageUri) {
        if (imageUri != null && MediaUtils.isImageSizeValid(this, imageUri, MAX_IMAGE_SIZE)) {
            selectedImageUris.add(imageUri);
            imagePreviewAdapter.notifyDataSetChanged();
            imagePreviewRecyclerView.setVisibility(View.VISIBLE);
            refreshAttachmentStatus();
        } else {
            Toast.makeText(this, "Image size should not exceed 1MB", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void refreshAttachmentStatus() {
        StringBuilder statusBuilder = new StringBuilder();
        
        if (currentLocation != null) {
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
        
        final String details = reportDetailsEditText.getText().toString().trim();
        if (details.isEmpty()) {
            Toast.makeText(this, "Please enter report details", Toast.LENGTH_SHORT).show();
            return;
        }
        
        loadingIndicator.setVisibility(View.VISIBLE);
        
        // Generate report ID and number
        String reportId = generateReportId(selectedReportType);
        
        // Create new report
        Report report = new Report(
            reportId,
            selectedReportType,
            details,
            "User Name", // TODO: Get from user profile
            "username", // TODO: Get from user profile
            String.valueOf(System.currentTimeMillis()),
            "", // TODO: Get phone number
            Constants.STATUS_PENDING
        );
        report.setReportNumber(reportId);
        
        // Set location if available
        if (currentLocation != null) {
            report.setLocation(currentLocation);
        }
        if (currentAddress != null) {
            report.setAddress(currentAddress);
        }
        
        // Add media URLs if any
        for (Uri uri : selectedImageUris) {
            try {
                byte[] compressedImage = MediaUtils.compressImage(this, uri, MAX_IMAGE_SIZE);
                // TODO: Upload image to cloud storage and get URL
                String imageUrl = "https://example.com/images/" + uri.getLastPathSegment(); // Temporary placeholder
                report.addMediaUrl(imageUrl);
            } catch (IOException e) {
                Toast.makeText(this, "Error processing images", Toast.LENGTH_SHORT).show();
                loadingIndicator.setVisibility(View.GONE);
                return;
            }
        }
        
        // Save report
        saveReport(report);
        loadingIndicator.setVisibility(View.GONE);
        finish();

        try {
            // Report already created and saved above

            Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newReport", report);
            setResult(RESULT_OK, resultIntent);
            finish();  // Close activity after successful save
        } catch (Exception e) {
            Toast.makeText(this, "Error creating report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

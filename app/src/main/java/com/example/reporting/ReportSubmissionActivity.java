package com.example.reporting;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
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

public class ReportSubmissionActivity extends AppCompatActivity implements ImagePreviewAdapter.OnImageRemoveListener {
    private RecyclerView imagePreviewRecyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1MB
    private ActivityResultLauncher<Intent> mediaPickerLauncher;

    @Override
    public void onImageRemove(int position) {
        if (position >= 0 && position < selectedImageUris.size()) {
            selectedImageUris.remove(position);
            imagePreviewAdapter.notifyItemRemoved(position);
            refreshAttachmentStatus();
        }
    }
    private List<String> mediaUrls = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST = Constants.LOCATION_PERMISSION_REQUEST;
    private static final int IMAGE_PERMISSION_REQUEST = Constants.IMAGE_PERMISSION_REQUEST;
    private StringBuilder statusBuilder;


    private ImageView previewImageView;
    private TextView locationTextView;
    private ProgressBar loadingIndicator;
    private String currentAddress;
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
    private String currentLocation;
    private String selectedReportType;

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submission);
        initializeViews();
        setupMediaPicker();
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
        imagePreviewRecyclerView = findViewById(R.id.imagePreviewRecyclerView);
        imagePreviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        spinnerReportType = findViewById(R.id.reportTypeSpinner);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        statusBuilder = new StringBuilder();
        locationTextView = findViewById(R.id.locationTextView);
        
        // Initialize RecyclerView for image previews
        imagePreviewRecyclerView = findViewById(R.id.imagePreviewRecyclerView);
        selectedImageUris = new ArrayList<>();
        imagePreviewAdapter = new ImagePreviewAdapter(this, selectedImageUris, this);
        imagePreviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagePreviewRecyclerView.setAdapter(imagePreviewAdapter);
        refreshAttachmentStatus();
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
                    Intent data = result.getData();
                    try {
                        if (data.getClipData() != null) {
                            // Handle multiple images
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                handleSingleImage(imageUri);
                            }
                        } else if (data.getData() != null) {
                            // Handle single image
                            handleSingleImage(data.getData());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to process selected images", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                IMAGE_PERMISSION_REQUEST);
            return;
        }
        launchImagePicker();
    }
    
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            mediaPickerLauncher.launch(Intent.createChooser(intent, "Select Pictures"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open image picker", Toast.LENGTH_SHORT).show();
        }
    }
    
    

    private void handleSingleImage(Uri imageUri) {
        if (imageUri != null) {
            try {
                if (MediaUtils.isImageSizeValid(this, imageUri, MAX_IMAGE_SIZE)) {
                    selectedImageUris.add(imageUri);
                    updateImagePreviewAdapter();
                    refreshAttachmentStatus();
                } else {
                    Toast.makeText(this, "Image size should not exceed 1MB", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImagePreviewAdapter() {
        if (imagePreviewAdapter == null) {
            imagePreviewAdapter = new ImagePreviewAdapter(this, selectedImageUris, this);
            imagePreviewRecyclerView.setAdapter(imagePreviewAdapter);
        } else {
            imagePreviewAdapter.notifyDataSetChanged();
        }
        imagePreviewRecyclerView.setVisibility(View.VISIBLE);
    }
    
    private void refreshAttachmentStatus() {
        statusBuilder.setLength(0);
        
        if (!selectedImageUris.isEmpty()) {
            statusBuilder.append(String.format("%d image(s) attached\n", selectedImageUris.size()));
            imagePreviewRecyclerView.setVisibility(View.VISIBLE);
        } else {
            imagePreviewRecyclerView.setVisibility(View.GONE);
        }
        
        if (currentLocation != null) {
            statusBuilder.append("📍 Location attached\n");
        }
        
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            statusBuilder.append("📎 ").append(mediaUrls.size()).append(" media file(s) attached");
        }
        
        String statusText = statusBuilder.toString().trim();
        if (!statusText.isEmpty()) {
            attachmentStatusTextView.setText(statusText);
            attachmentStatusTextView.setVisibility(View.VISIBLE);
        } else {
            attachmentStatusTextView.setText("No attachments");
            attachmentStatusTextView.setVisibility(View.GONE);
        }
    }
    
    private String getCurrentUsername() {
        // TODO: Implement actual user profile retrieval
        return "default_user";
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentPhoneNumber() {
        // TODO: Implement actual phone number retrieval
        return "";
    }

    private void submitReport() {
        if (reportDetailsEditText == null || spinnerReportType == null) {
            Toast.makeText(this, "Error: Form not properly initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        final String details = reportDetailsEditText.getText().toString().trim();
        
        // Convert Uri list to String list for the report
        ArrayList<String> imageUriStrings = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            imageUriStrings.add(uri.toString());
        }
        if (details.isEmpty()) {
            Toast.makeText(this, "Please enter report details", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String reportId = generateReportId(selectedReportType);
        
        // Convert image URIs to strings for storage
        imageUriStrings = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            imageUriStrings.add(uri.toString());
        }
        
        // Create new report with all required fields
        Report report = new Report(
            reportId,
            selectedReportType,
            details,
            "User Name", // TODO: Get from user profile
            getCurrentUsername(),
            getCurrentDate(),
            getCurrentPhoneNumber(),
            Constants.STATUS_PENDING
        );
        
        // Set location if available
        if (currentLocation != null) {
            report.setLocation(currentLocation);
        }
        if (currentAddress != null) {
            report.setAddress(currentAddress);
        }
        // Set image URIs regardless of address status
        report.setImageUris(imageUriStrings);
        
        // Add media URLs if any
        for (Uri uri : selectedImageUris) {
            try {
                byte[] compressedImage = MediaUtils.compressImage(this, uri, MAX_IMAGE_SIZE);
                
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

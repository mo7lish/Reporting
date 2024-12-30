package com.example.reporting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
public class ReportDetailsActivity extends AppCompatActivity {

    private TextView reportDetailsTextView, nameTextView, usernameTextView, phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        // Get the report passed from ReportPage
        Report report = getIntent().getParcelableExtra("report"); // import android.os.Parcelable

        reportDetailsTextView = findViewById(R.id.reportDetailsTextView);


        // Populate the details with static or passed data
        reportDetailsTextView.setText(report.getReportDetails());
        nameTextView.setText(report.getName());
        usernameTextView.setText(report.getUsername());
        phoneNumberTextView.setText(report.getPhoneNumber());

        ImageView backButton = findViewById(R.id.reportIconImageView);
        backButton.setOnClickListener(v -> finish());
    }
}

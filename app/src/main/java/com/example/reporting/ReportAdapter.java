package com.example.reporting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context context;
    private List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_report_item, parent, false);
        return new ViewHolder(view);
    }

    // @Override
    // public void onBindViewHolder(ViewHolder holder, int position) {
    //     Report report = reportList.get(position);
    //     holder.reportCodeTextView.setText(report.getReportCode());
    //     holder.statusTextView.setText(report.getStatus());

    //     holder.itemView.setOnClickListener(new View.OnClickListener() {
    //         @Override
    //         public void onClick(View v) {
    //             // Handle item click: pass the report object to ReportDetailsActivity
    //             Intent intent = new Intent(v.getContext(), ReportDetailsActivity.class);
    //             intent.putExtra("report", report); // Pass the selected report as an extra
    //             v.getContext().startActivity(intent);
    //         }
    //     });
    // }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Report report = reportList.get(position);
        
        // Set report ID and type
        holder.reportCodeTextView.setText("Report #" + report.getReportId() + "\n" + report.getReportType());
        
        // Set report details
        String details = report.getReportDetails();
        holder.reportDetailsEditText.setText(details != null ? details : "");
        
        // Set status text and background
        String status = report.getStatus();
        holder.statusTextView.setText(status != null ? status : "Pending");
        if ("Completed".equals(status)) {
            holder.statusPendingLayout.setBackgroundResource(R.drawable.status_background_completed);
        } else {
            holder.statusPendingLayout.setBackgroundResource(R.drawable.status_background_pending);
        }

    holder.itemView.setOnClickListener(v -> {
        Intent intent = new Intent(v.getContext(), ReportDetailsActivity.class);
        intent.putExtra("report", report);
        v.getContext().startActivity(intent);
    });
}

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateReports(List<Report> newReports) {
        this.reportList = newReports;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportCodeTextView, statusTextView;
        EditText reportDetailsEditText;
        LinearLayout statusPendingLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            reportCodeTextView = itemView.findViewById(R.id.reportCodeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            reportDetailsEditText = itemView.findViewById(R.id.reportDetailsEditText);
            statusPendingLayout = itemView.findViewById(R.id.statusPendingLayout);
        }
    }
}

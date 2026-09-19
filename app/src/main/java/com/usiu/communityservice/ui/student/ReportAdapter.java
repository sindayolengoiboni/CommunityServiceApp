package com.usiu.communityservice.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.util.DateFormatter;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final List<AcademicReport> reports = new ArrayList<>();

    public void setReports(List<AcademicReport> list) {
        reports.clear();
        if (list != null) {
            reports.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicReport report = reports.get(position);

        holder.tvType.setText(report.getReportType().replace("_", " "));
        holder.tvTitle.setText(report.getTitle() != null ? report.getTitle() : "Document Submission");
        holder.tvDate.setText("Submitted: " + DateFormatter.formatDisplayDate(report.getSubmittedAt()));

        UIUtils.styleStatusBadge(holder.tvStatus, report.getStatus());

        if (report.getLecturerFeedback() != null && !report.getLecturerFeedback().isEmpty()) {
            holder.layoutFeedback.setVisibility(View.VISIBLE);
            holder.tvGrade.setText("Evaluation: " + (report.getGradeOrScore() != null ? report.getGradeOrScore() : "Pending"));
            holder.tvFeedback.setText("Lecturer Feedback: " + report.getLecturerFeedback());
        } else {
            holder.layoutFeedback.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTitle, tvDate, tvStatus, tvGrade, tvFeedback;
        LinearLayout layoutFeedback;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_report_type_title);
            tvTitle = itemView.findViewById(R.id.tv_report_title);
            tvDate = itemView.findViewById(R.id.tv_report_date);
            tvStatus = itemView.findViewById(R.id.tv_report_status_badge);
            layoutFeedback = itemView.findViewById(R.id.layout_lecturer_feedback);
            tvGrade = itemView.findViewById(R.id.tv_report_grade);
            tvFeedback = itemView.findViewById(R.id.tv_report_feedback);
        }
    }
}

package com.usiu.communityservice.ui.lecturer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.util.DateFormatter;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the lecturer's report review list.
 * Each card shows student name, report type, submission date, grade (if any),
 * status badge, and a "Review" button.
 */
public class ReportReviewAdapter extends RecyclerView.Adapter<ReportReviewAdapter.ViewHolder> {

    public interface OnReviewClickListener {
        void onReview(AcademicReport report);
    }

    private List<AcademicReport> reports = new ArrayList<>();
    private final OnReviewClickListener listener;

    public ReportReviewAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    public void setReports(List<AcademicReport> reports) {
        this.reports = reports != null ? reports : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reports.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudent, tvStatusBadge, tvReportType, tvReportDate, tvGrade;
        Button btnReview;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tv_report_student);
            tvStatusBadge = itemView.findViewById(R.id.tv_report_status_badge);
            tvReportType = itemView.findViewById(R.id.tv_report_type_item);
            tvReportDate = itemView.findViewById(R.id.tv_report_date_item);
            tvGrade = itemView.findViewById(R.id.tv_report_grade);
            btnReview = itemView.findViewById(R.id.btn_review_report);
        }

        void bind(AcademicReport report, OnReviewClickListener listener) {
            tvStudent.setText(report.getStudentName() != null ? report.getStudentName() : report.getStudentId());
            tvReportType.setText(report.getReportType());
            tvReportDate.setText(
                    itemView.getContext().getString(R.string.label_submitted_prefix,
                            DateFormatter.formatTimestamp(report.getSubmittedAt())));

            UIUtils.applyStatusBadge(tvStatusBadge, report.getStatus());

            // Show grade if already reviewed
            if (report.getGrade() > 0) {
                tvGrade.setText(itemView.getContext().getString(
                        R.string.label_grade_value, report.getGrade()));
                tvGrade.setVisibility(View.VISIBLE);
            } else {
                tvGrade.setVisibility(View.GONE);
            }

            btnReview.setOnClickListener(v -> listener.onReview(report));
        }
    }
}

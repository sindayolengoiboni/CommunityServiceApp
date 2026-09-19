package com.usiu.communityservice.ui.lecturer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.data.repositories.ReportRepository;
import com.usiu.communityservice.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all student-submitted academic reports for the lecturer to review.
 * Supports filtering by status (All, Pending, Approved, Rejected).
 * Tapping a report card opens GradeFeedbackActivity.
 */
public class ReportReviewsFragment extends Fragment {

    private RecyclerView rvReports;
    private ProgressBar pbReports;
    private TextView tvNoReports;
    private AutoCompleteTextView spinnerReportStatus;

    private ReportReviewAdapter adapter;
    private List<AcademicReport> allReports = new ArrayList<>();

    private ReportRepository reportRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvReports = view.findViewById(R.id.rv_reports);
        pbReports = view.findViewById(R.id.pb_reports);
        tvNoReports = view.findViewById(R.id.tv_no_reports);
        spinnerReportStatus = view.findViewById(R.id.spinner_report_status);

        reportRepository = new ReportRepository();

        setupAdapter();
        setupStatusFilter();
        loadAllReports();
    }

    private void setupAdapter() {
        adapter = new ReportReviewAdapter(report -> {
            // Navigate to GradeFeedbackActivity
            Intent intent = new Intent(getContext(), GradeFeedbackActivity.class);
            intent.putExtra(GradeFeedbackActivity.EXTRA_REPORT_ID, report.getReportId());
            startActivity(intent);
        });
        rvReports.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReports.setAdapter(adapter);
    }

    private void setupStatusFilter() {
        String[] statuses = {
                "All",
                Constants.STATUS_PENDING,
                Constants.STATUS_APPROVED,
                Constants.STATUS_REJECTED
        };
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, statuses);
        spinnerReportStatus.setAdapter(filterAdapter);
        spinnerReportStatus.setText("All", false);

        spinnerReportStatus.setOnItemClickListener((parent, v, position, id) -> {
            String selected = statuses[position];
            if ("All".equals(selected)) {
                adapter.setReports(allReports);
            } else {
                List<AcademicReport> filtered = new ArrayList<>();
                for (AcademicReport r : allReports) {
                    if (selected.equals(r.getStatus())) filtered.add(r);
                }
                adapter.setReports(filtered);
            }
            updateEmptyState(adapter.getItemCount() == 0);
        });
    }

    private void loadAllReports() {
        pbReports.setVisibility(View.VISIBLE);
        tvNoReports.setVisibility(View.GONE);

        reportRepository.getAllReports(reports -> {
            if (!isAdded()) return;
            pbReports.setVisibility(View.GONE);
            allReports.clear();
            if (reports != null) allReports.addAll(reports);
            adapter.setReports(allReports);
            updateEmptyState(allReports.isEmpty());
        }, e -> {
            if (!isAdded()) return;
            pbReports.setVisibility(View.GONE);
            tvNoReports.setText(R.string.error_loading_data);
            tvNoReports.setVisibility(View.VISIBLE);
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvNoReports.setText(R.string.msg_no_reports);
            tvNoReports.setVisibility(View.VISIBLE);
            rvReports.setVisibility(View.GONE);
        } else {
            tvNoReports.setVisibility(View.GONE);
            rvReports.setVisibility(View.VISIBLE);
        }
    }
}

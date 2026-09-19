package com.usiu.communityservice.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ReportRepository;

import java.util.List;

public class ReportsFragment extends Fragment {

    private RecyclerView rvReports;
    private ProgressBar pbReports;
    private TextView tvNoReports;
    private Button btnUpload;
    private ReportAdapter adapter;
    private ReportRepository reportRepository;
    private User currentUser;

    public static ReportsFragment newInstance(User user) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        reportRepository = ReportRepository.getInstance();

        rvReports = view.findViewById(R.id.rv_student_reports);
        pbReports = view.findViewById(R.id.pb_reports);
        tvNoReports = view.findViewById(R.id.tv_no_reports);
        btnUpload = view.findViewById(R.id.btn_upload_new_report);

        rvReports.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter();
        rvReports.setAdapter(adapter);

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UploadReportActivity.class);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });

        loadReports();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReports();
    }

    private void loadReports() {
        if (currentUser == null) return;

        pbReports.setVisibility(View.VISIBLE);
        tvNoReports.setVisibility(View.GONE);

        reportRepository.getStudentReports(currentUser.getUid(), new ReportRepository.ListCallback<AcademicReport>() {
            @Override
            public void onSuccess(List<AcademicReport> items) {
                pbReports.setVisibility(View.GONE);
                adapter.setReports(items);
                tvNoReports.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbReports.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading reports: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

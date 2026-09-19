package com.usiu.communityservice.ui.lecturer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.data.repositories.ReportRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.DateFormatter;

import java.util.Objects;

/**
 * Allows a Lecturer to review a student-submitted academic report:
 *  - View report metadata (student, type, submission date)
 *  - Open uploaded file URL in browser
 *  - Set review status (APPROVED / REJECTED)
 *  - Enter a numeric grade (0–100) and written feedback
 *  - Submit — writes review to Firestore via ReportRepository and creates audit log
 */
public class GradeFeedbackActivity extends AppCompatActivity {

    public static final String EXTRA_REPORT_ID = "extra_report_id";

    private TextView tvStudentName, tvReportType, tvSubmittedDate;
    private Button btnViewFile, btnSubmitGrade;
    private AutoCompleteTextView spinnerReviewStatus;
    private TextInputEditText etGrade, etFeedback;
    private ProgressBar pbGradeSubmit;

    private ReportRepository reportRepository;
    private AcademicReport currentReport;
    private String reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_feedback);

        reportId = getIntent().getStringExtra(EXTRA_REPORT_ID);
        if (reportId == null || reportId.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_report, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupStatusSpinner();
        loadReport();
    }

    private void initViews() {
        tvStudentName = findViewById(R.id.tv_report_student_name);
        tvReportType = findViewById(R.id.tv_report_type);
        tvSubmittedDate = findViewById(R.id.tv_report_submitted_date);
        btnViewFile = findViewById(R.id.btn_view_report_file);
        spinnerReviewStatus = findViewById(R.id.spinner_review_status);
        etGrade = findViewById(R.id.et_grade);
        etFeedback = findViewById(R.id.et_feedback);
        btnSubmitGrade = findViewById(R.id.btn_submit_grade);
        pbGradeSubmit = findViewById(R.id.pb_grade_submit);

        reportRepository = new ReportRepository();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_grade_feedback);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupStatusSpinner() {
        String[] statuses = { Constants.STATUS_APPROVED, Constants.STATUS_REJECTED };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, statuses);
        spinnerReviewStatus.setAdapter(adapter);
    }

    private void loadReport() {
        reportRepository.getReportById(reportId, report -> {
            currentReport = report;
            populateFields(report);
            btnSubmitGrade.setOnClickListener(v -> submitReview());
        }, e -> {
            Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void populateFields(AcademicReport report) {
        tvStudentName.setText(report.getStudentName() != null ? report.getStudentName() : report.getStudentId());
        tvReportType.setText(report.getReportType());
        tvSubmittedDate.setText(DateFormatter.formatTimestamp(report.getSubmittedAt()));

        // If already reviewed, pre-fill
        if (report.getGrade() > 0) {
            etGrade.setText(String.valueOf(report.getGrade()));
        }
        if (report.getFeedback() != null) {
            etFeedback.setText(report.getFeedback());
        }
        if (report.getStatus() != null) {
            spinnerReviewStatus.setText(report.getStatus(), false);
        }

        // View file button
        btnViewFile.setOnClickListener(v -> {
            if (report.getFileUrl() != null && !report.getFileUrl().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(report.getFileUrl())));
            } else {
                Toast.makeText(this, R.string.msg_no_file_attached, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        String selectedStatus = spinnerReviewStatus.getText().toString().trim();
        String gradeStr = Objects.requireNonNull(etGrade.getText()).toString().trim();
        String feedback = Objects.requireNonNull(etFeedback.getText()).toString().trim();

        if (selectedStatus.isEmpty()) {
            Toast.makeText(this, R.string.error_select_status, Toast.LENGTH_SHORT).show();
            return;
        }
        if (gradeStr.isEmpty()) {
            etGrade.setError(getString(R.string.error_grade_required));
            return;
        }

        int grade;
        try {
            grade = Integer.parseInt(gradeStr);
            if (grade < 0 || grade > 100) {
                etGrade.setError(getString(R.string.error_grade_range));
                return;
            }
        } catch (NumberFormatException e) {
            etGrade.setError(getString(R.string.error_grade_invalid));
            return;
        }

        if (feedback.isEmpty()) {
            etFeedback.setError(getString(R.string.error_feedback_required));
            return;
        }

        String reviewerId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        pbGradeSubmit.setVisibility(View.VISIBLE);
        btnSubmitGrade.setEnabled(false);

        int finalGrade = grade;
        reportRepository.reviewReport(reportId, selectedStatus, finalGrade, feedback, reviewerId,
                unused -> {
                    pbGradeSubmit.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.msg_review_submitted, Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    pbGradeSubmit.setVisibility(View.GONE);
                    btnSubmitGrade.setEnabled(true);
                    Toast.makeText(this, R.string.error_submit_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package com.usiu.communityservice.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ReportRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

public class UploadReportActivity extends AppCompatActivity {

    private Spinner spReportType;
    private TextInputEditText etTitle;
    private Button btnSelectDoc, btnSubmit;
    private TextView tvSelectedDoc;
    private ProgressBar pbUpload;

    private ReportRepository reportRepository;
    private User currentUser;
    private String selectedFileName = "CMS3700M_Submission.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_report);

        reportRepository = ReportRepository.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        spReportType = findViewById(R.id.sp_report_type);
        etTitle = findViewById(R.id.et_upload_report_title);
        btnSelectDoc = findViewById(R.id.btn_select_document);
        tvSelectedDoc = findViewById(R.id.tv_selected_doc_name);
        btnSubmit = findViewById(R.id.btn_submit_report);
        pbUpload = findViewById(R.id.pb_upload_report);

        setupReportTypeSpinner();

        btnSelectDoc.setOnClickListener(v -> {
            selectedFileName = "USIU_CMS3700M_" + System.currentTimeMillis() + ".pdf";
            tvSelectedDoc.setText("Attached: " + selectedFileName);
            Toast.makeText(this, "Document attached (" + selectedFileName + ")", Toast.LENGTH_SHORT).show();
        });

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void setupReportTypeSpinner() {
        String[] types;
        if (currentUser != null && Constants.OPTION_PROJECT.equals(currentUser.getServiceOption())) {
            types = new String[]{
                    "PROJECT_PROPOSAL",
                    "PROJECT_MILESTONE_REPORT",
                    "PROJECT_FINAL_REPORT",
                    "HANDOVER_DOCUMENTATION"
            };
        } else {
            types = new String[]{
                    "FIRST_IMPRESSIONS_PAPER",
                    "FINAL_SERVICE_REPORT",
                    "SITE_EVALUATION_REPORT",
                    "ORAL_PRESENTATION_SLIDES"
            };
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spReportType.setAdapter(adapter);
    }

    private void submitReport() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Please enter a title for the report");
            etTitle.requestFocus();
            return;
        }

        String reportType = spReportType.getSelectedItem().toString();

        pbUpload.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        AcademicReport report = new AcademicReport();
        report.setStudentUid(currentUser.getUid());
        report.setStudentName(currentUser.getFullName());
        report.setStudentIdNumber(currentUser.getStudentId());
        report.setServiceOption(currentUser.getServiceOption());
        report.setReportType(reportType);
        report.setTitle(title);
        report.setDocumentFileName(selectedFileName);
        report.setDocumentUrl("https://storage.usiu.ac.ke/cms3700m/" + selectedFileName);

        reportRepository.submitReport(report, new ReportRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                pbUpload.setVisibility(View.GONE);
                Toast.makeText(UploadReportActivity.this, "Report uploaded. Lecturer notified for evaluation.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                pbUpload.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                UIUtils.showErrorDialog(UploadReportActivity.this, "Upload Failed", e.getMessage());
            }
        });
    }
}

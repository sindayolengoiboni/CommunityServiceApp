package com.usiu.communityservice.ui.supervisor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

public class ReportIssueActivity extends AppCompatActivity {

    private TextInputEditText etStudentId, etSubject, etDescription;
    private Button btnSubmit;
    private ProgressBar pbIssue;
    private User currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        db = FirebaseFirestore.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        etStudentId = findViewById(R.id.et_issue_student_id);
        etSubject = findViewById(R.id.et_issue_subject);
        etDescription = findViewById(R.id.et_issue_description);
        btnSubmit = findViewById(R.id.btn_submit_issue);
        pbIssue = findViewById(R.id.pb_issue);

        btnSubmit.setOnClickListener(v -> submitIssue());
    }

    private void submitIssue() {
        String studentInfo = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String subject = etSubject.getText() != null ? etSubject.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(subject)) {
            etSubject.setError("Subject is required");
            etSubject.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Please describe the issue or concern");
            etDescription.requestFocus();
            return;
        }

        pbIssue.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // Audit Log
        AuditLog log = new AuditLog(
                "SUPERVISOR_REPORT_ISSUE",
                currentUser != null ? currentUser.getUid() : "supervisor",
                currentUser != null ? currentUser.getFullName() : "Supervisor",
                Constants.ROLE_SUPERVISOR,
                studentInfo,
                "Reported issue to Coordinator: [" + subject + "] - " + description
        );
        db.collection(Constants.COLL_AUDIT_LOGS).add(log);

        // Broadcast notification to coordinator
        NotificationItem notif = new NotificationItem(
                "COORDINATOR_BROADCAST",
                "Supervisor Incident Report: " + subject,
                "Supervisor " + (currentUser != null ? currentUser.getFullName() : "") + " flagged concern regarding " + studentInfo + ": " + description,
                "INCIDENT_REPORT"
        );
        db.collection(Constants.COLL_NOTIFICATIONS).add(notif)
                .addOnSuccessListener(docRef -> {
                    pbIssue.setVisibility(View.GONE);
                    Toast.makeText(ReportIssueActivity.this, "Concern reported to USIU Community Service Coordinator.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbIssue.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    UIUtils.showErrorDialog(ReportIssueActivity.this, "Submission Failed", e.getMessage());
                });
    }
}

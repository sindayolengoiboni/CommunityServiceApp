package com.usiu.communityservice.ui.coordinator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.SupervisionVisit;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SupervisionRepository;
import com.usiu.communityservice.util.UIUtils;

import java.util.Arrays;

public class RecordSupervisionVisitActivity extends AppCompatActivity {

    private TextInputEditText etOrgName, etStudentsPresent, etActivities, etAttendanceObs, etFeedback, etChallenges, etFollowUp;
    private Button btnSave;
    private ProgressBar pbSave;

    private SupervisionRepository supervisionRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_supervision_visit);

        supervisionRepository = SupervisionRepository.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        etOrgName = findViewById(R.id.et_visit_org_name);
        etStudentsPresent = findViewById(R.id.et_visit_students_present);
        etActivities = findViewById(R.id.et_visit_activities);
        etAttendanceObs = findViewById(R.id.et_visit_attendance_obs);
        etFeedback = findViewById(R.id.et_visit_org_feedback);
        etChallenges = findViewById(R.id.et_visit_challenges);
        etFollowUp = findViewById(R.id.et_visit_follow_up);
        btnSave = findViewById(R.id.btn_save_supervision_visit);
        pbSave = findViewById(R.id.pb_save_visit);

        btnSave.setOnClickListener(v -> saveVisit());
    }

    private void saveVisit() {
        String orgName = etOrgName.getText() != null ? etOrgName.getText().toString().trim() : "";
        String students = etStudentsPresent.getText() != null ? etStudentsPresent.getText().toString().trim() : "";
        String activities = etActivities.getText() != null ? etActivities.getText().toString().trim() : "";
        String attendance = etAttendanceObs.getText() != null ? etAttendanceObs.getText().toString().trim() : "";
        String feedback = etFeedback.getText() != null ? etFeedback.getText().toString().trim() : "";
        String challenges = etChallenges.getText() != null ? etChallenges.getText().toString().trim() : "";
        String followUp = etFollowUp.getText() != null ? etFollowUp.getText().toString().trim() : "";

        if (TextUtils.isEmpty(orgName)) {
            etOrgName.setError("Organization name required");
            etOrgName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(activities)) {
            etActivities.setError("Observations required");
            etActivities.requestFocus();
            return;
        }

        pbSave.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        SupervisionVisit visit = new SupervisionVisit();
        visit.setCoordinatorUid(currentUser != null ? currentUser.getUid() : "coordinator");
        visit.setCoordinatorName(currentUser != null ? currentUser.getFullName() : "Coordinator");
        visit.setOrganizationName(orgName);
        visit.setObservedStudentNames(Arrays.asList(students.split(",")));
        visit.setActivitiesObserved(activities);
        visit.setAttendanceObservations(attendance);
        visit.setOrganizationFeedback(feedback);
        visit.setChallengesNoted(challenges);
        visit.setFollowUpActions(followUp);
        visit.setVisitDate(Timestamp.now());

        supervisionRepository.recordSupervisionVisit(visit, new SupervisionRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                pbSave.setVisibility(View.GONE);
                Toast.makeText(RecordSupervisionVisitActivity.this, "Site supervision visit report logged successfully.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                pbSave.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                UIUtils.showErrorDialog(RecordSupervisionVisitActivity.this, "Save Failed", e.getMessage());
            }
        });
    }
}

package com.usiu.communityservice.ui.student;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.data.models.Opportunity;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ApplicationRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

public class OpportunityDetailsActivity extends AppCompatActivity {

    private Opportunity opportunity;
    private User currentUser;
    private ApplicationRepository applicationRepository;

    private TextView tvTitle, tvOrg, tvSlots, tvSchedule, tvLocation, tvTrack, tvDescription;
    private Button btnApply;
    private ProgressBar pbApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_details);

        applicationRepository = ApplicationRepository.getInstance();

        opportunity = (Opportunity) getIntent().getSerializableExtra("OPP_EXTRA");
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        if (opportunity == null) {
            Toast.makeText(this, "Opportunity not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle = findViewById(R.id.tv_det_title);
        tvOrg = findViewById(R.id.tv_det_org);
        tvSlots = findViewById(R.id.tv_det_slots);
        tvSchedule = findViewById(R.id.tv_det_schedule);
        tvLocation = findViewById(R.id.tv_det_location);
        tvTrack = findViewById(R.id.tv_det_track);
        tvDescription = findViewById(R.id.tv_det_description);
        btnApply = findViewById(R.id.btn_apply_placement);
        pbApply = findViewById(R.id.pb_apply);

        bindData();

        btnApply.setOnClickListener(v -> submitApplication());
    }

    private void bindData() {
        tvTitle.setText(opportunity.getTitle());
        tvOrg.setText(opportunity.getOrganizationName());

        int available = opportunity.getAvailableSlots();
        tvSlots.setText("Available Placement Slots: " + available);

        String days = (opportunity.getServiceDays() != null && !opportunity.getServiceDays().isEmpty())
                ? String.join(", ", opportunity.getServiceDays())
                : "Flexible Days";
        String time = (opportunity.getStartTime() != null && opportunity.getEndTime() != null)
                ? " (" + opportunity.getStartTime() + " - " + opportunity.getEndTime() + ")"
                : "";
        tvSchedule.setText("Schedule: " + days + time);

        tvLocation.setText("Location: " + (opportunity.getLocation() != null ? opportunity.getLocation() : "Not specified"));
        tvTrack.setText("Service Track: " + (opportunity.getServiceOption() != null ? opportunity.getServiceOption().replace("_", " ") : "Hands-on"));
        tvDescription.setText(opportunity.getDescription());

        if (available <= 0) {
            btnApply.setEnabled(false);
            btnApply.setText("Placement Capacity Full");
        }
    }

    private void submitApplication() {
        if (currentUser == null) {
            Toast.makeText(this, "User session error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check project-based eligibility rule
        if (Constants.OPTION_PROJECT.equals(currentUser.getServiceOption())
                && !Constants.ELIGIBILITY_APPROVED.equals(currentUser.getEligibilityStatus())) {
            UIUtils.showErrorDialog(this, "Eligibility Verification Required",
                    "Your project-based service track is currently pending coordinator verification. Please ensure your employment evidence is uploaded and approved before applying for placements.");
            return;
        }

        pbApply.setVisibility(View.VISIBLE);
        btnApply.setEnabled(false);

        Application app = new Application();
        app.setStudentUid(currentUser.getUid());
        app.setStudentName(currentUser.getFullName());
        app.setStudentIdNumber(currentUser.getStudentId());
        app.setStudentEmail(currentUser.getEmail());
        app.setOpportunityId(opportunity.getId());
        app.setOpportunityTitle(opportunity.getTitle());
        app.setOrganizationId(opportunity.getOrganizationId());
        app.setOrganizationName(opportunity.getOrganizationName());
        app.setServiceOption(currentUser.getServiceOption());

        applicationRepository.submitApplication(app, new ApplicationRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                pbApply.setVisibility(View.GONE);
                Toast.makeText(OpportunityDetailsActivity.this, "Placement application submitted successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                pbApply.setVisibility(View.GONE);
                btnApply.setEnabled(true);
                UIUtils.showErrorDialog(OpportunityDetailsActivity.this, "Submission Failed", e.getMessage());
            }
        });
    }
}

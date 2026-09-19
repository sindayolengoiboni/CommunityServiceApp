package com.usiu.communityservice.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.data.models.CourseSettings;
import com.usiu.communityservice.data.models.Registration;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.data.repositories.SupervisionRepository;
import com.usiu.communityservice.util.UIUtils;
import com.usiu.communityservice.util.ValidationUtils;

import java.util.Arrays;
import java.util.Date;

public class SiteRegistrationActivity extends AppCompatActivity {

    private Application application;
    private User currentUser;
    private SupervisionRepository supervisionRepository;
    private SettingsAndAuditRepository settingsRepository;

    private TextView tvOrgName;
    private TextInputEditText etSupName, etSupEmail, etSupPhone, etDays, etStartTime, etEndTime;
    private Button btnSubmit;
    private ProgressBar pbSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_registration);

        supervisionRepository = SupervisionRepository.getInstance();
        settingsRepository = SettingsAndAuditRepository.getInstance();

        application = (Application) getIntent().getSerializableExtra("APP_EXTRA");
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        if (application == null) {
            Toast.makeText(this, "Application record missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvOrgName = findViewById(R.id.tv_reg_org_name);
        etSupName = findViewById(R.id.et_reg_sup_name);
        etSupEmail = findViewById(R.id.et_reg_sup_email);
        etSupPhone = findViewById(R.id.et_reg_sup_phone);
        etDays = findViewById(R.id.et_reg_days);
        etStartTime = findViewById(R.id.et_reg_start_time);
        etEndTime = findViewById(R.id.et_reg_end_time);
        btnSubmit = findViewById(R.id.btn_submit_site_reg);
        pbSubmit = findViewById(R.id.pb_site_reg);

        tvOrgName.setText("Placement Organization: " + application.getOrganizationName());

        btnSubmit.setOnClickListener(v -> submitSiteRegistration());
    }

    private void submitSiteRegistration() {
        String supName = etSupName.getText() != null ? etSupName.getText().toString().trim() : "";
        String supEmail = etSupEmail.getText() != null ? etSupEmail.getText().toString().trim() : "";
        String supPhone = etSupPhone.getText() != null ? etSupPhone.getText().toString().trim() : "";
        String days = etDays.getText() != null ? etDays.getText().toString().trim() : "";
        String startTime = etStartTime.getText() != null ? etStartTime.getText().toString().trim() : "";
        String endTime = etEndTime.getText() != null ? etEndTime.getText().toString().trim() : "";

        if (TextUtils.isEmpty(supName)) {
            etSupName.setError("Supervisor name is required");
            etSupName.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(supEmail)) {
            etSupEmail.setError("Valid supervisor email required");
            etSupEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(days)) {
            etDays.setError("Specify weekly service days");
            etDays.requestFocus();
            return;
        }

        pbSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // Fetch course settings to verify 3-week deadline compliance
        settingsRepository.getCourseSettings(new SettingsAndAuditRepository.ItemCallback<CourseSettings>() {
            @Override
            public void onSuccess(CourseSettings settings) {
                Registration reg = new Registration();
                reg.setApplicationId(application.getId());
                reg.setStudentUid(currentUser.getUid());
                reg.setStudentName(currentUser.getFullName());
                reg.setStudentIdNumber(currentUser.getStudentId());
                reg.setOrganizationId(application.getOrganizationId());
                reg.setOrganizationName(application.getOrganizationName());
                reg.setSupervisorName(supName);
                reg.setSupervisorEmail(supEmail);
                reg.setSupervisorPhone(supPhone);
                reg.setServiceDays(Arrays.asList(days.split(",")));
                reg.setServiceStartTime(startTime);
                reg.setServiceEndTime(endTime);
                reg.setServiceOption(currentUser.getServiceOption());

                // Check registration deadline (e.g. within 3 weeks of semester start)
                long nowMs = System.currentTimeMillis();
                long semStartMs = settings.getSemesterStartDate() != null
                        ? settings.getSemesterStartDate().toDate().getTime()
                        : nowMs;
                long deadlineMs = semStartMs + ((long) settings.getRegistrationDeadlineWeeks() * 7L * 24L * 3600L * 1000L);

                if (nowMs > deadlineMs) {
                    reg.setLateSubmission(true);
                }

                supervisionRepository.submitRegistration(reg, new SupervisionRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        pbSubmit.setVisibility(View.GONE);
                        Toast.makeText(SiteRegistrationActivity.this, "Official site registration submitted to Coordinator.", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        pbSubmit.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        UIUtils.showErrorDialog(SiteRegistrationActivity.this, "Registration Error", e.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                pbSubmit.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                UIUtils.showErrorDialog(SiteRegistrationActivity.this, "Settings Fetch Error", e.getMessage());
            }
        });
    }
}

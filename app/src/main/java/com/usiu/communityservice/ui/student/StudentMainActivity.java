package com.usiu.communityservice.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.CourseSettings;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.data.repositories.ServiceTrackingRepository;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.ui.auth.LoginActivity;
import com.usiu.communityservice.util.Constants;

public class StudentMainActivity extends AppCompatActivity {

    private User currentUser;
    private AuthRepository authRepository;
    private ServiceTrackingRepository trackingRepository;
    private SettingsAndAuditRepository settingsRepository;

    private TextView tvStudentName, tvStudentTrack, tvHoursLabel, tvRecordedLabel, tvAbsenceWarning;
    private ProgressBar pbHoursProgress;
    private ImageView ivSignOut;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        authRepository = AuthRepository.getInstance();
        trackingRepository = ServiceTrackingRepository.getInstance();
        settingsRepository = SettingsAndAuditRepository.getInstance();

        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        tvStudentName = findViewById(R.id.tv_student_header_name);
        tvStudentTrack = findViewById(R.id.tv_student_header_track);
        tvHoursLabel = findViewById(R.id.tv_hours_progress_label);
        tvRecordedLabel = findViewById(R.id.tv_recorded_hours_label);
        tvAbsenceWarning = findViewById(R.id.tv_absence_warning);
        pbHoursProgress = findViewById(R.id.pb_hours_progress);
        ivSignOut = findViewById(R.id.iv_student_signout);
        bottomNav = findViewById(R.id.student_bottom_nav);

        updateHeaderUI();

        ivSignOut.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_opportunities) {
                selected = BrowseOpportunitiesFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_applications) {
                selected = MyApplicationsFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_diary) {
                selected = AttendanceDiaryFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_reports) {
                selected = ReportsFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_profile) {
                selected = StudentProfileFragment.newInstance(currentUser);
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.student_fragment_container, selected)
                        .commit();
                return true;
            }
            return false;
        });

        // Default to Opportunities tab
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.student_fragment_container, BrowseOpportunitiesFragment.newInstance(currentUser))
                    .commit();
        }

        checkAbsenceWarnings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserProfile();
    }

    private void refreshUserProfile() {
        authRepository.getCurrentUserProfile(new AuthRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                updateHeaderUI();
                checkAbsenceWarnings();
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    private void updateHeaderUI() {
        if (currentUser == null) return;

        tvStudentName.setText(currentUser.getFullName());
        boolean isHandsOn = Constants.OPTION_HANDS_ON.equals(currentUser.getServiceOption());

        tvStudentTrack.setText(isHandsOn
                ? "Hands-on Service (CMS 3700 M: 90 hrs)"
                : "Project-based Service (CMS 3700 M)");

        if (isHandsOn) {
            findViewById(R.id.layout_hours_progress).setVisibility(View.VISIBLE);
            double verified = currentUser.getVerifiedHours();
            double recorded = currentUser.getRecordedHours();
            int progress = (int) Math.min(90, Math.round(verified));

            pbHoursProgress.setProgress(progress);
            tvHoursLabel.setText(String.format(java.util.Locale.US, "Verified: %.1f / 90.0 hrs", verified));
            tvRecordedLabel.setText(String.format(java.util.Locale.US, "Recorded: %.1f hrs", recorded));
        } else {
            // Project based does not enforce 90-hour rule by default
            findViewById(R.id.layout_hours_progress).setVisibility(View.GONE);
        }
    }

    private void checkAbsenceWarnings() {
        if (currentUser == null) return;

        settingsRepository.getCourseSettings(new SettingsAndAuditRepository.ItemCallback<CourseSettings>() {
            @Override
            public void onSuccess(CourseSettings settings) {
                trackingRepository.checkAbsenceStatistics(
                        currentUser.getUid(),
                        settings.getMaxAllowedAbsencesSite(),
                        settings.getMaxAllowedAbsencesClass(),
                        new ServiceTrackingRepository.AbsenceStatsCallback() {
                            @Override
                            public void onStats(int siteAbsences, int classAbsences, boolean siteWarning, boolean classWarning) {
                                if (siteWarning || classWarning) {
                                    tvAbsenceWarning.setVisibility(View.VISIBLE);
                                    tvAbsenceWarning.setText(
                                            "⚠️ Attendance Warning: You have reached " + siteAbsences +
                                            " site absences and " + classAbsences +
                                            " class absences. Contact your Coordinator/Lecturer immediately."
                                    );
                                } else {
                                    tvAbsenceWarning.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Exception e) {}
                        }
                );
            }

            @Override
            public void onError(Exception e) {}
        });
    }
}

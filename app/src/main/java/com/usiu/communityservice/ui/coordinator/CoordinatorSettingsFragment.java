package com.usiu.communityservice.ui.coordinator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.CourseSettings;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.util.UIUtils;

public class CoordinatorSettingsFragment extends Fragment {

    private TextInputEditText etSemester, etDeadlineWeeks, etMinHours, etDurationWeeks, etDaysWeek, etHoursDay, etMaxSiteAbs, etMaxClassAbs;
    private Button btnSaveSettings, btnViewAuditLogs;
    private ProgressBar pbSave;

    private SettingsAndAuditRepository settingsRepository;
    private User currentUser;
    private CourseSettings currentSettings;

    public static CoordinatorSettingsFragment newInstance(User user) {
        CoordinatorSettingsFragment fragment = new CoordinatorSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_settings, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        settingsRepository = SettingsAndAuditRepository.getInstance();

        etSemester = view.findViewById(R.id.et_set_semester_name);
        etDeadlineWeeks = view.findViewById(R.id.et_set_reg_deadline_weeks);
        etMinHours = view.findViewById(R.id.et_set_min_hours);
        etDurationWeeks = view.findViewById(R.id.et_set_duration_weeks);
        etDaysWeek = view.findViewById(R.id.et_set_days_week);
        etHoursDay = view.findViewById(R.id.et_set_hours_day);
        etMaxSiteAbs = view.findViewById(R.id.et_set_max_site_absences);
        etMaxClassAbs = view.findViewById(R.id.et_set_max_class_absences);
        btnSaveSettings = view.findViewById(R.id.btn_save_settings);
        btnViewAuditLogs = view.findViewById(R.id.btn_view_audit_logs);
        pbSave = view.findViewById(R.id.pb_save_settings);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());

        btnViewAuditLogs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuditLogViewerActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadSettings() {
        pbSave.setVisibility(View.VISIBLE);
        settingsRepository.getCourseSettings(new SettingsAndAuditRepository.ItemCallback<CourseSettings>() {
            @Override
            public void onSuccess(CourseSettings settings) {
                pbSave.setVisibility(View.GONE);
                currentSettings = settings;
                etSemester.setText(settings.getSemesterName());
                etDeadlineWeeks.setText(String.valueOf(settings.getRegistrationDeadlineWeeks()));
                etMinHours.setText(String.valueOf(settings.getHandsOnMinHours()));
                etDurationWeeks.setText(String.valueOf(settings.getHandsOnDurationWeeks()));
                etDaysWeek.setText(String.valueOf(settings.getHandsOnDaysPerWeek()));
                etHoursDay.setText(String.valueOf(settings.getHandsOnHoursPerDay()));
                etMaxSiteAbs.setText(String.valueOf(settings.getMaxAllowedAbsencesSite()));
                etMaxClassAbs.setText(String.valueOf(settings.getMaxAllowedAbsencesClass()));
            }

            @Override
            public void onError(Exception e) {
                pbSave.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSettings() {
        if (currentSettings == null) currentSettings = new CourseSettings();

        try {
            currentSettings.setSemesterName(etSemester.getText().toString().trim());
            currentSettings.setRegistrationDeadlineWeeks(Integer.parseInt(etDeadlineWeeks.getText().toString().trim()));
            currentSettings.setHandsOnMinHours(Double.parseDouble(etMinHours.getText().toString().trim()));
            currentSettings.setHandsOnDurationWeeks(Integer.parseInt(etDurationWeeks.getText().toString().trim()));
            currentSettings.setHandsOnDaysPerWeek(Integer.parseInt(etDaysWeek.getText().toString().trim()));
            currentSettings.setHandsOnHoursPerDay(Double.parseDouble(etHoursDay.getText().toString().trim()));
            currentSettings.setMaxAllowedAbsencesSite(Integer.parseInt(etMaxSiteAbs.getText().toString().trim()));
            currentSettings.setMaxAllowedAbsencesClass(Integer.parseInt(etMaxClassAbs.getText().toString().trim()));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        pbSave.setVisibility(View.VISIBLE);
        btnSaveSettings.setEnabled(false);

        settingsRepository.saveCourseSettings(
                currentSettings,
                currentUser != null ? currentUser.getUid() : "coordinator",
                new SettingsAndAuditRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        pbSave.setVisibility(View.GONE);
                        btnSaveSettings.setEnabled(true);
                        Toast.makeText(getContext(), "CMS 3700 M course settings saved!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        pbSave.setVisibility(View.GONE);
                        btnSaveSettings.setEnabled(true);
                        if (getContext() != null) {
                            UIUtils.showErrorDialog(getContext(), "Save Failed", e.getMessage());
                        }
                    }
                }
        );
    }
}

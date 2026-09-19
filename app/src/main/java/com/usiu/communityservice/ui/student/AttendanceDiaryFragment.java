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
import com.usiu.communityservice.data.models.CourseSettings;
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.data.repositories.ServiceTrackingRepository;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;

import java.util.List;

public class AttendanceDiaryFragment extends Fragment {

    private TextView tvVerifiedHours, tvRecordedHours, tvAbsences;
    private Button btnAddEntry;
    private RecyclerView rvDiary;
    private ProgressBar pbDiary;
    private TextView tvNoDiary;
    private DiaryAdapter adapter;

    private ServiceTrackingRepository trackingRepository;
    private AuthRepository authRepository;
    private SettingsAndAuditRepository settingsRepository;
    private User currentUser;

    public static AttendanceDiaryFragment newInstance(User user) {
        AttendanceDiaryFragment fragment = new AttendanceDiaryFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_diary, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        trackingRepository = ServiceTrackingRepository.getInstance();
        authRepository = AuthRepository.getInstance();
        settingsRepository = SettingsAndAuditRepository.getInstance();

        tvVerifiedHours = view.findViewById(R.id.tv_metric_verified_hours);
        tvRecordedHours = view.findViewById(R.id.tv_metric_recorded_hours);
        tvAbsences = view.findViewById(R.id.tv_metric_absences);
        btnAddEntry = view.findViewById(R.id.btn_add_diary_entry);
        rvDiary = view.findViewById(R.id.rv_diary_entries);
        pbDiary = view.findViewById(R.id.pb_diary);
        tvNoDiary = view.findViewById(R.id.tv_no_diary);

        rvDiary.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiaryAdapter();
        rvDiary.setAdapter(adapter);

        btnAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddDiaryEntryActivity.class);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });

        loadDiaryAndMetrics();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDiaryAndMetrics();
    }

    private void loadDiaryAndMetrics() {
        if (currentUser == null) return;

        pbDiary.setVisibility(View.VISIBLE);
        tvNoDiary.setVisibility(View.GONE);

        // Refresh user profile for updated verified and recorded hours
        authRepository.getCurrentUserProfile(new AuthRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                tvVerifiedHours.setText(String.format(java.util.Locale.US, "%.1f", user.getVerifiedHours()));
                double pendingHours = Math.max(0.0, user.getRecordedHours() - user.getVerifiedHours());
                tvRecordedHours.setText(String.format(java.util.Locale.US, "%.1f", pendingHours));
            }

            @Override
            public void onError(Exception e) {}
        });

        // Load diary entries
        trackingRepository.getStudentDiaryEntries(currentUser.getUid(), new ServiceTrackingRepository.ListCallback<DiaryEntry>() {
            @Override
            public void onSuccess(List<DiaryEntry> items) {
                pbDiary.setVisibility(View.GONE);
                adapter.setEntries(items);
                tvNoDiary.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbDiary.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading diary: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check absences
        settingsRepository.getCourseSettings(new SettingsAndAuditRepository.ItemCallback<CourseSettings>() {
            @Override
            public void onSuccess(CourseSettings settings) {
                trackingRepository.checkAbsenceStatistics(currentUser.getUid(),
                        settings.getMaxAllowedAbsencesSite(),
                        settings.getMaxAllowedAbsencesClass(),
                        new ServiceTrackingRepository.AbsenceStatsCallback() {
                            @Override
                            public void onStats(int siteAbsences, int classAbsences, boolean siteWarning, boolean classWarning) {
                                tvAbsences.setText(String.valueOf(siteAbsences));
                            }

                            @Override
                            public void onError(Exception e) {}
                        });
            }

            @Override
            public void onError(Exception e) {}
        });
    }
}

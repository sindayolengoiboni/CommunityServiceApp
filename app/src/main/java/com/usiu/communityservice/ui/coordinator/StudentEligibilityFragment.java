package com.usiu.communityservice.ui.coordinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StudentEligibilityFragment extends Fragment {

    private RecyclerView rvEligibility;
    private ProgressBar pbEligibility;
    private TextView tvNoEligibility;
    private EligibilityAdapter adapter;
    private SettingsAndAuditRepository settingsRepository;
    private User currentUser;

    public static StudentEligibilityFragment newInstance(User user) {
        StudentEligibilityFragment fragment = new StudentEligibilityFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_eligibility, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        settingsRepository = SettingsAndAuditRepository.getInstance();

        rvEligibility = view.findViewById(R.id.rv_eligibility);
        pbEligibility = view.findViewById(R.id.pb_eligibility);
        tvNoEligibility = view.findViewById(R.id.tv_no_eligibility);

        rvEligibility.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EligibilityAdapter((student, newStatus) -> {
            pbEligibility.setVisibility(View.VISIBLE);
            settingsRepository.reviewStudentEligibility(
                    student.getUid(),
                    newStatus,
                    currentUser != null ? currentUser.getUid() : "coordinator",
                    currentUser != null ? currentUser.getFullName() : "Coordinator",
                    new SettingsAndAuditRepository.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            pbEligibility.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Eligibility status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                            loadPendingStudents();
                        }

                        @Override
                        public void onError(Exception e) {
                            pbEligibility.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
        rvEligibility.setAdapter(adapter);

        loadPendingStudents();

        return view;
    }

    private void loadPendingStudents() {
        pbEligibility.setVisibility(View.VISIBLE);
        tvNoEligibility.setVisibility(View.GONE);

        settingsRepository.getAllUsers(new SettingsAndAuditRepository.ListCallback<User>() {
            @Override
            public void onSuccess(List<User> items) {
                pbEligibility.setVisibility(View.GONE);
                List<User> pendingProjectStudents = new ArrayList<>();
                for (User u : items) {
                    if (Constants.ROLE_STUDENT.equals(u.getRole())
                            && Constants.OPTION_PROJECT.equals(u.getServiceOption())) {
                        pendingProjectStudents.add(u);
                    }
                }
                adapter.setStudents(pendingProjectStudents);
                tvNoEligibility.setVisibility(pendingProjectStudents.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbEligibility.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

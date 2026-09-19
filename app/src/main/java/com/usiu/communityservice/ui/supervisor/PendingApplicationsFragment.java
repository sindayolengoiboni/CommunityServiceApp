package com.usiu.communityservice.ui.supervisor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ApplicationRepository;
import com.usiu.communityservice.util.UIUtils;

import java.util.List;

public class PendingApplicationsFragment extends Fragment {

    private RecyclerView rvApps;
    private ProgressBar pbApps;
    private TextView tvNoApps;
    private SupervisorApplicationAdapter adapter;
    private ApplicationRepository applicationRepository;
    private User currentUser;

    public static PendingApplicationsFragment newInstance(User user) {
        PendingApplicationsFragment fragment = new PendingApplicationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_applications, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        applicationRepository = ApplicationRepository.getInstance();

        rvApps = view.findViewById(R.id.rv_sup_applications);
        pbApps = view.findViewById(R.id.pb_sup_apps);
        tvNoApps = view.findViewById(R.id.tv_sup_no_apps);

        rvApps.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SupervisorApplicationAdapter(new SupervisorApplicationAdapter.OnApplicationDecisionListener() {
            @Override
            public void onApprove(Application application) {
                approveApplication(application);
            }

            @Override
            public void onReject(Application application) {
                showRejectDialog(application);
            }
        });
        rvApps.setAdapter(adapter);

        loadApplications();

        return view;
    }

    private void loadApplications() {
        if (currentUser == null) return;

        pbApps.setVisibility(View.VISIBLE);
        tvNoApps.setVisibility(View.GONE);

        String orgId = currentUser.getOrganizationId() != null ? currentUser.getOrganizationId() : "";

        applicationRepository.getOrganizationApplications(orgId, new ApplicationRepository.ListCallback<Application>() {
            @Override
            public void onSuccess(List<Application> items) {
                pbApps.setVisibility(View.GONE);
                adapter.setApplications(items);
                tvNoApps.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbApps.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void approveApplication(Application application) {
        pbApps.setVisibility(View.VISIBLE);
        applicationRepository.approveApplicationWithCapacityCheck(
                application.getId(),
                application.getOpportunityId(),
                currentUser.getUid(),
                currentUser.getFullName(),
                new ApplicationRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        pbApps.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Placement application approved! Slot recorded.", Toast.LENGTH_SHORT).show();
                        loadApplications();
                    }

                    @Override
                    public void onError(Exception e) {
                        pbApps.setVisibility(View.GONE);
                        if (getContext() != null) {
                            UIUtils.showErrorDialog(getContext(), "Approval Blocked", e.getMessage());
                        }
                    }
                }
        );
    }

    private void showRejectDialog(Application application) {
        if (getContext() == null) return;

        final EditText etReason = new EditText(getContext());
        etReason.setHint("State reason for rejection...");
        etReason.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(getContext())
                .setTitle("Reject Placement Application")
                .setMessage("Enter the reason for not accepting this student placement:")
                .setView(etReason)
                .setPositiveButton("Confirm Reject", (dialog, which) -> {
                    String reason = etReason.getText().toString().trim();
                    if (reason.isEmpty()) reason = "Placement capacity full or schedule mismatch.";

                    pbApps.setVisibility(View.VISIBLE);
                    applicationRepository.rejectApplication(
                            application.getId(),
                            currentUser.getUid(),
                            currentUser.getFullName(),
                            reason,
                            new ApplicationRepository.ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    pbApps.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Application rejected.", Toast.LENGTH_SHORT).show();
                                    loadApplications();
                                }

                                @Override
                                public void onError(Exception e) {
                                    pbApps.setVisibility(View.GONE);
                                    if (getContext() != null) {
                                        UIUtils.showErrorDialog(getContext(), "Rejection Error", e.getMessage());
                                    }
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

package com.usiu.communityservice.ui.coordinator;

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
import com.usiu.communityservice.data.models.Registration;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SupervisionRepository;
import com.usiu.communityservice.util.UIUtils;

import java.util.List;

public class SiteRegistrationApprovalsFragment extends Fragment {

    private RecyclerView rvRegistrations;
    private ProgressBar pbRegistrations;
    private TextView tvNoRegistrations;
    private RegistrationApprovalAdapter adapter;
    private SupervisionRepository supervisionRepository;
    private User currentUser;

    public static SiteRegistrationApprovalsFragment newInstance(User user) {
        SiteRegistrationApprovalsFragment fragment = new SiteRegistrationApprovalsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_approvals, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        supervisionRepository = SupervisionRepository.getInstance();

        rvRegistrations = view.findViewById(R.id.rv_registrations);
        pbRegistrations = view.findViewById(R.id.pb_registrations);
        tvNoRegistrations = view.findViewById(R.id.tv_no_registrations);

        rvRegistrations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegistrationApprovalAdapter(new RegistrationApprovalAdapter.OnRegistrationDecisionListener() {
            @Override
            public void onApprove(Registration registration) {
                performRegistrationReview(registration, true, "Approved official site registration.");
            }

            @Override
            public void onReject(Registration registration) {
                showRejectDialog(registration);
            }
        });
        rvRegistrations.setAdapter(adapter);

        loadRegistrations();

        return view;
    }

    private void loadRegistrations() {
        pbRegistrations.setVisibility(View.VISIBLE);
        tvNoRegistrations.setVisibility(View.GONE);

        supervisionRepository.getAllRegistrations(new SupervisionRepository.ListCallback<Registration>() {
            @Override
            public void onSuccess(List<Registration> items) {
                pbRegistrations.setVisibility(View.GONE);
                adapter.setRegistrations(items);
                tvNoRegistrations.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbRegistrations.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading registrations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performRegistrationReview(Registration registration, boolean approved, String comments) {
        pbRegistrations.setVisibility(View.VISIBLE);
        supervisionRepository.reviewRegistration(
                registration.getId(),
                approved,
                currentUser != null ? currentUser.getUid() : "coordinator",
                currentUser != null ? currentUser.getFullName() : "Coordinator",
                comments,
                new SupervisionRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        pbRegistrations.setVisibility(View.GONE);
                        Toast.makeText(getContext(), approved ? "Official site registration approved!" : "Registration rejected.", Toast.LENGTH_SHORT).show();
                        loadRegistrations();
                    }

                    @Override
                    public void onError(Exception e) {
                        pbRegistrations.setVisibility(View.GONE);
                        if (getContext() != null) {
                            UIUtils.showErrorDialog(getContext(), "Review Failed", e.getMessage());
                        }
                    }
                }
        );
    }

    private void showRejectDialog(Registration registration) {
        if (getContext() == null) return;

        final EditText etReason = new EditText(getContext());
        etReason.setHint("State reason for rejection...");
        etReason.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(getContext())
                .setTitle("Reject Site Registration")
                .setMessage("Provide details for rejecting this registration:")
                .setView(etReason)
                .setPositiveButton("Confirm Reject", (dialog, which) -> {
                    String reason = etReason.getText().toString().trim();
                    if (reason.isEmpty()) reason = "Unverified site or supervisor credentials.";
                    performRegistrationReview(registration, false, reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

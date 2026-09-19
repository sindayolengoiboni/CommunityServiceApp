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
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ServiceTrackingRepository;
import com.usiu.communityservice.util.UIUtils;

import java.util.List;

public class VerifyDiaryFragment extends Fragment {

    private RecyclerView rvDiary;
    private ProgressBar pbDiary;
    private TextView tvNoDiary;
    private SupervisorDiaryAdapter adapter;
    private ServiceTrackingRepository trackingRepository;
    private User currentUser;

    public static VerifyDiaryFragment newInstance(User user) {
        VerifyDiaryFragment fragment = new VerifyDiaryFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_diary, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        trackingRepository = ServiceTrackingRepository.getInstance();

        rvDiary = view.findViewById(R.id.rv_sup_diary);
        pbDiary = view.findViewById(R.id.pb_sup_diary);
        tvNoDiary = view.findViewById(R.id.tv_sup_no_diary);

        rvDiary.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SupervisorDiaryAdapter(new SupervisorDiaryAdapter.OnDiaryVerificationListener() {
            @Override
            public void onVerify(DiaryEntry entry) {
                showVerifyDialog(entry);
            }

            @Override
            public void onFlag(DiaryEntry entry) {
                showFlagDialog(entry);
            }
        });
        rvDiary.setAdapter(adapter);

        loadDiaryEntries();

        return view;
    }

    private void loadDiaryEntries() {
        if (currentUser == null) return;

        pbDiary.setVisibility(View.VISIBLE);
        tvNoDiary.setVisibility(View.GONE);

        String orgId = currentUser.getOrganizationId() != null ? currentUser.getOrganizationId() : "";

        trackingRepository.getOrganizationDiaryEntries(orgId, new ServiceTrackingRepository.ListCallback<DiaryEntry>() {
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
                    Toast.makeText(getContext(), "Error loading diary entries: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showVerifyDialog(DiaryEntry entry) {
        if (getContext() == null) return;

        final EditText etComments = new EditText(getContext());
        etComments.setHint("Add supervisor comments (optional)...");
        etComments.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(getContext())
                .setTitle("Verify Service Hours")
                .setMessage("Confirm and credit " + entry.getHoursRecorded() + " hours for " + entry.getStudentName() + "?")
                .setView(etComments)
                .setPositiveButton("Verify & Credit Hours", (dialog, which) -> {
                    String comments = etComments.getText().toString().trim();
                    if (comments.isEmpty()) comments = "Service verified by organization supervisor.";

                    pbDiary.setVisibility(View.VISIBLE);
                    trackingRepository.verifyDiaryEntry(
                            entry.getId(),
                            currentUser.getUid(),
                            currentUser.getFullName(),
                            comments,
                            new ServiceTrackingRepository.ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    pbDiary.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Hours verified and credited to student profile!", Toast.LENGTH_SHORT).show();
                                    loadDiaryEntries();
                                }

                                @Override
                                public void onError(Exception e) {
                                    pbDiary.setVisibility(View.GONE);
                                    if (getContext() != null) {
                                        UIUtils.showErrorDialog(getContext(), "Verification Failed", e.getMessage());
                                    }
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showFlagDialog(DiaryEntry entry) {
        if (getContext() == null) return;

        final EditText etReason = new EditText(getContext());
        etReason.setHint("State reason for flagging (e.g. incorrect hours)...");
        etReason.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(getContext())
                .setTitle("Flag Diary Entry")
                .setMessage("Enter the explanation for flagging this entry:")
                .setView(etReason)
                .setPositiveButton("Flag Entry", (dialog, which) -> {
                    String reason = etReason.getText().toString().trim();
                    if (reason.isEmpty()) reason = "Discrepancy in recorded service hours or tasks.";

                    pbDiary.setVisibility(View.VISIBLE);
                    trackingRepository.flagDiaryEntry(
                            entry.getId(),
                            currentUser.getUid(),
                            currentUser.getFullName(),
                            reason,
                            new ServiceTrackingRepository.ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    pbDiary.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Diary entry flagged.", Toast.LENGTH_SHORT).show();
                                    loadDiaryEntries();
                                }

                                @Override
                                public void onError(Exception e) {
                                    pbDiary.setVisibility(View.GONE);
                                    if (getContext() != null) {
                                        UIUtils.showErrorDialog(getContext(), "Flagging Error", e.getMessage());
                                    }
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

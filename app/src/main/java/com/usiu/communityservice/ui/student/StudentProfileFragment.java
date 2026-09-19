package com.usiu.communityservice.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.ui.auth.LoginActivity;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

public class StudentProfileFragment extends Fragment {

    private TextView tvName, tvId, tvEmail, tvPhone, tvTrack, tvEligibility;
    private Button btnUploadEvidence, btnSignOut;
    private AuthRepository authRepository;
    private User currentUser;

    public static StudentProfileFragment newInstance(User user) {
        StudentProfileFragment fragment = new StudentProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        authRepository = AuthRepository.getInstance();

        tvName = view.findViewById(R.id.tv_prof_name);
        tvId = view.findViewById(R.id.tv_prof_id);
        tvEmail = view.findViewById(R.id.tv_prof_email);
        tvPhone = view.findViewById(R.id.tv_prof_phone);
        tvTrack = view.findViewById(R.id.tv_prof_track);
        tvEligibility = view.findViewById(R.id.tv_prof_eligibility_badge);
        btnUploadEvidence = view.findViewById(R.id.btn_upload_employment_evidence);
        btnSignOut = view.findViewById(R.id.btn_prof_signout);

        displayUserData();

        btnUploadEvidence.setOnClickListener(v -> uploadEmploymentEvidence());

        btnSignOut.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void displayUserData() {
        if (currentUser == null) return;

        tvName.setText(currentUser.getFullName());
        tvId.setText("Student ID: " + currentUser.getStudentId());
        tvEmail.setText(currentUser.getEmail());
        tvPhone.setText(currentUser.getPhone());

        String track = Constants.OPTION_PROJECT.equals(currentUser.getServiceOption())
                ? "Track: Project-based Service (Working/Part-time)"
                : "Track: Hands-on Service (Full-time: 90 hours)";
        tvTrack.setText(track);

        UIUtils.styleStatusBadge(tvEligibility, currentUser.getEligibilityStatus());

        if (Constants.OPTION_PROJECT.equals(currentUser.getServiceOption())
                && !Constants.ELIGIBILITY_APPROVED.equals(currentUser.getEligibilityStatus())) {
            btnUploadEvidence.setVisibility(View.VISIBLE);
        } else {
            btnUploadEvidence.setVisibility(View.GONE);
        }
    }

    private void uploadEmploymentEvidence() {
        currentUser.setEmploymentProofUrl("https://storage.usiu.ac.ke/evidence/employment_proof_" + currentUser.getStudentId() + ".pdf");
        currentUser.setEligibilityStatus(Constants.ELIGIBILITY_PENDING);

        authRepository.updateUserProfile(currentUser, new AuthRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Employment evidence uploaded. Awaiting Coordinator approval.", Toast.LENGTH_LONG).show();
                displayUserData();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

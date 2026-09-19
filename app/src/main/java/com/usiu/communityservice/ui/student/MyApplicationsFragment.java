package com.usiu.communityservice.ui.student;

import android.content.Intent;
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
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ApplicationRepository;

import java.util.List;

public class MyApplicationsFragment extends Fragment {

    private RecyclerView rvApplications;
    private ProgressBar pbMyApps;
    private TextView tvNoApps;
    private ApplicationAdapter adapter;
    private ApplicationRepository applicationRepository;
    private User currentUser;

    public static MyApplicationsFragment newInstance(User user) {
        MyApplicationsFragment fragment = new MyApplicationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_applications, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        applicationRepository = ApplicationRepository.getInstance();

        rvApplications = view.findViewById(R.id.rv_my_applications);
        pbMyApps = view.findViewById(R.id.pb_my_apps);
        tvNoApps = view.findViewById(R.id.tv_no_apps);

        rvApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter(application -> {
            Intent intent = new Intent(getContext(), SiteRegistrationActivity.class);
            intent.putExtra("APP_EXTRA", application);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });
        rvApplications.setAdapter(adapter);

        loadApplications();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadApplications();
    }

    private void loadApplications() {
        if (currentUser == null) return;

        pbMyApps.setVisibility(View.VISIBLE);
        tvNoApps.setVisibility(View.GONE);

        applicationRepository.getStudentApplications(currentUser.getUid(), new ApplicationRepository.ListCallback<Application>() {
            @Override
            public void onSuccess(List<Application> items) {
                pbMyApps.setVisibility(View.GONE);
                adapter.setApplications(items);
                tvNoApps.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbMyApps.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

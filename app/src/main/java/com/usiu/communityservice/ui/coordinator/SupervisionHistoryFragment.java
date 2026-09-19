package com.usiu.communityservice.ui.coordinator;

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
import com.usiu.communityservice.data.models.SupervisionVisit;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SupervisionRepository;

import java.util.List;

public class SupervisionHistoryFragment extends Fragment {

    private RecyclerView rvSupervision;
    private ProgressBar pbSupervision;
    private TextView tvNoSupervision;
    private Button btnRecordVisit;
    private SupervisionAdapter adapter;
    private SupervisionRepository supervisionRepository;
    private User currentUser;

    public static SupervisionHistoryFragment newInstance(User user) {
        SupervisionHistoryFragment fragment = new SupervisionHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervision_history, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        supervisionRepository = SupervisionRepository.getInstance();

        rvSupervision = view.findViewById(R.id.rv_supervision_visits);
        pbSupervision = view.findViewById(R.id.pb_supervision);
        tvNoSupervision = view.findViewById(R.id.tv_no_supervision);
        btnRecordVisit = view.findViewById(R.id.btn_record_visit);

        rvSupervision.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SupervisionAdapter();
        rvSupervision.setAdapter(adapter);

        btnRecordVisit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RecordSupervisionVisitActivity.class);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });

        loadSupervisionVisits();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSupervisionVisits();
    }

    private void loadSupervisionVisits() {
        pbSupervision.setVisibility(View.VISIBLE);
        tvNoSupervision.setVisibility(View.GONE);

        supervisionRepository.getAllSupervisionVisits(new SupervisionRepository.ListCallback<SupervisionVisit>() {
            @Override
            public void onSuccess(List<SupervisionVisit> items) {
                pbSupervision.setVisibility(View.GONE);
                adapter.setVisits(items);
                tvNoSupervision.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbSupervision.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

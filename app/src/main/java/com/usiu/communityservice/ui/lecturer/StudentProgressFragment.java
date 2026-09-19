package com.usiu.communityservice.ui.lecturer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Displays all enrolled students with their verified-hours progress bar,
 * absence count, and service track. Supports name-based search.
 */
public class StudentProgressFragment extends Fragment {

    private RecyclerView rvStudents;
    private ProgressBar pbStudents;
    private TextView tvNoStudents;
    private TextView tvStudentCount;
    private EditText etSearchStudents;

    private StudentProgressAdapter adapter;
    private List<User> allStudents = new ArrayList<>();

    private SettingsAndAuditRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvStudents = view.findViewById(R.id.rv_students);
        pbStudents = view.findViewById(R.id.pb_students);
        tvNoStudents = view.findViewById(R.id.tv_no_students);
        tvStudentCount = view.findViewById(R.id.tv_student_count);
        etSearchStudents = view.findViewById(R.id.et_search_students);

        repository = new SettingsAndAuditRepository();

        adapter = new StudentProgressAdapter();
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudents.setAdapter(adapter);

        setupSearch();
        loadStudents();
    }

    private void setupSearch() {
        etSearchStudents.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterStudents(String query) {
        if (query.isEmpty()) {
            adapter.setStudents(allStudents);
        } else {
            List<User> filtered = new ArrayList<>();
            String lower = query.toLowerCase(Locale.ROOT);
            for (User u : allStudents) {
                if ((u.getFullName() != null && u.getFullName().toLowerCase(Locale.ROOT).contains(lower))
                        || (u.getStudentId() != null && u.getStudentId().toLowerCase(Locale.ROOT).contains(lower))) {
                    filtered.add(u);
                }
            }
            adapter.setStudents(filtered);
        }
    }

    private void loadStudents() {
        pbStudents.setVisibility(View.VISIBLE);

        repository.getAllUsers(users -> {
            if (!isAdded()) return;
            pbStudents.setVisibility(View.GONE);

            // Filter to STUDENT role only
            allStudents.clear();
            for (User u : users) {
                if ("STUDENT".equals(u.getRole())) {
                    allStudents.add(u);
                }
            }

            if (allStudents.isEmpty()) {
                tvNoStudents.setVisibility(View.VISIBLE);
                tvStudentCount.setText("");
            } else {
                tvNoStudents.setVisibility(View.GONE);
                tvStudentCount.setText(
                        getString(R.string.label_student_count, allStudents.size()));
                adapter.setStudents(allStudents);
            }
        }, e -> {
            if (!isAdded()) return;
            pbStudents.setVisibility(View.GONE);
            tvNoStudents.setText(R.string.error_loading_data);
            tvNoStudents.setVisibility(View.VISIBLE);
        });
    }
}

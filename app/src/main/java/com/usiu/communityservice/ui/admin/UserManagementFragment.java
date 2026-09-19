package com.usiu.communityservice.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.usiu.communityservice.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Displays all registered users and allows the Admin to search/filter by role
 * and tap a user to open EditUserRoleActivity for role/status changes.
 */
public class UserManagementFragment extends Fragment {

    private RecyclerView rvUsers;
    private ProgressBar pbUsers;
    private TextView tvNoUsers;
    private EditText etSearchUsers;
    private AutoCompleteTextView spinnerRoleFilter;

    private UserAdminAdapter adapter;
    private List<User> allUsers = new ArrayList<>();
    private String currentRoleFilter = "All";

    private SettingsAndAuditRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUsers = view.findViewById(R.id.rv_users);
        pbUsers = view.findViewById(R.id.pb_users);
        tvNoUsers = view.findViewById(R.id.tv_no_users);
        etSearchUsers = view.findViewById(R.id.et_search_users);
        spinnerRoleFilter = view.findViewById(R.id.spinner_role_filter);

        repository = new SettingsAndAuditRepository();

        setupAdapter();
        setupRoleFilter();
        setupSearch();
        loadUsers();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list when returning from EditUserRoleActivity
        loadUsers();
    }

    private void setupAdapter() {
        adapter = new UserAdminAdapter(user -> {
            Intent intent = new Intent(getContext(), EditUserRoleActivity.class);
            intent.putExtra(EditUserRoleActivity.EXTRA_USER_ID, user.getUid());
            startActivity(intent);
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);
    }

    private void setupRoleFilter() {
        String[] roles = { "All", Constants.ROLE_STUDENT, Constants.ROLE_SUPERVISOR,
                Constants.ROLE_COORDINATOR, Constants.ROLE_LECTURER, Constants.ROLE_ADMIN };
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRoleFilter.setAdapter(roleAdapter);
        spinnerRoleFilter.setText("All", false);

        spinnerRoleFilter.setOnItemClickListener((parent, v, position, id) -> {
            currentRoleFilter = roles[position];
            applyFilters();
        });
    }

    private void setupSearch() {
        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        String query = etSearchUsers.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<User> filtered = new ArrayList<>();
        for (User u : allUsers) {
            boolean roleMatch = "All".equals(currentRoleFilter) || currentRoleFilter.equals(u.getRole());
            boolean searchMatch = query.isEmpty()
                    || (u.getFullName() != null && u.getFullName().toLowerCase(Locale.ROOT).contains(query))
                    || (u.getEmail() != null && u.getEmail().toLowerCase(Locale.ROOT).contains(query));
            if (roleMatch && searchMatch) filtered.add(u);
        }
        adapter.setUsers(filtered);
        tvNoUsers.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void loadUsers() {
        pbUsers.setVisibility(View.VISIBLE);
        tvNoUsers.setVisibility(View.GONE);

        repository.getAllUsers(users -> {
            if (!isAdded()) return;
            pbUsers.setVisibility(View.GONE);
            allUsers.clear();
            if (users != null) allUsers.addAll(users);
            applyFilters();
        }, e -> {
            if (!isAdded()) return;
            pbUsers.setVisibility(View.GONE);
            tvNoUsers.setText(R.string.error_loading_data);
            tvNoUsers.setVisibility(View.VISIBLE);
        });
    }
}

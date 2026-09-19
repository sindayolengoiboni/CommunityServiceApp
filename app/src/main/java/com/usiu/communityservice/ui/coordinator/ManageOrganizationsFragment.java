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
import com.usiu.communityservice.data.models.Organization;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.OrganizationRepository;

import java.util.List;

public class ManageOrganizationsFragment extends Fragment {

    private RecyclerView rvOrgs;
    private ProgressBar pbOrgs;
    private TextView tvNoOrgs;
    private Button btnAddOrg;
    private OrgAdapter adapter;
    private OrganizationRepository organizationRepository;
    private User currentUser;

    public static ManageOrganizationsFragment newInstance(User user) {
        ManageOrganizationsFragment fragment = new ManageOrganizationsFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_orgs, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        organizationRepository = OrganizationRepository.getInstance();

        rvOrgs = view.findViewById(R.id.rv_coord_orgs);
        pbOrgs = view.findViewById(R.id.pb_coord_orgs);
        tvNoOrgs = view.findViewById(R.id.tv_coord_no_orgs);
        btnAddOrg = view.findViewById(R.id.btn_coord_add_org);

        rvOrgs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrgAdapter(org -> toggleOrgStatus(org));
        rvOrgs.setAdapter(adapter);

        btnAddOrg.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditOrganizationActivity.class);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });

        loadOrganizations();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrganizations();
    }

    private void loadOrganizations() {
        pbOrgs.setVisibility(View.VISIBLE);
        tvNoOrgs.setVisibility(View.GONE);

        organizationRepository.getAllOrganizations(new OrganizationRepository.ListCallback<Organization>() {
            @Override
            public void onSuccess(List<Organization> items) {
                pbOrgs.setVisibility(View.GONE);
                adapter.setOrganizations(items);
                tvNoOrgs.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbOrgs.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toggleOrgStatus(Organization org) {
        org.setActive(!org.isActive());
        organizationRepository.saveOrganization(org, new OrganizationRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Organization status updated.", Toast.LENGTH_SHORT).show();
                loadOrganizations();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

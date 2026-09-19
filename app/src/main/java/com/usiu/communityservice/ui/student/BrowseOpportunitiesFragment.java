package com.usiu.communityservice.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.usiu.communityservice.data.models.Opportunity;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.OrganizationRepository;

import java.util.ArrayList;
import java.util.List;

public class BrowseOpportunitiesFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvOpportunities;
    private ProgressBar pbOpps;
    private TextView tvNoOpps;
    private OpportunityAdapter adapter;
    private final List<Opportunity> allOpportunities = new ArrayList<>();
    private OrganizationRepository organizationRepository;
    private User currentUser;

    public static BrowseOpportunitiesFragment newInstance(User user) {
        BrowseOpportunitiesFragment fragment = new BrowseOpportunitiesFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_opportunities, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        organizationRepository = OrganizationRepository.getInstance();

        etSearch = view.findViewById(R.id.et_search_opps);
        rvOpportunities = view.findViewById(R.id.rv_opportunities);
        pbOpps = view.findViewById(R.id.pb_opps);
        tvNoOpps = view.findViewById(R.id.tv_no_opps);

        rvOpportunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OpportunityAdapter(opportunity -> {
            Intent intent = new Intent(getContext(), OpportunityDetailsActivity.class);
            intent.putExtra("OPP_EXTRA", opportunity);
            intent.putExtra("USER_EXTRA", currentUser);
            startActivity(intent);
        });
        rvOpportunities.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOpportunities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadOpportunities();

        return view;
    }

    private void loadOpportunities() {
        pbOpps.setVisibility(View.VISIBLE);
        tvNoOpps.setVisibility(View.GONE);

        organizationRepository.getApprovedOpportunities(new OrganizationRepository.ListCallback<Opportunity>() {
            @Override
            public void onSuccess(List<Opportunity> items) {
                pbOpps.setVisibility(View.GONE);
                allOpportunities.clear();
                allOpportunities.addAll(items);
                adapter.setOpportunities(allOpportunities);
                tvNoOpps.setVisibility(allOpportunities.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pbOpps.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading opportunities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterOpportunities(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setOpportunities(allOpportunities);
            return;
        }

        String lower = query.toLowerCase().trim();
        List<Opportunity> filtered = new ArrayList<>();
        for (Opportunity opp : allOpportunities) {
            boolean matchesTitle = opp.getTitle() != null && opp.getTitle().toLowerCase().contains(lower);
            boolean matchesOrg = opp.getOrganizationName() != null && opp.getOrganizationName().toLowerCase().contains(lower);
            boolean matchesLoc = opp.getLocation() != null && opp.getLocation().toLowerCase().contains(lower);

            if (matchesTitle || matchesOrg || matchesLoc) {
                filtered.add(opp);
            }
        }
        adapter.setOpportunities(filtered);
    }
}

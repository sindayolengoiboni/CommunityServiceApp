package com.usiu.communityservice.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.ui.coordinator.AuditLogAdapter;

/**
 * Admin-facing system audit log fragment.
 * Reuses {@link AuditLogAdapter} from the coordinator package to display
 * the immutable, append-only audit trail of all significant system actions.
 */
public class SystemAuditFragment extends Fragment {

    private RecyclerView rvSystemAudit;
    private ProgressBar pbSystemAudit;
    private TextView tvNoAuditAdmin;

    private AuditLogAdapter auditLogAdapter;
    private SettingsAndAuditRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system_audit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSystemAudit = view.findViewById(R.id.rv_system_audit);
        pbSystemAudit = view.findViewById(R.id.pb_system_audit);
        tvNoAuditAdmin = view.findViewById(R.id.tv_no_audit_admin);

        repository = new SettingsAndAuditRepository();

        auditLogAdapter = new AuditLogAdapter();
        rvSystemAudit.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSystemAudit.setAdapter(auditLogAdapter);

        loadAuditLogs();
    }

    private void loadAuditLogs() {
        pbSystemAudit.setVisibility(View.VISIBLE);
        tvNoAuditAdmin.setVisibility(View.GONE);

        repository.getAuditLogs(logs -> {
            if (!isAdded()) return;
            pbSystemAudit.setVisibility(View.GONE);
            if (logs == null || logs.isEmpty()) {
                tvNoAuditAdmin.setVisibility(View.VISIBLE);
            } else {
                auditLogAdapter.setLogs(logs);
            }
        }, e -> {
            if (!isAdded()) return;
            pbSystemAudit.setVisibility(View.GONE);
            tvNoAuditAdmin.setText(R.string.error_loading_audit_logs);
            tvNoAuditAdmin.setVisibility(View.VISIBLE);
        });
    }
}

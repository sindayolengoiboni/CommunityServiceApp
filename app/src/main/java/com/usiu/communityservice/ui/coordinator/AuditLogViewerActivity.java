package com.usiu.communityservice.ui.coordinator;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;

/**
 * Displays an immutable, append-only audit log of all significant actions
 * in the system (approvals, role changes, settings edits, report reviews, etc.).
 * Accessible only to Coordinators and Admins.
 */
public class AuditLogViewerActivity extends AppCompatActivity {

    private RecyclerView rvAuditLogs;
    private ProgressBar pbAudit;
    private TextView tvNoAudit;

    private SettingsAndAuditRepository auditRepository;
    private AuditLogAdapter auditLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_log_viewer);

        initViews();
        setupRecyclerView();
        setupToolbar();
        loadAuditLogs();
    }

    private void initViews() {
        rvAuditLogs = findViewById(R.id.rv_audit_logs);
        pbAudit = findViewById(R.id.pb_audit);
        tvNoAudit = findViewById(R.id.tv_no_audit);
        auditRepository = new SettingsAndAuditRepository();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_audit_log);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        auditLogAdapter = new AuditLogAdapter();
        rvAuditLogs.setLayoutManager(new LinearLayoutManager(this));
        rvAuditLogs.setAdapter(auditLogAdapter);
    }

    private void loadAuditLogs() {
        pbAudit.setVisibility(View.VISIBLE);
        tvNoAudit.setVisibility(View.GONE);

        auditRepository.getAuditLogs(logs -> {
            pbAudit.setVisibility(View.GONE);
            if (logs == null || logs.isEmpty()) {
                tvNoAudit.setVisibility(View.VISIBLE);
            } else {
                tvNoAudit.setVisibility(View.GONE);
                auditLogAdapter.setLogs(logs);
            }
        }, e -> {
            pbAudit.setVisibility(View.GONE);
            tvNoAudit.setText(R.string.error_loading_audit_logs);
            tvNoAudit.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

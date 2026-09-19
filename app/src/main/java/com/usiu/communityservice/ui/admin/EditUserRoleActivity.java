package com.usiu.communityservice.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.SettingsAndAuditRepository;
import com.usiu.communityservice.util.Constants;

import java.util.Objects;

/**
 * Allows an Admin to change a user's role and account status.
 * All changes are written atomically to Firestore and create an immutable
 * audit log entry. Admins cannot demote themselves.
 */
public class EditUserRoleActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    private TextView tvUserName, tvUserEmail, tvCurrentRole, tvCurrentStatus;
    private AutoCompleteTextView spinnerNewRole, spinnerNewStatus;
    private TextInputEditText etReason;
    private Button btnSave;
    private ProgressBar pbSave;

    private SettingsAndAuditRepository repository;
    private User targetUser;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_role);

        targetUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (targetUserId == null) {
            Toast.makeText(this, R.string.error_user_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupSpinners();
        loadUser();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_edit_user_name);
        tvUserEmail = findViewById(R.id.tv_edit_user_email);
        tvCurrentRole = findViewById(R.id.tv_edit_user_current_role);
        tvCurrentStatus = findViewById(R.id.tv_edit_user_current_status);
        spinnerNewRole = findViewById(R.id.spinner_new_role);
        spinnerNewStatus = findViewById(R.id.spinner_new_status);
        etReason = findViewById(R.id.et_role_change_reason);
        btnSave = findViewById(R.id.btn_save_role);
        pbSave = findViewById(R.id.pb_role_save);
        repository = new SettingsAndAuditRepository();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit_user_role);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSpinners() {
        // All assignable roles
        String[] roles = {
                Constants.ROLE_STUDENT,
                Constants.ROLE_SUPERVISOR,
                Constants.ROLE_COORDINATOR,
                Constants.ROLE_LECTURER,
                Constants.ROLE_ADMIN
        };
        spinnerNewRole.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, roles));

        // Account statuses
        String[] statuses = { "ACTIVE", "SUSPENDED", "INACTIVE" };
        spinnerNewStatus.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, statuses));
    }

    private void loadUser() {
        repository.getUserById(targetUserId, user -> {
            targetUser = user;
            populateCurrentInfo(user);
            btnSave.setOnClickListener(v -> confirmAndSave());
        }, e -> {
            Toast.makeText(this, R.string.error_user_not_found, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void populateCurrentInfo(User user) {
        tvUserName.setText(user.getFullName());
        tvUserEmail.setText(user.getEmail());
        tvCurrentRole.setText(user.getRole());
        tvCurrentStatus.setText(user.getAccountStatus());
        // Pre-select current values
        spinnerNewRole.setText(user.getRole(), false);
        spinnerNewStatus.setText(user.getAccountStatus(), false);
    }

    private void confirmAndSave() {
        // Guard: admin cannot change their own role
        String currentAdminUid = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()).getUid();
        if (currentAdminUid.equals(targetUserId)) {
            Toast.makeText(this, R.string.error_cannot_edit_self, Toast.LENGTH_SHORT).show();
            return;
        }

        String newRole = spinnerNewRole.getText().toString().trim();
        String newStatus = spinnerNewStatus.getText().toString().trim();
        String reason = Objects.requireNonNull(etReason.getText()).toString().trim();

        if (newRole.isEmpty()) {
            Toast.makeText(this, R.string.error_select_role, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newStatus.isEmpty()) {
            Toast.makeText(this, R.string.error_select_status, Toast.LENGTH_SHORT).show();
            return;
        }
        if (reason.isEmpty()) {
            etReason.setError(getString(R.string.error_reason_required));
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_changes)
                .setMessage(getString(R.string.msg_confirm_role_change,
                        targetUser.getFullName(), newRole, newStatus))
                .setPositiveButton(R.string.action_confirm, (d, w) -> saveChanges(newRole, newStatus, reason))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void saveChanges(String newRole, String newStatus, String reason) {
        pbSave.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String adminUid = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()).getUid();

        repository.updateUserRoleAndStatus(targetUserId, newRole, newStatus, reason, adminUid,
                unused -> {
                    pbSave.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.msg_user_updated, Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    pbSave.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, R.string.error_submit_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package com.usiu.communityservice.ui.coordinator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.usiu.communityservice.R;
import com.usiu.communityservice.ui.auth.LoginActivity;

/**
 * Main activity for the Coordinator role.
 * Hosts fragment navigation for: Organizations, Student Eligibility,
 * Site Registration Approvals, Supervision History, and Settings.
 */
public class CoordinatorMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvToolbarTitle;
    private ImageView ivCoordSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_main);

        initViews();
        setupBottomNavigation();
        setupSignOut();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new ManageOrganizationsFragment(), getString(R.string.nav_coord_orgs));
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_nav_coordinator);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivCoordSignout = findViewById(R.id.iv_coord_signout);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_coord_orgs) {
                fragment = new ManageOrganizationsFragment();
                title = getString(R.string.nav_coord_orgs);
            } else if (id == R.id.nav_coord_eligibility) {
                fragment = new StudentEligibilityFragment();
                title = getString(R.string.nav_coord_eligibility);
            } else if (id == R.id.nav_coord_registrations) {
                fragment = new SiteRegistrationApprovalsFragment();
                title = getString(R.string.nav_coord_registrations);
            } else if (id == R.id.nav_coord_supervision) {
                fragment = new SupervisionHistoryFragment();
                title = getString(R.string.nav_coord_supervision);
            } else if (id == R.id.nav_coord_settings) {
                fragment = new CoordinatorSettingsFragment();
                title = getString(R.string.nav_coord_settings);
            }

            if (fragment != null) {
                loadFragment(fragment, title);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.coordinator_fragment_container, fragment)
                .commit();
    }

    private void setupSignOut() {
        ivCoordSignout.setOnClickListener(v -> confirmSignOut());
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_confirm_message)
                .setPositiveButton(R.string.sign_out, (dialog, which) -> performSignOut())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performSignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

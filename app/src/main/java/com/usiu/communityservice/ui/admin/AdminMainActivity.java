package com.usiu.communityservice.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.usiu.communityservice.R;
import com.usiu.communityservice.ui.auth.LoginActivity;

/**
 * Main activity for the Admin role.
 * Hosts User Management (provision roles/accounts) and System Audit log fragments.
 * Admins are the only users who can assign non-STUDENT roles via the app.
 */
public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvToolbarTitle;
    private ImageView ivAdminSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initViews();
        setupBottomNavigation();
        setupSignOut();

        if (savedInstanceState == null) {
            loadFragment(new UserManagementFragment(), getString(R.string.nav_admin_users));
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_nav_admin);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivAdminSignout = findViewById(R.id.iv_admin_signout);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_admin_users) {
                fragment = new UserManagementFragment();
                title = getString(R.string.nav_admin_users);
            } else if (id == R.id.nav_admin_audit) {
                fragment = new SystemAuditFragment();
                title = getString(R.string.nav_admin_audit);
            }

            if (fragment != null) {
                loadFragment(fragment, title);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }

    private void setupSignOut() {
        ivAdminSignout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.sign_out)
                        .setMessage(R.string.sign_out_confirm_message)
                        .setPositiveButton(R.string.sign_out, (d, w) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show());
    }
}

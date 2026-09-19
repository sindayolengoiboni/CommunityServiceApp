package com.usiu.communityservice.ui.lecturer;

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
 * Main activity for the Lecturer role.
 * Hosts navigation for Student Progress tracking and Report Reviews.
 */
public class LecturerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvToolbarTitle;
    private ImageView ivLecturerSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_main);

        initViews();
        setupBottomNavigation();
        setupSignOut();

        if (savedInstanceState == null) {
            loadFragment(new StudentProgressFragment(), getString(R.string.nav_lecturer_progress));
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_nav_lecturer);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivLecturerSignout = findViewById(R.id.iv_lecturer_signout);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_lecturer_progress) {
                fragment = new StudentProgressFragment();
                title = getString(R.string.nav_lecturer_progress);
            } else if (id == R.id.nav_lecturer_reports) {
                fragment = new ReportReviewsFragment();
                title = getString(R.string.nav_lecturer_reports);
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
                .replace(R.id.lecturer_fragment_container, fragment)
                .commit();
    }

    private void setupSignOut() {
        ivLecturerSignout.setOnClickListener(v -> new AlertDialog.Builder(this)
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

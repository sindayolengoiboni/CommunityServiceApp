package com.usiu.communityservice.ui.supervisor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.ui.auth.LoginActivity;

public class SupervisorMainActivity extends AppCompatActivity {

    private User currentUser;
    private AuthRepository authRepository;

    private TextView tvHeaderName, tvHeaderOrg;
    private ImageView ivSignOut;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main);

        authRepository = AuthRepository.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        tvHeaderName = findViewById(R.id.tv_sup_header_name);
        tvHeaderOrg = findViewById(R.id.tv_sup_header_org);
        ivSignOut = findViewById(R.id.iv_sup_signout);
        bottomNav = findViewById(R.id.supervisor_bottom_nav);

        if (currentUser != null) {
            tvHeaderName.setText(currentUser.getFullName());
            tvHeaderOrg.setText("Supervisor • " + (currentUser.getOrganizationId() != null ? currentUser.getOrganizationId() : "Assigned Organization"));
        }

        ivSignOut.setOnClickListener(v -> {
            authRepository.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_supervisor_apps) {
                selected = PendingApplicationsFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_supervisor_attendance) {
                selected = VerifyAttendanceFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_supervisor_diary) {
                selected = VerifyDiaryFragment.newInstance(currentUser);
            } else if (itemId == R.id.nav_supervisor_report_issue) {
                Intent intent = new Intent(this, ReportIssueActivity.class);
                intent.putExtra("USER_EXTRA", currentUser);
                startActivity(intent);
                return false;
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.supervisor_fragment_container, selected)
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.supervisor_fragment_container, PendingApplicationsFragment.newInstance(currentUser))
                    .commit();
        }
    }
}

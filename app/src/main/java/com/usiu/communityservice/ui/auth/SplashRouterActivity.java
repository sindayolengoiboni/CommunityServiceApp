package com.usiu.communityservice.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.ui.admin.AdminMainActivity;
import com.usiu.communityservice.ui.coordinator.CoordinatorMainActivity;
import com.usiu.communityservice.ui.lecturer.LecturerMainActivity;
import com.usiu.communityservice.ui.student.StudentMainActivity;
import com.usiu.communityservice.ui.supervisor.SupervisorMainActivity;
import com.usiu.communityservice.util.Constants;

public class SplashRouterActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_router);

        authRepository = AuthRepository.getInstance();
        tvStatus = findViewById(R.id.tv_splash_status);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthAndRoute, 1200);
    }

    private void checkAuthAndRoute() {
        if (!authRepository.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvStatus.setText("Loading user role and permissions...");

        authRepository.getCurrentUserProfile(new AuthRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                routeUserByRole(user);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SplashRouterActivity.this, "Session error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                authRepository.signOut();
                startActivity(new Intent(SplashRouterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void routeUserByRole(User user) {
        Intent intent;
        String role = user.getRole();

        if (Constants.ROLE_STUDENT.equals(role)) {
            intent = new Intent(this, StudentMainActivity.class);
        } else if (Constants.ROLE_SUPERVISOR.equals(role)) {
            intent = new Intent(this, SupervisorMainActivity.class);
        } else if (Constants.ROLE_COORDINATOR.equals(role)) {
            intent = new Intent(this, CoordinatorMainActivity.class);
        } else if (Constants.ROLE_LECTURER.equals(role)) {
            intent = new Intent(this, LecturerMainActivity.class);
        } else if (Constants.ROLE_ADMIN.equals(role)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            Toast.makeText(this, "Unknown role assigned: " + role, Toast.LENGTH_SHORT).show();
            intent = new Intent(this, LoginActivity.class);
        }

        intent.putExtra("USER_EXTRA", user);
        startActivity(intent);
        finish();
    }
}

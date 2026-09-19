package com.usiu.communityservice.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.util.UIUtils;
import com.usiu.communityservice.util.ValidationUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnSend;
    private ProgressBar pbReset;
    private TextView tvBackToLogin;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authRepository = AuthRepository.getInstance();

        etEmail = findViewById(R.id.et_reset_email);
        btnSend = findViewById(R.id.btn_send_reset);
        pbReset = findViewById(R.id.pb_reset);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        btnSend.setOnClickListener(v -> performPasswordReset());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void performPasswordReset() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email) || !ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        pbReset.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        authRepository.sendPasswordReset(email)
                .addOnSuccessListener(aVoid -> {
                    pbReset.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbReset.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    UIUtils.showErrorDialog(this, "Reset Request Failed", e.getMessage());
                });
    }
}

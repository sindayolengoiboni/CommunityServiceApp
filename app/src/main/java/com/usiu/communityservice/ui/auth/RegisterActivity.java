package com.usiu.communityservice.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.repositories.AuthRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;
import com.usiu.communityservice.util.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etStudentId, etEmail, etPhone, etPassword;
    private Spinner spStudentStatus;
    private RadioGroup rgServiceOption;
    private TextView tvProjectNote, tvGoToLogin;
    private Button btnRegister;
    private ProgressBar pbRegister;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = AuthRepository.getInstance();

        etName = findViewById(R.id.et_reg_name);
        etStudentId = findViewById(R.id.et_reg_student_id);
        etEmail = findViewById(R.id.et_reg_email);
        etPhone = findViewById(R.id.et_reg_phone);
        etPassword = findViewById(R.id.et_reg_password);
        spStudentStatus = findViewById(R.id.sp_student_status);
        rgServiceOption = findViewById(R.id.rg_service_option);
        tvProjectNote = findViewById(R.id.tv_project_note);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);
        btnRegister = findViewById(R.id.btn_register);
        pbRegister = findViewById(R.id.pb_register);

        setupStatusSpinner();

        rgServiceOption.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_project_based) {
                tvProjectNote.setVisibility(View.VISIBLE);
            } else {
                tvProjectNote.setVisibility(View.GONE);
            }
        });

        btnRegister.setOnClickListener(v -> performRegistration());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void setupStatusSpinner() {
        String[] statuses = {
                "Full-Time Student",
                "Part-Time Student",
                "Working Student"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spStudentStatus.setAdapter(adapter);
    }

    private void performRegistration() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validations
        if (TextUtils.isEmpty(name)) {
            etName.setError("Full name is required");
            etName.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidStudentId(studentId)) {
            etStudentId.setError("Enter a valid USIU student ID (5-8 digits)");
            etStudentId.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Valid email required");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isInstitutionalEmail(email)) {
            etEmail.setError("Please use your official university email (" + Constants.INSTITUTION_EMAIL_DOMAIN + ")");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        String studentStatus;
        int statusPos = spStudentStatus.getSelectedItemPosition();
        if (statusPos == 1) studentStatus = Constants.STATUS_PART_TIME;
        else if (statusPos == 2) studentStatus = Constants.STATUS_WORKING;
        else studentStatus = Constants.STATUS_FULL_TIME;

        String serviceOption = (rgServiceOption.getCheckedRadioButtonId() == R.id.rb_project_based)
                ? Constants.OPTION_PROJECT
                : Constants.OPTION_HANDS_ON;

        setLoading(true);

        authRepository.registerStudent(email, password, name, studentId, phone, studentStatus, serviceOption,
                new AuthRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this, "Student registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, SplashRouterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        setLoading(false);
                        UIUtils.showErrorDialog(RegisterActivity.this, "Registration Failed", e.getMessage());
                    }
                });
    }

    private void setLoading(boolean loading) {
        pbRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }
}

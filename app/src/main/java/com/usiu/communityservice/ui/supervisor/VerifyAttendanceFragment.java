package com.usiu.communityservice.ui.supervisor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AttendanceRecord;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ServiceTrackingRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.DateFormatter;
import com.usiu.communityservice.util.UIUtils;

public class VerifyAttendanceFragment extends Fragment {

    private TextInputEditText etStudentId, etStudentName, etDate, etNotes;
    private Spinner spStatus;
    private Button btnSave;
    private ProgressBar pbSave;

    private ServiceTrackingRepository trackingRepository;
    private User currentUser;

    public static VerifyAttendanceFragment newInstance(User user) {
        VerifyAttendanceFragment fragment = new VerifyAttendanceFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER_EXTRA", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_attendance, container, false);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("USER_EXTRA");
        }

        trackingRepository = ServiceTrackingRepository.getInstance();

        etStudentId = view.findViewById(R.id.et_att_student_id);
        etStudentName = view.findViewById(R.id.et_att_student_name);
        etDate = view.findViewById(R.id.et_att_date);
        etNotes = view.findViewById(R.id.et_att_notes);
        spStatus = view.findViewById(R.id.sp_att_status);
        btnSave = view.findViewById(R.id.btn_save_attendance);
        pbSave = view.findViewById(R.id.pb_save_att);

        etDate.setText(DateFormatter.getTodayString());

        setupStatusSpinner();

        btnSave.setOnClickListener(v -> saveAttendance());

        return view;
    }

    private void setupStatusSpinner() {
        String[] statuses = {
                Constants.ATT_STATUS_PRESENT,
                Constants.ATT_STATUS_ABSENT,
                Constants.ATT_STATUS_LATE,
                Constants.ATT_STATUS_EXCUSED
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spStatus.setAdapter(adapter);
    }

    private void saveAttendance() {
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String studentName = etStudentName.getText() != null ? etStudentName.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
        String status = spStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Student ID required");
            etStudentId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(studentName)) {
            etStudentName.setError("Student name required");
            etStudentName.requestFocus();
            return;
        }

        pbSave.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentUid(studentId); // Identifier for query
        record.setStudentIdNumber(studentId);
        record.setStudentName(studentName);
        record.setDate(date);
        record.setType(Constants.ATT_TYPE_SITE);
        record.setStatus(status);
        record.setNotes(notes);
        record.setVerified(true);
        record.setRecordedByUid(currentUser != null ? currentUser.getUid() : "supervisor");
        record.setRecordedByName(currentUser != null ? currentUser.getFullName() : "Supervisor");
        record.setRecordedByRole(Constants.ROLE_SUPERVISOR);

        trackingRepository.recordAttendance(record, new ServiceTrackingRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                pbSave.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(getContext(), "Site attendance record saved (" + status + ")", Toast.LENGTH_SHORT).show();
                etStudentId.setText("");
                etStudentName.setText("");
                etNotes.setText("");
            }

            @Override
            public void onError(Exception e) {
                pbSave.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                if (getContext() != null) {
                    UIUtils.showErrorDialog(getContext(), "Save Failed", e.getMessage());
                }
            }
        });
    }
}

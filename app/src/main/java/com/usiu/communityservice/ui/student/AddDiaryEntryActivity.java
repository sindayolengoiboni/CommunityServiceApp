package com.usiu.communityservice.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.ServiceTrackingRepository;
import com.usiu.communityservice.util.DateFormatter;
import com.usiu.communityservice.util.UIUtils;
import com.usiu.communityservice.util.ValidationUtils;

public class AddDiaryEntryActivity extends AppCompatActivity {

    private TextInputEditText etDate, etArrival, etDeparture, etHours, etActivities, etEvents, etReflection;
    private Button btnSave;
    private ProgressBar pbSave;
    private ServiceTrackingRepository trackingRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        trackingRepository = ServiceTrackingRepository.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        etDate = findViewById(R.id.et_diary_date);
        etArrival = findViewById(R.id.et_diary_arrival);
        etDeparture = findViewById(R.id.et_diary_departure);
        etHours = findViewById(R.id.et_diary_hours);
        etActivities = findViewById(R.id.et_diary_activities);
        etEvents = findViewById(R.id.et_diary_events);
        etReflection = findViewById(R.id.et_diary_reflection);
        btnSave = findViewById(R.id.btn_save_diary_entry);
        pbSave = findViewById(R.id.pb_save_diary);

        etDate.setText(DateFormatter.getTodayString());
        etArrival.setText("09:00");
        etDeparture.setText("12:00");
        etHours.setText("3.0");

        btnSave.setOnClickListener(v -> saveDiaryEntry());
    }

    private void saveDiaryEntry() {
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String arrival = etArrival.getText() != null ? etArrival.getText().toString().trim() : "";
        String departure = etDeparture.getText() != null ? etDeparture.getText().toString().trim() : "";
        String hoursStr = etHours.getText() != null ? etHours.getText().toString().trim() : "";
        String activities = etActivities.getText() != null ? etActivities.getText().toString().trim() : "";
        String events = etEvents.getText() != null ? etEvents.getText().toString().trim() : "";
        String reflection = etReflection.getText() != null ? etReflection.getText().toString().trim() : "";

        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        double hours;
        try {
            hours = Double.parseDouble(hoursStr);
        } catch (NumberFormatException e) {
            etHours.setError("Enter valid number of hours");
            etHours.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidDailyHours(hours)) {
            etHours.setError("Daily hours must be between 0.5 and 12.0");
            etHours.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(activities)) {
            etActivities.setError("Describe the activities performed");
            etActivities.requestFocus();
            return;
        }

        pbSave.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        DiaryEntry entry = new DiaryEntry();
        entry.setStudentUid(currentUser.getUid());
        entry.setStudentName(currentUser.getFullName());
        entry.setStudentIdNumber(currentUser.getStudentId());
        entry.setDate(date);
        entry.setArrivalTime(arrival);
        entry.setDepartureTime(departure);
        entry.setHoursRecorded(hours);
        entry.setActivitiesPerformed(activities);
        entry.setMeetingsOrEvents(events);
        entry.setStudentComments(reflection);

        trackingRepository.addDiaryEntry(entry, new ServiceTrackingRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                pbSave.setVisibility(View.GONE);
                Toast.makeText(AddDiaryEntryActivity.this, "Diary entry recorded. Pending supervisor verification.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                pbSave.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                UIUtils.showErrorDialog(AddDiaryEntryActivity.this, "Submission Failed", e.getMessage());
            }
        });
    }
}

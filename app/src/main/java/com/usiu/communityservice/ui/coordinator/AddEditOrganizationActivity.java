package com.usiu.communityservice.ui.coordinator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Opportunity;
import com.usiu.communityservice.data.models.Organization;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.data.repositories.OrganizationRepository;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;
import com.usiu.communityservice.util.ValidationUtils;

import java.util.Arrays;

public class AddEditOrganizationActivity extends AppCompatActivity {

    private TextInputEditText etName, etType, etLocation, etCommunity, etCapacity, etContactPerson, etContactEmail, etContactPhone, etDescription;
    private Button btnSave;
    private ProgressBar pbSave;

    private OrganizationRepository orgRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_organization);

        orgRepository = OrganizationRepository.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("USER_EXTRA");

        etName = findViewById(R.id.et_org_name);
        etType = findViewById(R.id.et_org_type);
        etLocation = findViewById(R.id.et_org_location);
        etCommunity = findViewById(R.id.et_org_community_served);
        etCapacity = findViewById(R.id.et_org_capacity);
        etContactPerson = findViewById(R.id.et_org_contact_person);
        etContactEmail = findViewById(R.id.et_org_contact_email);
        etContactPhone = findViewById(R.id.et_org_contact_phone);
        etDescription = findViewById(R.id.et_org_description);
        btnSave = findViewById(R.id.btn_save_org);
        pbSave = findViewById(R.id.pb_save_org);

        btnSave.setOnClickListener(v -> saveOrganization());
    }

    private void saveOrganization() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String type = etType.getText() != null ? etType.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String community = etCommunity.getText() != null ? etCommunity.getText().toString().trim() : "";
        String capacityStr = etCapacity.getText() != null ? etCapacity.getText().toString().trim() : "";
        String person = etContactPerson.getText() != null ? etContactPerson.getText().toString().trim() : "";
        String email = etContactEmail.getText() != null ? etContactEmail.getText().toString().trim() : "";
        String phone = etContactPhone.getText() != null ? etContactPhone.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Organization name required");
            etName.requestFocus();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            etCapacity.setError("Enter valid numeric capacity");
            etCapacity.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etContactEmail.setError("Valid contact email required");
            etContactEmail.requestFocus();
            return;
        }

        pbSave.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Organization org = new Organization();
        org.setName(name);
        org.setType(type);
        org.setLocation(location);
        org.setCommunityServed(community);
        org.setTotalCapacity(capacity);
        org.setFilledSlots(0);
        org.setContactPerson(person);
        org.setContactEmail(email);
        org.setContactPhone(phone);
        org.setDescription(description);
        org.setSupportedOption(Constants.OPTION_HANDS_ON);
        org.setApproved(true); // Coordinator creating it approves it
        org.setActive(true);

        orgRepository.saveOrganization(org, new OrganizationRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                // Also create an associated Opportunity opening for this organization
                Opportunity opp = new Opportunity();
                opp.setOrganizationId(org.getId());
                opp.setOrganizationName(org.getName());
                opp.setTitle(org.getName() + " - Community Service Placement");
                opp.setDescription(description);
                opp.setLocation(location);
                opp.setServiceDays(Arrays.asList("Monday", "Wednesday", "Friday"));
                opp.setStartTime("09:00");
                opp.setEndTime("12:00");
                opp.setTotalSlots(capacity);
                opp.setFilledSlots(0);
                opp.setServiceOption(Constants.OPTION_HANDS_ON);
                opp.setActive(true);

                orgRepository.saveOpportunity(opp, new OrganizationRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        pbSave.setVisibility(View.GONE);
                        Toast.makeText(AddEditOrganizationActivity.this, "Organization and placement opportunity registered successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        pbSave.setVisibility(View.GONE);
                        finish();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                pbSave.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                UIUtils.showErrorDialog(AddEditOrganizationActivity.this, "Save Failed", e.getMessage());
            }
        });
    }
}

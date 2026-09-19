package com.usiu.communityservice.ui.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Registration;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class RegistrationApprovalAdapter extends RecyclerView.Adapter<RegistrationApprovalAdapter.ViewHolder> {

    public interface OnRegistrationDecisionListener {
        void onApprove(Registration registration);
        void onReject(Registration registration);
    }

    private final List<Registration> registrations = new ArrayList<>();
    private final OnRegistrationDecisionListener listener;

    public RegistrationApprovalAdapter(OnRegistrationDecisionListener listener) {
        this.listener = listener;
    }

    public void setRegistrations(List<Registration> list) {
        registrations.clear();
        if (list != null) {
            registrations.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registration_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Registration reg = registrations.get(position);

        holder.tvStudent.setText(reg.getStudentName() + " (" + reg.getStudentIdNumber() + ")");
        holder.tvOrg.setText("Site: " + reg.getOrganizationName());
        holder.tvSup.setText("Supervisor: " + reg.getSupervisorName() + " (" + reg.getSupervisorEmail() + ")");

        String days = (reg.getServiceDays() != null && !reg.getServiceDays().isEmpty())
                ? String.join(", ", reg.getServiceDays())
                : "Flexible Days";
        String time = (reg.getServiceStartTime() != null && reg.getServiceEndTime() != null)
                ? " (" + reg.getServiceStartTime() + " - " + reg.getServiceEndTime() + ")"
                : "";
        holder.tvSchedule.setText("Schedule: " + days + time);

        UIUtils.styleStatusBadge(holder.tvStatus, reg.getStatus());

        if (reg.isLateSubmission()) {
            holder.tvLateWarning.setVisibility(View.VISIBLE);
        } else {
            holder.tvLateWarning.setVisibility(View.GONE);
        }

        if (Constants.REG_STATUS_SUBMITTED.equals(reg.getStatus())) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(reg);
            });
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(reg);
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return registrations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudent, tvOrg, tvSup, tvSchedule, tvStatus, tvLateWarning;
        LinearLayout layoutActions;
        Button btnApprove, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tv_reg_approval_student);
            tvOrg = itemView.findViewById(R.id.tv_reg_approval_org);
            tvSup = itemView.findViewById(R.id.tv_reg_approval_sup);
            tvSchedule = itemView.findViewById(R.id.tv_reg_approval_schedule);
            tvStatus = itemView.findViewById(R.id.tv_reg_approval_status);
            tvLateWarning = itemView.findViewById(R.id.tv_reg_late_warning);
            layoutActions = itemView.findViewById(R.id.layout_reg_actions);
            btnApprove = itemView.findViewById(R.id.btn_reg_approve);
            btnReject = itemView.findViewById(R.id.btn_reg_reject);
        }
    }
}

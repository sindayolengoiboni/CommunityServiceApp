package com.usiu.communityservice.ui.supervisor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class SupervisorApplicationAdapter extends RecyclerView.Adapter<SupervisorApplicationAdapter.ViewHolder> {

    public interface OnApplicationDecisionListener {
        void onApprove(Application application);
        void onReject(Application application);
    }

    private final List<Application> applications = new ArrayList<>();
    private final OnApplicationDecisionListener listener;

    public SupervisorApplicationAdapter(OnApplicationDecisionListener listener) {
        this.listener = listener;
    }

    public void setApplications(List<Application> list) {
        applications.clear();
        if (list != null) {
            applications.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supervisor_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applications.get(position);

        holder.tvName.setText(app.getStudentName());
        holder.tvDetails.setText("ID: " + app.getStudentIdNumber() + " • " + app.getStudentEmail());
        holder.tvOpp.setText("Role: " + app.getOpportunityTitle());

        UIUtils.styleStatusBadge(holder.tvStatus, app.getStatus());

        if (Constants.APP_STATUS_PENDING.equals(app.getStatus())) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(app);
            });
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(app);
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvOpp, tvStatus;
        LinearLayout layoutActions;
        Button btnApprove, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_sup_app_student_name);
            tvDetails = itemView.findViewById(R.id.tv_sup_app_student_details);
            tvOpp = itemView.findViewById(R.id.tv_sup_app_opp);
            tvStatus = itemView.findViewById(R.id.tv_sup_app_status);
            layoutActions = itemView.findViewById(R.id.layout_sup_actions);
            btnApprove = itemView.findViewById(R.id.btn_sup_approve);
            btnReject = itemView.findViewById(R.id.btn_sup_reject);
        }
    }
}

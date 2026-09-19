package com.usiu.communityservice.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.DateFormatter;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    public interface OnSiteRegistrationClickListener {
        void onSiteRegistrationClick(Application application);
    }

    private final List<Application> applications = new ArrayList<>();
    private final OnSiteRegistrationClickListener regListener;

    public ApplicationAdapter(OnSiteRegistrationClickListener regListener) {
        this.regListener = regListener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applications.get(position);

        holder.tvTitle.setText(app.getOpportunityTitle());
        holder.tvOrg.setText(app.getOrganizationName());
        holder.tvDate.setText("Applied: " + DateFormatter.formatDisplayDate(app.getAppliedAt()));

        UIUtils.styleStatusBadge(holder.tvStatus, app.getStatus());

        if (Constants.APP_STATUS_REJECTED.equals(app.getStatus()) && app.getDecisionNotes() != null) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText("Reason: " + app.getDecisionNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }

        // Only allow site registration if approved by organization
        if (Constants.APP_STATUS_APPROVED.equals(app.getStatus())) {
            holder.btnSiteReg.setVisibility(View.VISIBLE);
            holder.btnSiteReg.setOnClickListener(v -> {
                if (regListener != null) {
                    regListener.onSiteRegistrationClick(app);
                }
            });
        } else {
            holder.btnSiteReg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvDate, tvStatus, tvNotes;
        Button btnSiteReg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_app_opp_title);
            tvOrg = itemView.findViewById(R.id.tv_app_org_name);
            tvDate = itemView.findViewById(R.id.tv_app_date);
            tvStatus = itemView.findViewById(R.id.tv_app_status_badge);
            tvNotes = itemView.findViewById(R.id.tv_app_notes);
            btnSiteReg = itemView.findViewById(R.id.btn_complete_site_reg);
        }
    }
}

package com.usiu.communityservice.ui.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.util.DateFormatter;

import java.util.ArrayList;
import java.util.List;

public class AuditLogAdapter extends RecyclerView.Adapter<AuditLogAdapter.ViewHolder> {

    private final List<AuditLog> logs = new ArrayList<>();

    public void setLogs(List<AuditLog> list) {
        logs.clear();
        if (list != null) {
            logs.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audit_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuditLog log = logs.get(position);

        holder.tvAction.setText(log.getActionType());
        holder.tvTime.setText(DateFormatter.formatDisplayDateTime(log.getTimestamp()));
        holder.tvActor.setText("Performed by: " + log.getPerformedByName() + " (" + log.getPerformedByRole() + ")");
        holder.tvDetails.setText(log.getDetails());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAction, tvTime, tvActor, tvDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAction = itemView.findViewById(R.id.tv_audit_action);
            tvTime = itemView.findViewById(R.id.tv_audit_time);
            tvActor = itemView.findViewById(R.id.tv_audit_actor);
            tvDetails = itemView.findViewById(R.id.tv_audit_details);
        }
    }
}

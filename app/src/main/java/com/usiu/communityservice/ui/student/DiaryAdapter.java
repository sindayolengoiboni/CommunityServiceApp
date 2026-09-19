package com.usiu.communityservice.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private final List<DiaryEntry> entries = new ArrayList<>();

    public void setEntries(List<DiaryEntry> list) {
        entries.clear();
        if (list != null) {
            entries.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntry entry = entries.get(position);

        holder.tvDate.setText(entry.getDate());
        holder.tvHours.setText(entry.getHoursRecorded() + " Hours");
        holder.tvTime.setText("🕒 " + entry.getArrivalTime() + " - " + entry.getDepartureTime());

        UIUtils.styleStatusBadge(holder.tvStatus, entry.getVerificationStatus());

        holder.tvActivities.setText("Activities: " + entry.getActivitiesPerformed());

        if (entry.getStudentComments() != null && !entry.getStudentComments().isEmpty()) {
            holder.tvReflection.setVisibility(View.VISIBLE);
            holder.tvReflection.setText("Reflection: " + entry.getStudentComments());
        } else {
            holder.tvReflection.setVisibility(View.GONE);
        }

        if (entry.getSupervisorComments() != null && !entry.getSupervisorComments().isEmpty()) {
            holder.tvSupComment.setVisibility(View.VISIBLE);
            holder.tvSupComment.setText("Supervisor Feedback: " + entry.getSupervisorComments());
        } else {
            holder.tvSupComment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHours, tvTime, tvStatus, tvActivities, tvReflection, tvSupComment;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_diary_date);
            tvHours = itemView.findViewById(R.id.tv_diary_hours);
            tvTime = itemView.findViewById(R.id.tv_diary_time);
            tvStatus = itemView.findViewById(R.id.tv_diary_status_badge);
            tvActivities = itemView.findViewById(R.id.tv_diary_activities);
            tvReflection = itemView.findViewById(R.id.tv_diary_reflection);
            tvSupComment = itemView.findViewById(R.id.tv_diary_sup_comment);
        }
    }
}

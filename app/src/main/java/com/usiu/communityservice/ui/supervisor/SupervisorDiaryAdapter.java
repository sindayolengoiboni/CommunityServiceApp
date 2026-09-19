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
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class SupervisorDiaryAdapter extends RecyclerView.Adapter<SupervisorDiaryAdapter.ViewHolder> {

    public interface OnDiaryVerificationListener {
        void onVerify(DiaryEntry entry);
        void onFlag(DiaryEntry entry);
    }

    private final List<DiaryEntry> entries = new ArrayList<>();
    private final OnDiaryVerificationListener listener;

    public SupervisorDiaryAdapter(OnDiaryVerificationListener listener) {
        this.listener = listener;
    }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supervisor_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntry entry = entries.get(position);

        holder.tvStudent.setText(entry.getStudentName() + " (" + entry.getStudentIdNumber() + ")");
        holder.tvDate.setText("Date: " + entry.getDate() + " (" + entry.getArrivalTime() + " - " + entry.getDepartureTime() + ")");
        holder.tvHours.setText(entry.getHoursRecorded() + " Hours");
        holder.tvActivities.setText("Tasks: " + entry.getActivitiesPerformed());

        UIUtils.styleStatusBadge(holder.tvStatus, entry.getVerificationStatus());

        if (Constants.DIARY_PENDING.equals(entry.getVerificationStatus())) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnVerify.setOnClickListener(v -> {
                if (listener != null) listener.onVerify(entry);
            });
            holder.btnFlag.setOnClickListener(v -> {
                if (listener != null) listener.onFlag(entry);
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudent, tvDate, tvHours, tvActivities, tvStatus;
        LinearLayout layoutActions;
        Button btnVerify, btnFlag;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tv_sup_diary_student);
            tvDate = itemView.findViewById(R.id.tv_sup_diary_date);
            tvHours = itemView.findViewById(R.id.tv_sup_diary_hours);
            tvActivities = itemView.findViewById(R.id.tv_sup_diary_activities);
            tvStatus = itemView.findViewById(R.id.tv_sup_diary_status);
            layoutActions = itemView.findViewById(R.id.layout_sup_diary_actions);
            btnVerify = itemView.findViewById(R.id.btn_sup_verify_hours);
            btnFlag = itemView.findViewById(R.id.btn_sup_flag_entry);
        }
    }
}

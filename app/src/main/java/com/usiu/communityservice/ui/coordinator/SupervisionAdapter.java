package com.usiu.communityservice.ui.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.SupervisionVisit;
import com.usiu.communityservice.util.DateFormatter;

import java.util.ArrayList;
import java.util.List;

public class SupervisionAdapter extends RecyclerView.Adapter<SupervisionAdapter.ViewHolder> {

    private final List<SupervisionVisit> visits = new ArrayList<>();

    public void setVisits(List<SupervisionVisit> list) {
        visits.clear();
        if (list != null) {
            visits.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supervision_visit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupervisionVisit visit = visits.get(position);

        holder.tvOrg.setText(visit.getOrganizationName());
        holder.tvDate.setText(DateFormatter.formatDisplayDate(visit.getVisitDate()));

        String students = (visit.getObservedStudentNames() != null && !visit.getObservedStudentNames().isEmpty())
                ? "Students Present: " + String.join(", ", visit.getObservedStudentNames())
                : "Students Present: Recorded on site";
        holder.tvStudents.setText(students);

        holder.tvObs.setText("Observations: " + visit.getActivitiesObserved());

        if (visit.getChallengesNoted() != null && !visit.getChallengesNoted().isEmpty()) {
            holder.tvChallenges.setVisibility(View.VISIBLE);
            holder.tvChallenges.setText("Challenges: " + visit.getChallengesNoted());
        } else {
            holder.tvChallenges.setVisibility(View.GONE);
        }

        if (visit.getFollowUpActions() != null && !visit.getFollowUpActions().isEmpty()) {
            holder.tvActions.setVisibility(View.VISIBLE);
            holder.tvActions.setText("Follow-up: " + visit.getFollowUpActions());
        } else {
            holder.tvActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrg, tvDate, tvStudents, tvObs, tvChallenges, tvActions;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrg = itemView.findViewById(R.id.tv_visit_org_name);
            tvDate = itemView.findViewById(R.id.tv_visit_date);
            tvStudents = itemView.findViewById(R.id.tv_visit_students_observed);
            tvObs = itemView.findViewById(R.id.tv_visit_observations);
            tvChallenges = itemView.findViewById(R.id.tv_visit_challenges);
            tvActions = itemView.findViewById(R.id.tv_visit_actions);
        }
    }
}

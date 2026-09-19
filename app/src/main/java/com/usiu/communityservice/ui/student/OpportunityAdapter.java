package com.usiu.communityservice.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Opportunity;

import java.util.ArrayList;
import java.util.List;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    public interface OnOpportunityClickListener {
        void onOpportunityClick(Opportunity opportunity);
    }

    private final List<Opportunity> opportunities = new ArrayList<>();
    private final OnOpportunityClickListener listener;

    public OpportunityAdapter(OnOpportunityClickListener listener) {
        this.listener = listener;
    }

    public void setOpportunities(List<Opportunity> list) {
        opportunities.clear();
        if (list != null) {
            opportunities.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Opportunity opp = opportunities.get(position);
        holder.tvTitle.setText(opp.getTitle());
        holder.tvOrg.setText(opp.getOrganizationName());
        holder.tvLocation.setText("📍 " + (opp.getLocation() != null ? opp.getLocation() : "N/A"));

        String days = (opp.getServiceDays() != null && !opp.getServiceDays().isEmpty())
                ? String.join(", ", opp.getServiceDays())
                : "Flexible Days";
        String time = (opp.getStartTime() != null && opp.getEndTime() != null)
                ? " (" + opp.getStartTime() + " - " + opp.getEndTime() + ")"
                : "";
        holder.tvSchedule.setText("🕒 " + days + time);

        int available = opp.getAvailableSlots();
        holder.tvSlots.setText(available + (available == 1 ? " slot left" : " slots left"));

        holder.tvDesc.setText(opp.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpportunityClick(opp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return opportunities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvLocation, tvSchedule, tvSlots, tvDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_opp_title);
            tvOrg = itemView.findViewById(R.id.tv_opp_org_name);
            tvLocation = itemView.findViewById(R.id.tv_opp_location);
            tvSchedule = itemView.findViewById(R.id.tv_opp_days_time);
            tvSlots = itemView.findViewById(R.id.tv_opp_slots_badge);
            tvDesc = itemView.findViewById(R.id.tv_opp_desc);
        }
    }
}

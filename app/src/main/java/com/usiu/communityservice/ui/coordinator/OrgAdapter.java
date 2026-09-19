package com.usiu.communityservice.ui.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.Organization;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.ViewHolder> {

    public interface OnOrgToggleListener {
        void onToggle(Organization org);
    }

    private final List<Organization> organizations = new ArrayList<>();
    private final OnOrgToggleListener toggleListener;

    public OrgAdapter(OnOrgToggleListener toggleListener) {
        this.toggleListener = toggleListener;
    }

    public void setOrganizations(List<Organization> list) {
        organizations.clear();
        if (list != null) {
            organizations.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_org_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organization org = organizations.get(position);

        holder.tvName.setText(org.getName());
        holder.tvTypeLoc.setText(org.getType() + " • " + (org.getLocation() != null ? org.getLocation() : "N/A"));
        holder.tvCapacity.setText("Capacity: " + org.getFilledSlots() + " / " + org.getTotalCapacity() + " Filled");

        String statusText = org.isApproved() ? "APPROVED" : "PENDING";
        UIUtils.styleStatusBadge(holder.tvBadge, statusText);

        holder.btnToggle.setText(org.isActive() ? "Deactivate" : "Activate");
        holder.btnToggle.setOnClickListener(v -> {
            if (toggleListener != null) {
                toggleListener.onToggle(org);
            }
        });
    }

    @Override
    public int getItemCount() {
        return organizations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTypeLoc, tvCapacity, tvBadge;
        Button btnToggle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_org_admin_name);
            tvTypeLoc = itemView.findViewById(R.id.tv_org_admin_type_loc);
            tvCapacity = itemView.findViewById(R.id.tv_org_admin_capacity);
            tvBadge = itemView.findViewById(R.id.tv_org_admin_badge);
            btnToggle = itemView.findViewById(R.id.btn_org_admin_toggle);
        }
    }
}

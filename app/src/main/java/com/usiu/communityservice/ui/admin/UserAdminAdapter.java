package com.usiu.communityservice.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the Admin user management list.
 * Shows full name, role badge, email, account status, and an Edit button.
 */
public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {

    public interface OnEditClickListener {
        void onEdit(User user);
    }

    private List<User> users = new ArrayList<>();
    private final OnEditClickListener listener;

    public UserAdminAdapter(OnEditClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvRoleBadge, tvEmail, tvStatus;
        Button btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_user_full_name);
            tvRoleBadge = itemView.findViewById(R.id.tv_user_role_badge);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            btnEdit = itemView.findViewById(R.id.btn_edit_user);
        }

        void bind(User user, OnEditClickListener listener) {
            tvFullName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());

            String role = user.getRole() != null ? user.getRole() : "N/A";
            tvRoleBadge.setText(role);
            // Color badge by role
            int bgColor;
            switch (role) {
                case "ADMIN":        bgColor = 0xFF8B0000; break; // dark red
                case "COORDINATOR":  bgColor = 0xFF003087; break; // USIU blue
                case "LECTURER":     bgColor = 0xFF1565C0; break;
                case "SUPERVISOR":   bgColor = 0xFF2E7D32; break;
                default:             bgColor = 0xFF757575; break; // grey for STUDENT
            }
            tvRoleBadge.getBackground().setTint(bgColor);

            String status = user.getAccountStatus() != null ? user.getAccountStatus() : "UNKNOWN";
            tvStatus.setText(status);
            tvStatus.setTextColor("ACTIVE".equals(status) ? 0xFF2E7D32 : 0xFFC62828);

            btnEdit.setOnClickListener(v -> listener.onEdit(user));
        }
    }
}

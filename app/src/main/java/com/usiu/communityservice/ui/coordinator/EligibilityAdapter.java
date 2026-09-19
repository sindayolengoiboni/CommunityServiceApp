package com.usiu.communityservice.ui.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.util.Constants;
import com.usiu.communityservice.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class EligibilityAdapter extends RecyclerView.Adapter<EligibilityAdapter.ViewHolder> {

    public interface OnEligibilityDecisionListener {
        void onDecision(User student, String newStatus);
    }

    private final List<User> students = new ArrayList<>();
    private final OnEligibilityDecisionListener listener;

    public EligibilityAdapter(OnEligibilityDecisionListener listener) {
        this.listener = listener;
    }

    public void setStudents(List<User> list) {
        students.clear();
        if (list != null) {
            students.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eligibility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User student = students.get(position);

        holder.tvName.setText(student.getFullName());
        holder.tvDetails.setText("ID: " + student.getStudentId() + " • " + student.getStudentStatus());

        String proof = student.getEmploymentProofUrl() != null
                ? "📄 Evidence Submitted: " + student.getEmploymentProofUrl()
                : "⚠️ No employment proof uploaded yet.";
        holder.tvProof.setText(proof);

        UIUtils.styleStatusBadge(holder.tvBadge, student.getEligibilityStatus());

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onDecision(student, Constants.ELIGIBILITY_APPROVED);
        });

        holder.btnCorrection.setOnClickListener(v -> {
            if (listener != null) listener.onDecision(student, Constants.ELIGIBILITY_CORRECTION_NEEDED);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onDecision(student, Constants.ELIGIBILITY_REJECTED);
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvProof, tvBadge;
        Button btnApprove, btnCorrection, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_elig_student_name);
            tvDetails = itemView.findViewById(R.id.tv_elig_details);
            tvProof = itemView.findViewById(R.id.tv_elig_proof_url);
            tvBadge = itemView.findViewById(R.id.tv_elig_badge);
            btnApprove = itemView.findViewById(R.id.btn_elig_approve);
            btnCorrection = itemView.findViewById(R.id.btn_elig_correction);
            btnReject = itemView.findViewById(R.id.btn_elig_reject);
        }
    }
}

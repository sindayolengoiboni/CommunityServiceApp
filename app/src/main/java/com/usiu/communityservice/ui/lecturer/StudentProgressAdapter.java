package com.usiu.communityservice.ui.lecturer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usiu.communityservice.R;
import com.usiu.communityservice.data.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter displaying each student's name, track, ID,
 * verified-hours progress bar, and absence counts for the lecturer.
 */
public class StudentProgressAdapter extends RecyclerView.Adapter<StudentProgressAdapter.ViewHolder> {

    private List<User> students = new ArrayList<>();

    public void setStudents(List<User> students) {
        this.students = students != null ? students : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_progress, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(students.get(position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTrackBadge, tvStudentId, tvHoursProgress, tvAbsenceCount;
        ProgressBar pbHours;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_student_name);
            tvTrackBadge = itemView.findViewById(R.id.tv_track_badge);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvHoursProgress = itemView.findViewById(R.id.tv_hours_progress);
            pbHours = itemView.findViewById(R.id.pb_hours);
            tvAbsenceCount = itemView.findViewById(R.id.tv_absence_count);
        }

        void bind(User user) {
            tvName.setText(user.getFullName());
            tvStudentId.setText(
                    itemView.getContext().getString(R.string.label_student_id_prefix, user.getStudentId()));

            // Track badge
            String track = user.getServiceTrack();
            tvTrackBadge.setText(track != null ? track : "N/A");

            // Hours progress
            double verified = user.getVerifiedHours();
            double target = 90.0; // Default; could come from CourseSettings
            tvHoursProgress.setText(String.format(Locale.ROOT, "%.0f / %.0f hrs", verified, target));
            int progress = (int) Math.min((verified / target) * 100, 100);
            pbHours.setProgress(progress);

            // Absences
            int site = user.getSiteAbsences();
            int classAbs = user.getClassAbsences();
            tvAbsenceCount.setText(
                    itemView.getContext().getString(R.string.label_absences_summary, site, classAbs));
        }
    }
}

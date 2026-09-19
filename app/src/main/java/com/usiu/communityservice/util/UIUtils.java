package com.usiu.communityservice.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.usiu.communityservice.R;

public final class UIUtils {
    private UIUtils() {}

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showSnackbar(View view, String message) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void showErrorDialog(Context context, String title, String message) {
        if (context == null) return;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void styleStatusBadge(TextView textView, String status) {
        if (textView == null || status == null) return;

        textView.setText(status.replace("_", " "));
        Context context = textView.getContext();

        switch (status) {
            case Constants.APP_STATUS_APPROVED:
            case Constants.REG_STATUS_APPROVED:
            case Constants.DIARY_VERIFIED:
            case Constants.REPORT_STATUS_APPROVED:
            case Constants.ELIGIBILITY_APPROVED:
                textView.setTextColor(ContextCompat.getColor(context, R.color.status_approved));
                textView.setBackgroundColor(Color.parseColor("#E8F5E9"));
                break;

            case Constants.APP_STATUS_REJECTED:
            case Constants.REG_STATUS_REJECTED:
            case Constants.REPORT_STATUS_REJECTED:
            case Constants.ELIGIBILITY_REJECTED:
                textView.setTextColor(ContextCompat.getColor(context, R.color.status_rejected));
                textView.setBackgroundColor(Color.parseColor("#FFEBEE"));
                break;

            case Constants.DIARY_FLAGGED:
            case Constants.REPORT_STATUS_CORRECTION_REQUIRED:
            case Constants.ELIGIBILITY_CORRECTION_NEEDED:
                textView.setTextColor(ContextCompat.getColor(context, R.color.status_flagged));
                textView.setBackgroundColor(Color.parseColor("#F3E8FF"));
                break;

            case Constants.APP_STATUS_PENDING:
            case Constants.REG_STATUS_SUBMITTED:
            case Constants.DIARY_PENDING:
            case Constants.REPORT_STATUS_UNDER_REVIEW:
            case Constants.ELIGIBILITY_PENDING:
            default:
                textView.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
                textView.setBackgroundColor(Color.parseColor("#FFFBEB"));
                break;
        }
    }
}

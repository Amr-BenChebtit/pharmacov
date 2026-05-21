package com.wellys.pharmacovigilance.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.CaseStatus;

/**
 * A small rounded pill that displays a CaseStatus with the right background + text color.
 * Drop into any layout, then call setStatus(CaseStatus) from your adapter/fragment.
 */
public class StatusPillView extends MaterialTextView {

    public StatusPillView(@NonNull Context context) {
        super(context);
        init();
    }

    public StatusPillView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusPillView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int padH = dp(12);
        int padV = dp(4);
        setPadding(padH, padV, padH, padV);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        setAllCaps(false);
        // Default state if no status is set
        setStatus(CaseStatus.RECEIVED);
    }

    public void setStatus(@NonNull CaseStatus status) {
        int bgColor;
        int textColor;
        String label;

        switch (status) {
            case RECEIVED:
                bgColor = ContextCompat.getColor(getContext(), R.color.pill_received_bg);
                textColor = ContextCompat.getColor(getContext(), R.color.pill_received_text);
                label = getContext().getString(R.string.status_received);
                break;
            case UNDER_ANALYSIS:
                bgColor = ContextCompat.getColor(getContext(), R.color.pill_analysis_bg);
                textColor = ContextCompat.getColor(getContext(), R.color.pill_analysis_text);
                label = getContext().getString(R.string.status_under_analysis);
                break;
            case INFO_REQUESTED:
                bgColor = ContextCompat.getColor(getContext(), R.color.pill_info_bg);
                textColor = ContextCompat.getColor(getContext(), R.color.pill_info_text);
                label = getContext().getString(R.string.status_info_requested);
                break;
            case VALIDATED:
                bgColor = ContextCompat.getColor(getContext(), R.color.pill_validated_bg);
                textColor = ContextCompat.getColor(getContext(), R.color.pill_validated_text);
                label = getContext().getString(R.string.status_validated);
                break;
            case CLOSED:
            default:
                bgColor = ContextCompat.getColor(getContext(), R.color.pill_closed_bg);
                textColor = ContextCompat.getColor(getContext(), R.color.pill_closed_text);
                label = getContext().getString(R.string.status_closed);
                break;
        }

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(16));
        bg.setColor(ColorStateList.valueOf(bgColor));
        setBackground(bg);

        setTextColor(textColor);
        setText(label);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}

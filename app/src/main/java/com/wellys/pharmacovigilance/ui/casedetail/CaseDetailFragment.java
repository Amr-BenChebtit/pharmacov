package com.wellys.pharmacovigilance.ui.casedetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.CaseStatus;
import com.wellys.pharmacovigilance.data.local.entity.Severity;
import com.wellys.pharmacovigilance.databinding.FragmentCaseDetailBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.DateFormatter;

public class CaseDetailFragment extends Fragment {

    private FragmentCaseDetailBinding binding;
    private CaseDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCaseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long caseId = getArguments() != null ? getArguments().getLong("caseId", -1L) : -1L;

        viewModel = new ViewModelProvider(this, ViewModelFactory.get())
            .get(CaseDetailViewModel.class);
        viewModel.setCaseId(caseId);

        binding.toolbar.setNavigationOnClickListener(v ->
            NavHostFragment.findNavController(this).popBackStack());

        viewModel.getCase().observe(getViewLifecycleOwner(), c -> {
            if (c == null) return;
            render(c);
        });
    }

    private void render(@NonNull CaseEntity c) {
        binding.productTitle.setText(c.productName);
        binding.statusPill.setStatus(c.getStatusEnum());

        setRow(binding.rowInitials.getRoot(), R.string.detail_field_initials, c.patientInitials);
        setRow(binding.rowSex.getRoot(), R.string.detail_field_sex,
            "F".equals(c.patientSex)
                ? getString(R.string.step1_sex_female)
                : getString(R.string.step1_sex_male));
        setRow(binding.rowAge.getRoot(), R.string.detail_field_age,
            c.patientAge + " ans");

        setRow(binding.rowProductName.getRoot(), R.string.detail_field_product_name, c.productName);
        setRow(binding.rowBarcode.getRoot(), R.string.detail_field_barcode,
            c.productBarcode != null && !c.productBarcode.isEmpty()
                ? c.productBarcode
                : getString(R.string.detail_field_not_provided));

        setRow(binding.rowSeverity.getRoot(), R.string.detail_field_severity, severityLabel(c.getSeverityEnum()));
        setRow(binding.rowOnset.getRoot(), R.string.detail_field_onset, DateFormatter.absolute(c.onsetDate));

        binding.description.setText(c.eventDescription);

        buildTimeline(c);
    }

    private void setRow(View row, int labelRes, String value) {
        TextView label = row.findViewById(R.id.label);
        TextView val = row.findViewById(R.id.value);
        label.setText(labelRes);
        val.setText(value);
    }

    private String severityLabel(@NonNull Severity s) {
        switch (s) {
            case SERIOUS: return getString(R.string.severity_serious);
            case LIFE_THREATENING: return getString(R.string.severity_life_threatening);
            case FATAL: return getString(R.string.severity_fatal);
            case MINOR:
            default: return getString(R.string.severity_minor);
        }
    }

    /**
     * Build a simple linear timeline: dots are colored if their stage has been reached,
     * grayed out otherwise. Timestamps are real for RECEIVED and approximated (+5s/+15s)
     * for the next stages since we don't track per-stage history in the DB.
     */
    private void buildTimeline(@NonNull CaseEntity c) {
        binding.timelineContainer.removeAllViews();
        CaseStatus current = c.getStatusEnum();

        addTimelineStep(R.string.status_received,
            DateFormatter.absoluteWithTime(c.submittedAt),
            true);

        boolean reachedAnalysis = current == CaseStatus.UNDER_ANALYSIS
            || current == CaseStatus.INFO_REQUESTED
            || current == CaseStatus.VALIDATED
            || current == CaseStatus.CLOSED;
        addTimelineStep(R.string.status_under_analysis,
            reachedAnalysis ? DateFormatter.absoluteWithTime(c.submittedAt + 5_000) : "",
            reachedAnalysis);

        boolean reachedValidated = current == CaseStatus.VALIDATED
            || current == CaseStatus.CLOSED;
        addTimelineStep(R.string.status_validated,
            reachedValidated ? DateFormatter.absoluteWithTime(c.submittedAt + 15_000) : "",
            reachedValidated);
    }

    private void addTimelineStep(int labelRes, String time, boolean reached) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View row = inflater.inflate(R.layout.item_timeline_step,
            binding.timelineContainer, false);

        View dot = row.findViewById(R.id.timelineDot);
        TextView label = row.findViewById(R.id.timelineLabel);
        TextView timeView = row.findViewById(R.id.timelineTime);

        label.setText(labelRes);
        timeView.setText(time);

        if (!reached) {
            int gray = ContextCompat.getColor(requireContext(), R.color.text_tertiary);
            dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gray));
            label.setTextColor(gray);
            timeView.setVisibility(View.GONE);
        }

        binding.timelineContainer.addView(row,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

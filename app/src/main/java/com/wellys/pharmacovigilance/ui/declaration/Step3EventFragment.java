package com.wellys.pharmacovigilance.ui.declaration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.Severity;
import com.wellys.pharmacovigilance.databinding.FragmentStep3EventBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.DateFormatter;

public class Step3EventFragment extends Fragment {

    private FragmentStep3EventBinding binding;
    private DeclarationViewModel viewModel;
    private long pickedDate;
    private boolean dateChosen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStep3EventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController nav = NavHostFragment.findNavController(this);
        NavBackStackEntry entry = nav.getBackStackEntry(R.id.declaration_graph);
        viewModel = new ViewModelProvider(entry, ViewModelFactory.get())
            .get(DeclarationViewModel.class);

        binding.stepLabel.setText(getString(R.string.declaration_step_label, 3));

        binding.toolbar.setNavigationOnClickListener(v -> nav.popBackStack());

        // Restore
        binding.descriptionInput.setText(viewModel.getDraft().eventDescription);
        switch (viewModel.getDraft().getSeverityEnum()) {
            case SERIOUS: binding.severitySerious.setChecked(true); break;
            case LIFE_THREATENING: binding.severityLifeThreat.setChecked(true); break;
            case FATAL: binding.severityFatal.setChecked(true); break;
            case MINOR:
            default: binding.severityMinor.setChecked(true); break;
        }
        if (viewModel.getDraft().onsetDate > 0
                && viewModel.getDraft().onsetDate != System.currentTimeMillis()) {
            pickedDate = viewModel.getDraft().onsetDate;
            dateChosen = true;
            binding.dateButton.setText(DateFormatter.absolute(pickedDate));
        }

        binding.dateButton.setOnClickListener(v -> showDatePicker());

        binding.backButton.setOnClickListener(v -> nav.popBackStack());

        binding.nextButton.setOnClickListener(v -> {
            if (!validateAndSave()) return;
            nav.navigate(R.id.action_step3_to_step4);
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.step3_onset_label)
            .setSelection(dateChosen ? pickedDate : MaterialDatePicker.todayInUtcMilliseconds())
            .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            pickedDate = selection;
            dateChosen = true;
            binding.dateButton.setText(DateFormatter.absolute(selection));
        });
        picker.show(getParentFragmentManager(), "date_picker");
    }

    private boolean validateAndSave() {
        String desc = binding.descriptionInput.getText() == null
            ? "" : binding.descriptionInput.getText().toString().trim();
        if (desc.length() < 10) {
            binding.descriptionLayout.setError(getString(R.string.step3_error_description));
            return false;
        }
        binding.descriptionLayout.setError(null);

        if (!dateChosen) {
            binding.dateButton.setError(getString(R.string.step3_error_date));
            return false;
        }

        Severity severity;
        int sid = binding.severityGroup.getCheckedRadioButtonId();
        if (sid == R.id.severitySerious) severity = Severity.SERIOUS;
        else if (sid == R.id.severityLifeThreat) severity = Severity.LIFE_THREATENING;
        else if (sid == R.id.severityFatal) severity = Severity.FATAL;
        else severity = Severity.MINOR;

        viewModel.setEvent(desc, severity, pickedDate);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

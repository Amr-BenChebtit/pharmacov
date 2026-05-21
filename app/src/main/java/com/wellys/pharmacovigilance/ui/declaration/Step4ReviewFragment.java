package com.wellys.pharmacovigilance.ui.declaration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.Severity;
import com.wellys.pharmacovigilance.databinding.FragmentStep4ReviewBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.DateFormatter;

public class Step4ReviewFragment extends Fragment {

    private FragmentStep4ReviewBinding binding;
    private DeclarationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStep4ReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController nav = NavHostFragment.findNavController(this);
        NavBackStackEntry entry = nav.getBackStackEntry(R.id.declaration_graph);
        viewModel = new ViewModelProvider(entry, ViewModelFactory.get())
            .get(DeclarationViewModel.class);

        binding.stepLabel.setText(getString(R.string.declaration_step_label, 4));

        binding.toolbar.setNavigationOnClickListener(v -> nav.popBackStack());

        CaseEntity draft = viewModel.getDraft();
        binding.patientSummary.setText(buildPatientSummary(draft));
        binding.productSummary.setText(
            draft.productBarcode != null && !draft.productBarcode.isEmpty()
                ? draft.productName + "\nCode-barres : " + draft.productBarcode
                : draft.productName);
        binding.eventSummary.setText(buildEventSummary(draft));

        // "Modifier" buttons pop back to the right step.
        binding.editPatient.setOnClickListener(v -> nav.popBackStack(R.id.step1PatientFragment, false));
        binding.editProduct.setOnClickListener(v -> nav.popBackStack(R.id.step2ProductFragment, false));
        binding.editEvent.setOnClickListener(v -> nav.popBackStack(R.id.step3EventFragment, false));

        binding.backButton.setOnClickListener(v -> nav.popBackStack());

        binding.submitButton.setOnClickListener(v -> {
            binding.submitButton.setEnabled(false);
            binding.submitButton.setText("…");
            viewModel.submit();
        });

        viewModel.getSubmitted().observe(getViewLifecycleOwner(), submitted -> {
            if (Boolean.TRUE.equals(submitted)) {
                Toast.makeText(requireContext(),
                    R.string.declaration_submitted_toast, Toast.LENGTH_SHORT).show();
                // Pop the entire declaration_graph off the back stack.
                nav.popBackStack(R.id.declaration_graph, true);
            }
        });
    }

    private String buildPatientSummary(CaseEntity c) {
        String sex = "F".equals(c.patientSex)
            ? getString(R.string.step1_sex_female)
            : getString(R.string.step1_sex_male);
        return c.patientInitials + " — " + sex + ", " + c.patientAge + " ans";
    }

    private String buildEventSummary(CaseEntity c) {
        String severity;
        Severity s = c.getSeverityEnum();
        switch (s) {
            case SERIOUS: severity = getString(R.string.severity_serious); break;
            case LIFE_THREATENING: severity = getString(R.string.severity_life_threatening); break;
            case FATAL: severity = getString(R.string.severity_fatal); break;
            case MINOR:
            default: severity = getString(R.string.severity_minor); break;
        }
        return getString(R.string.step3_severity_label) + " : " + severity + "\n"
            + getString(R.string.step3_onset_label) + " : " + DateFormatter.absolute(c.onsetDate) + "\n\n"
            + c.eventDescription;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

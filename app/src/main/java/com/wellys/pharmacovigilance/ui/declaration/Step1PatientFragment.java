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

import com.wellys.pharmacovigilance.PharmacovigilanceApp;
import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.UserEntity;
import com.wellys.pharmacovigilance.databinding.FragmentStep1PatientBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.SessionManager;

public class Step1PatientFragment extends Fragment {

    private FragmentStep1PatientBinding binding;
    private DeclarationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStep1PatientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController nav = NavHostFragment.findNavController(this);
        NavBackStackEntry entry = nav.getBackStackEntry(R.id.declaration_graph);
        viewModel = new ViewModelProvider(entry, ViewModelFactory.get())
            .get(DeclarationViewModel.class);

        // Bind logged-in user id once so step 4 can submit.
        long userId = new SessionManager(requireContext()).getUserId();
        viewModel.setUserId(userId);

        binding.stepLabel.setText(getString(R.string.declaration_step_label, 1));

        binding.toolbar.setNavigationOnClickListener(v -> nav.popBackStack());

        // Restore in case the user came back from step 2.
        binding.initialsInput.setText(viewModel.getDraft().patientInitials);
        if (viewModel.getDraft().patientAge > 0) {
            binding.ageInput.setText(String.valueOf(viewModel.getDraft().patientAge));
        }
        if ("F".equals(viewModel.getDraft().patientSex)) {
            binding.sexFemale.setChecked(true);
        } else {
            binding.sexMale.setChecked(true);
        }
        binding.selfCheckbox.setChecked(viewModel.isPatientSelf());

        binding.selfCheckbox.setOnCheckedChangeListener((b, checked) -> {
            viewModel.setPatientSelf(checked);
            if (checked) {
                fillFromLoggedInUser(userId);
            }
        });

        binding.nextButton.setOnClickListener(v -> {
            if (!validateAndSave()) return;
            nav.navigate(R.id.action_step1_to_step2);
        });
    }

    private void fillFromLoggedInUser(long userId) {
        PharmacovigilanceApp.get().getUserRepository().findById(userId, user -> {
            if (user == null || binding == null) return;
            requireActivity().runOnUiThread(() -> {
                if (binding == null) return;
                binding.initialsInput.setText(initialsOf(user.fullName));
            });
        });
    }

    private String initialsOf(String fullName) {
        if (fullName == null) return "";
        StringBuilder out = new StringBuilder();
        for (String part : fullName.trim().split("\\s+")) {
            if (!part.isEmpty()) out.append(Character.toUpperCase(part.charAt(0))).append('.');
        }
        return out.toString();
    }

    private boolean validateAndSave() {
        String initials = binding.initialsInput.getText() == null
            ? "" : binding.initialsInput.getText().toString().trim();
        if (initials.isEmpty()) {
            binding.initialsLayout.setError(getString(R.string.step1_error_initials));
            return false;
        }
        binding.initialsLayout.setError(null);

        String ageStr = binding.ageInput.getText() == null
            ? "" : binding.ageInput.getText().toString().trim();
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 120) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.ageLayout.setError(getString(R.string.step1_error_age));
            return false;
        }
        binding.ageLayout.setError(null);

        String sex = binding.sexFemale.isChecked() ? "F" : "M";
        viewModel.setPatient(initials, sex, age);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

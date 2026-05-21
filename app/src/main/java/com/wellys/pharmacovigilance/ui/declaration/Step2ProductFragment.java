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
import com.wellys.pharmacovigilance.databinding.FragmentStep2ProductBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;

public class Step2ProductFragment extends Fragment {

    private FragmentStep2ProductBinding binding;
    private DeclarationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStep2ProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController nav = NavHostFragment.findNavController(this);
        NavBackStackEntry entry = nav.getBackStackEntry(R.id.declaration_graph);
        viewModel = new ViewModelProvider(entry, ViewModelFactory.get())
            .get(DeclarationViewModel.class);

        binding.stepLabel.setText(getString(R.string.declaration_step_label, 2));

        binding.toolbar.setNavigationOnClickListener(v -> nav.popBackStack());

        binding.productInput.setText(viewModel.getDraft().productName);

        binding.scanButton.setOnClickListener(v ->
            Toast.makeText(requireContext(),
                R.string.step2_scan_unavailable, Toast.LENGTH_SHORT).show());

        binding.backButton.setOnClickListener(v -> nav.popBackStack());

        binding.nextButton.setOnClickListener(v -> {
            String product = binding.productInput.getText() == null
                ? "" : binding.productInput.getText().toString().trim();
            if (product.isEmpty()) {
                binding.productLayout.setError(getString(R.string.step2_error_product));
                return;
            }
            binding.productLayout.setError(null);
            viewModel.setProduct(product, viewModel.getDraft().productBarcode);
            nav.navigate(R.id.action_step2_to_step3);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

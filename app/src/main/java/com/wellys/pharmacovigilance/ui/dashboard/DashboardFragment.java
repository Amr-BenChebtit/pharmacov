package com.wellys.pharmacovigilance.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.databinding.FragmentDashboardBinding;
import com.wellys.pharmacovigilance.ui.auth.LoginActivity;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.SessionManager;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private CaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());
        long userId = session.getUserId();
        if (userId < 0) {
            // Defensive: should never happen if launcher routes correctly.
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
            return;
        }

        viewModel = new ViewModelProvider(this, ViewModelFactory.get())
            .get(DashboardViewModel.class);
        viewModel.setUserId(userId);

        adapter = new CaseAdapter(c -> {
            Bundle args = new Bundle();
            args.putLong("caseId", c.id);
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_dashboard_to_caseDetail, args);
        });
        binding.casesList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.casesList.setAdapter(adapter);

        binding.fab.setOnClickListener(v ->
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_dashboard_to_declaration));

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            binding.greeting.setText(getString(R.string.dashboard_greeting, user.fullName));
        });

        viewModel.getCases().observe(getViewLifecycleOwner(), cases -> {
            if (cases == null || cases.isEmpty()) {
                binding.casesList.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.casesList.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
                adapter.submitList(cases);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

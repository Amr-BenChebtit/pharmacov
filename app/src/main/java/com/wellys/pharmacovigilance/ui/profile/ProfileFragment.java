package com.wellys.pharmacovigilance.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wellys.pharmacovigilance.PharmacovigilanceApp;
import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.data.local.entity.UserEntity;
import com.wellys.pharmacovigilance.data.local.entity.UserType;
import com.wellys.pharmacovigilance.databinding.FragmentProfileBinding;
import com.wellys.pharmacovigilance.ui.auth.LoginActivity;
import com.wellys.pharmacovigilance.util.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        long userId = session.getUserId();

        PharmacovigilanceApp.get().getUserRepository().observeUser(userId)
            .observe(getViewLifecycleOwner(), this::render);

        binding.logoutButton.setOnClickListener(v -> {
            session.logout();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });
    }

    private void render(@Nullable UserEntity user) {
        if (user == null || binding == null) return;
        binding.profileName.setText(user.fullName);

        setRow(binding.rowEmail.getRoot(), R.string.profile_email, user.email);

        String typeLabel = user.getUserTypeEnum() == UserType.PATIENT
            ? getString(R.string.profile_type_patient)
            : getString(R.string.profile_type_pro);
        setRow(binding.rowType.getRoot(), R.string.profile_type, typeLabel);

        if (user.profession != null && !user.profession.isEmpty()) {
            binding.rowProfession.getRoot().setVisibility(View.VISIBLE);
            setRow(binding.rowProfession.getRoot(), R.string.profile_profession, user.profession);
        } else {
            binding.rowProfession.getRoot().setVisibility(View.GONE);
        }
    }

    private void setRow(View row, int labelRes, String value) {
        TextView label = row.findViewById(R.id.label);
        TextView val = row.findViewById(R.id.value);
        label.setText(labelRes);
        val.setText(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

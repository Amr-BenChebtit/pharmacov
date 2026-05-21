package com.wellys.pharmacovigilance.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.wellys.pharmacovigilance.MainActivity;
import com.wellys.pharmacovigilance.R;
import com.wellys.pharmacovigilance.databinding.ActivityLoginBinding;
import com.wellys.pharmacovigilance.ui.common.ViewModelFactory;
import com.wellys.pharmacovigilance.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        // If already logged in, skip straight to MainActivity.
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        viewModel = new ViewModelProvider(this, ViewModelFactory.get()).get(LoginViewModel.class);

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailInput.getText() == null
                ? "" : binding.emailInput.getText().toString();
            String password = binding.passwordInput.getText() == null
                ? "" : binding.passwordInput.getText().toString();
            viewModel.login(email, password);
        });

        binding.emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) viewModel.clearError();
        });
        binding.passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) viewModel.clearError();
        });

        viewModel.getState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    binding.loginButton.setEnabled(false);
                    binding.loginButton.setText("…");
                    break;
                case INVALID_INPUT:
                    resetButton();
                    Snackbar.make(binding.getRoot(),
                        R.string.login_error_empty, Snackbar.LENGTH_SHORT).show();
                    break;
                case WRONG_CREDENTIALS:
                    resetButton();
                    Snackbar.make(binding.getRoot(),
                        R.string.login_error_wrong, Snackbar.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    // The user comes through getLoggedInUser() observer below.
                    break;
                case IDLE:
                default:
                    resetButton();
                    break;
            }
        });

        viewModel.getLoggedInUser().observe(this, user -> {
            if (user == null) return;
            session.setUserId(user.id);
            goToMain();
        });
    }

    private void resetButton() {
        binding.loginButton.setEnabled(true);
        binding.loginButton.setText(R.string.login_button);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

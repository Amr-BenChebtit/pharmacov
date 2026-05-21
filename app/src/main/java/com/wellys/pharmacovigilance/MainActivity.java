package com.wellys.pharmacovigilance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.wellys.pharmacovigilance.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.navHostFragment);
        if (host == null) {
            throw new IllegalStateException("NavHostFragment not found");
        }
        NavController navController = host.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // Hide the bottom nav on detail / wizard screens for a cleaner full-screen feel.
        navController.addOnDestinationChangedListener((c, destination, args) -> {
            int id = destination.getId();
            boolean show = id == R.id.dashboardFragment
                || id == R.id.profileFragment;
            binding.bottomNav.setVisibility(show ? android.view.View.VISIBLE
                                                 : android.view.View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

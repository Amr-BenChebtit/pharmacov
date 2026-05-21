package com.wellys.pharmacovigilance.ui.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wellys.pharmacovigilance.PharmacovigilanceApp;
import com.wellys.pharmacovigilance.data.repository.CaseRepository;
import com.wellys.pharmacovigilance.data.repository.UserRepository;
import com.wellys.pharmacovigilance.ui.auth.LoginViewModel;
import com.wellys.pharmacovigilance.ui.casedetail.CaseDetailViewModel;
import com.wellys.pharmacovigilance.ui.dashboard.DashboardViewModel;
import com.wellys.pharmacovigilance.ui.declaration.DeclarationViewModel;

/**
 * Manual factory — Hilt would do this for us, but we're avoiding DI in the demo.
 * Pulls repositories from the Application singleton and wires them into each ViewModel.
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory INSTANCE;

    public static ViewModelFactory get() {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) INSTANCE = new ViewModelFactory();
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory() {}

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        PharmacovigilanceApp app = PharmacovigilanceApp.get();
        UserRepository userRepo = app.getUserRepository();
        CaseRepository caseRepo = app.getCaseRepository();

        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepo);
        }
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(userRepo, caseRepo);
        }
        if (modelClass.isAssignableFrom(DeclarationViewModel.class)) {
            return (T) new DeclarationViewModel(caseRepo);
        }
        if (modelClass.isAssignableFrom(CaseDetailViewModel.class)) {
            return (T) new CaseDetailViewModel(caseRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

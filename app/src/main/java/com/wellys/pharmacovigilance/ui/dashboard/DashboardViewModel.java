package com.wellys.pharmacovigilance.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.UserEntity;
import com.wellys.pharmacovigilance.data.repository.CaseRepository;
import com.wellys.pharmacovigilance.data.repository.UserRepository;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final UserRepository userRepo;
    private final CaseRepository caseRepo;

    private final MutableLiveData<Long> userId = new MutableLiveData<>();
    private final LiveData<UserEntity> user;
    private final LiveData<List<CaseEntity>> cases;

    public DashboardViewModel(@NonNull UserRepository userRepo, @NonNull CaseRepository caseRepo) {
        this.userRepo = userRepo;
        this.caseRepo = caseRepo;

        // Switch the observed user / cases whenever the userId changes.
        user = Transformations.switchMap(userId, id ->
            id == null || id < 0 ? new MutableLiveData<>(null) : userRepo.observeUser(id));

        cases = Transformations.switchMap(userId, id ->
            id == null || id < 0 ? new MutableLiveData<>(null) : caseRepo.observeCasesForUser(id));
    }

    public void setUserId(long id) {
        if (userId.getValue() == null || userId.getValue() != id) {
            userId.setValue(id);
        }
    }

    public LiveData<UserEntity> getUser() { return user; }
    public LiveData<List<CaseEntity>> getCases() { return cases; }
}

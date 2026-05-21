package com.wellys.pharmacovigilance.ui.casedetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.repository.CaseRepository;

public class CaseDetailViewModel extends ViewModel {

    private final CaseRepository caseRepo;
    private final MutableLiveData<Long> caseId = new MutableLiveData<>();
    private final LiveData<CaseEntity> caseLive;

    public CaseDetailViewModel(@NonNull CaseRepository caseRepo) {
        this.caseRepo = caseRepo;
        caseLive = Transformations.switchMap(caseId, id ->
            id == null || id < 0 ? new MutableLiveData<>(null) : caseRepo.observeById(id));
    }

    public void setCaseId(long id) {
        if (caseId.getValue() == null || caseId.getValue() != id) {
            caseId.setValue(id);
        }
    }

    public LiveData<CaseEntity> getCase() { return caseLive; }
}

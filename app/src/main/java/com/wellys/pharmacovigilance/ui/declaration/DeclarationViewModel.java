package com.wellys.pharmacovigilance.ui.declaration;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.CaseStatus;
import com.wellys.pharmacovigilance.data.local.entity.Severity;
import com.wellys.pharmacovigilance.data.repository.CaseRepository;

/**
 * Lives across all 4 wizard steps by being scoped to the nested nav graph in the fragments.
 * Holds the in-progress CaseEntity in memory; commits it via repository on the final step.
 */
public class DeclarationViewModel extends ViewModel {

    private final CaseRepository caseRepo;
    private final CaseEntity draft = new CaseEntity();
    private final MutableLiveData<Boolean> submitted = new MutableLiveData<>(false);
    private boolean isPatientSelf = false;

    public DeclarationViewModel(@NonNull CaseRepository caseRepo) {
        this.caseRepo = caseRepo;
        // Default values
        draft.status = CaseStatus.RECEIVED.name();
        draft.severity = Severity.MINOR.name();
        draft.patientSex = "M";
        draft.onsetDate = System.currentTimeMillis();
    }

    public CaseEntity getDraft() { return draft; }

    public LiveData<Boolean> getSubmitted() { return submitted; }

    // ---------- Step 1 ----------
    public void setPatient(@NonNull String initials, @NonNull String sex, int age) {
        draft.patientInitials = initials;
        draft.patientSex = sex;
        draft.patientAge = age;
    }

    public void setPatientSelf(boolean self) { this.isPatientSelf = self; }
    public boolean isPatientSelf() { return isPatientSelf; }

    // ---------- Step 2 ----------
    public void setProduct(@NonNull String name, String barcode) {
        draft.productName = name;
        draft.productBarcode = barcode;
    }

    // ---------- Step 3 ----------
    public void setEvent(@NonNull String description, @NonNull Severity severity, long onsetDate) {
        draft.eventDescription = description;
        draft.severity = severity.name();
        draft.onsetDate = onsetDate;
    }

    // ---------- Step 4 ----------
    public void setUserId(long userId) { draft.userId = userId; }

    public void submit() {
        // Defensive — guarantee status starts at RECEIVED; the repository sets timestamp.
        draft.status = CaseStatus.RECEIVED.name();
        caseRepo.submitCase(draft, caseId -> submitted.postValue(true));
    }
}

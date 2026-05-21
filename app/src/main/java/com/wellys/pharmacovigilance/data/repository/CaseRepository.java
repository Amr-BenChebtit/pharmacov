package com.wellys.pharmacovigilance.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.wellys.pharmacovigilance.data.local.CaseDao;
import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.CaseStatus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaseRepository {

    private final CaseDao caseDao;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public CaseRepository(CaseDao caseDao) {
        this.caseDao = caseDao;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<CaseEntity>> observeCasesForUser(long userId) {
        return caseDao.observeCasesForUser(userId);
    }

    public LiveData<CaseEntity> observeById(long id) {
        return caseDao.observeById(id);
    }

    public interface OnSubmitted {
        void onDone(long caseId);
    }

    public void submitCase(CaseEntity c, OnSubmitted callback) {
        c.status = CaseStatus.RECEIVED.name();
        c.submittedAt = System.currentTimeMillis();

        executor.execute(() -> {
            long id = caseDao.insert(c);

            if (callback != null) {
                mainHandler.post(() -> callback.onDone(id));
            }

            // Fake the pharmacovigilance team's workflow for demo realism.
            mainHandler.postDelayed(
                () -> executor.execute(
                    () -> caseDao.updateStatus(id, CaseStatus.UNDER_ANALYSIS.name())),
                5_000);

            mainHandler.postDelayed(
                () -> executor.execute(
                    () -> caseDao.updateStatus(id, CaseStatus.VALIDATED.name())),
                15_000);
        });
    }

    public void forceStatus(long caseId, CaseStatus newStatus) {
        executor.execute(() -> caseDao.updateStatus(caseId, newStatus.name()));
    }
}

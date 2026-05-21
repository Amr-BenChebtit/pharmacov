package com.wellys.pharmacovigilance.util;

import com.wellys.pharmacovigilance.data.local.AppDatabase;
import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import com.wellys.pharmacovigilance.data.local.entity.CaseStatus;
import com.wellys.pharmacovigilance.data.local.entity.Severity;
import com.wellys.pharmacovigilance.data.local.entity.UserEntity;
import com.wellys.pharmacovigilance.data.local.entity.UserType;

import java.util.concurrent.Executors;

public class SeedData {

    public static void seedIfEmpty(AppDatabase db) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (db.userDao().count() > 0) return;

            long patientId = db.userDao().insert(new UserEntity(
                "patient@demo.com", "demo", "Sara Bennani",
                UserType.PATIENT, null
            ));

            db.userDao().insert(new UserEntity(
                "doc@demo.com", "demo", "Dr. Yassine El Amrani",
                UserType.HEALTHCARE_PRO, "Médecin"
            ));

            long now = System.currentTimeMillis();
            long day = 24L * 60 * 60 * 1000;

            CaseEntity c1 = new CaseEntity();
            c1.userId = patientId;
            c1.submittedAt = now - 3 * day;
            c1.patientInitials = "S.B.";
            c1.patientSex = "F";
            c1.patientAge = 34;
            c1.productName = "Paracétamol 500mg";
            c1.eventDescription = "Éruption cutanée 2h après la prise.";
            c1.severity = Severity.MINOR.name();
            c1.onsetDate = now - 3 * day;
            c1.status = CaseStatus.VALIDATED.name();
            db.caseDao().insert(c1);

            CaseEntity c2 = new CaseEntity();
            c2.userId = patientId;
            c2.submittedAt = now - day;
            c2.patientInitials = "S.B.";
            c2.patientSex = "F";
            c2.patientAge = 34;
            c2.productName = "Amoxicilline 1g";
            c2.eventDescription = "Nausées et vertiges importants.";
            c2.severity = Severity.SERIOUS.name();
            c2.onsetDate = now - day;
            c2.status = CaseStatus.UNDER_ANALYSIS.name();
            db.caseDao().insert(c2);
        });
    }
}

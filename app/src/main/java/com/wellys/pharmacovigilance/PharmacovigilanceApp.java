package com.wellys.pharmacovigilance;

import android.app.Application;

import com.wellys.pharmacovigilance.data.local.AppDatabase;
import com.wellys.pharmacovigilance.data.repository.CaseRepository;
import com.wellys.pharmacovigilance.data.repository.UserRepository;
import com.wellys.pharmacovigilance.util.SeedData;

public class PharmacovigilanceApp extends Application {

    private static PharmacovigilanceApp instance;

    private AppDatabase database;
    private UserRepository userRepository;
    private CaseRepository caseRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = AppDatabase.get(this);
        userRepository = new UserRepository(database.userDao());
        caseRepository = new CaseRepository(database.caseDao());

        SeedData.seedIfEmpty(database);
    }

    public static PharmacovigilanceApp get() { return instance; }

    public AppDatabase getDatabase() { return database; }
    public UserRepository getUserRepository() { return userRepository; }
    public CaseRepository getCaseRepository() { return caseRepository; }
}

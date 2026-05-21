package com.wellys.pharmacovigilance.data.repository;

import androidx.lifecycle.LiveData;

import com.wellys.pharmacovigilance.data.local.UserDao;
import com.wellys.pharmacovigilance.data.local.entity.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executor;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public interface OnLogin {
        void onResult(UserEntity user);
    }

    public void login(String email, String password, OnLogin callback) {
        executor.execute(() -> callback.onResult(userDao.login(email, password)));
    }

    public LiveData<UserEntity> observeUser(long id) {
        return userDao.observeById(id);
    }

    public void findById(long id, OnLogin callback) {
        executor.execute(() -> callback.onResult(userDao.findById(id)));
    }
}

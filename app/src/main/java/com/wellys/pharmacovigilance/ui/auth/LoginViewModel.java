package com.wellys.pharmacovigilance.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wellys.pharmacovigilance.data.local.entity.UserEntity;
import com.wellys.pharmacovigilance.data.repository.UserRepository;

public class LoginViewModel extends ViewModel {

    public enum State { IDLE, LOADING, SUCCESS, INVALID_INPUT, WRONG_CREDENTIALS }

    private final UserRepository userRepo;
    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);
    private final MutableLiveData<UserEntity> loggedInUser = new MutableLiveData<>();

    public LoginViewModel(@NonNull UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public LiveData<State> getState() { return state; }
    public LiveData<UserEntity> getLoggedInUser() { return loggedInUser; }

    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {
            state.setValue(State.INVALID_INPUT);
            return;
        }
        state.setValue(State.LOADING);
        userRepo.login(email.trim(), password, user -> {
            // Callback runs on a background thread → use postValue, not setValue.
            if (user == null) {
                state.postValue(State.WRONG_CREDENTIALS);
            } else {
                loggedInUser.postValue(user);
                state.postValue(State.SUCCESS);
            }
        });
    }

    /** Reset to IDLE so re-typing after an error doesn't keep flashing the snackbar. */
    public void clearError() {
        State current = state.getValue();
        if (current == State.WRONG_CREDENTIALS || current == State.INVALID_INPUT) {
            state.setValue(State.IDLE);
        }
    }
}

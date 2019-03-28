package com.samuelchowi.intercorpretail.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModel extends ViewModel {

    MutableLiveData<String> phoneNumberData = new MutableLiveData<>();
    MutableLiveData<Boolean> sendingSMSData = new MutableLiveData<>();
    MutableLiveData<String> verificationIdData = new MutableLiveData<>();

    public static class LoginViewModelFactory implements ViewModelProvider.Factory {


        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return null;
        }
    }
}

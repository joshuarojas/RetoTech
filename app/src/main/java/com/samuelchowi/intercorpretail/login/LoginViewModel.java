package com.samuelchowi.intercorpretail.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Sending Data Values:
 *      - 0 non started process
 *      - 1 sending code to phone number
 *      - 2 waiting for the code
 *      - 3 verifying code
 */
public class LoginViewModel extends ViewModel {

    MutableLiveData<String> phoneNumberData = new MutableLiveData<>();
    MutableLiveData<Integer> sendingSMSData = new MutableLiveData<>();
    MutableLiveData<String> verificationIdData = new MutableLiveData<>();
}

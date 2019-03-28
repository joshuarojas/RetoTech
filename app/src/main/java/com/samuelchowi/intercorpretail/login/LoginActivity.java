package com.samuelchowi.intercorpretail.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.samuelchowi.intercorpretail.BaseActivity;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.common.PreferenceUtils;
import com.samuelchowi.intercorpretail.databinding.ActivityLoginBinding;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verifyCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            loginViewModel.sendingSMSData.postValue(false);
            binding.edtCode.setText(phoneAuthCredential.getSmsCode());
//            signInWithPhoneNumber(phoneAuthCredential);
//            hideLoading(R.string.login_verify_number);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, e.getMessage(), e);
            binding.tvwReSend.setVisibility(View.VISIBLE);
            loginViewModel.sendingSMSData.postValue(false);
            showAlert(getString(R.string.login_couldnt_send_message));
            hideLoading(R.string.login_login);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            loginViewModel.sendingSMSData.postValue(false);
            loginViewModel.verificationIdData.postValue(verificationId);

            binding.btnLogin.setVisibility(View.GONE);
            binding.edtPhone.setVisibility(View.GONE);
            binding.ivwContact.setVisibility(View.GONE);

            binding.btnVerify.setVisibility(View.VISIBLE);
            binding.edtCode.setVisibility(View.VISIBLE);
            hideLoading(R.string.login_verify_number);
        }
    };

    public static Intent intent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.phoneNumberData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                PreferenceUtils.instance(LoginActivity.this).setPhoneNumber(value);
            }
        });
        loginViewModel.sendingSMSData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean state) {
                if (state) showLoading();
            }
        });

        FirebaseAuth.getInstance().setLanguageCode("es");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.edtPhone.setText(PreferenceUtils.instance(this).getPhoneNumber());
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.phoneNumberData.postValue(s.toString());
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable phoneNumber = binding.edtPhone.getText();
                if (phoneNumber != null && phoneNumber.toString().trim().length() < 9) {
                    showAlert(getString(R.string.login_uncompleted_number));
                } else if (phoneNumber != null) {
                    showLoading();
                    binding.tvwReSend.setOnClickListener(this);
                    loginViewModel.sendingSMSData.setValue(true);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            String.format("+51%s", phoneNumber.toString()),
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            verifyCallback);
                }
            }
        });
        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable code = binding.edtCode.getText();
                if (code != null && code.toString().trim().length() < 6) {
                    showAlert(getString(R.string.login_uncompleted_code));
                } else if (code != null) {
                    String verificationID = loginViewModel.verificationIdData.getValue();
                    if (verificationID != null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code.toString());
                        signInWithPhoneNumber(credential);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (loginViewModel.sendingSMSData.getValue() == Boolean.TRUE) showLoading();
    }

    private void showLoading() {
        binding.btnLogin.setText("");
        binding.btnVerify.setText("");
        binding.lteAnim.setVisibility(View.VISIBLE);
        binding.lteAnim.setRepeatCount(LottieDrawable.INFINITE);
        binding.lteAnim.playAnimation();
    }

    private void hideLoading(int btnMessage) {
        binding.btnLogin.setText(btnMessage);
        binding.btnVerify.setText(btnMessage);
        binding.lteAnim.setVisibility(View.GONE);
        binding.lteAnim.cancelAnimation();
    }

    private void signInWithPhoneNumber(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();

                        } else {
                            binding.edtPhone.setVisibility(View.VISIBLE);
                            binding.ivwContact.setVisibility(View.VISIBLE);

                            binding.edtCode.setVisibility(View.GONE);
                            hideLoading(R.string.login_login);
                        }
                    }
                });

    }
}

package com.samuelchowi.intercorpretail.login;

import android.app.ActivityOptions;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.samuelchowi.intercorpretail.BaseActivity;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.common.PreferenceUtils;
import com.samuelchowi.intercorpretail.databinding.ActivityLoginBinding;
import com.samuelchowi.intercorpretail.register.RegisterActivity;

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
            loginViewModel.sendingSMSData.postValue(2);
            binding.edtCode.setText(phoneAuthCredential.getSmsCode());
            signInWithPhoneNumber(phoneAuthCredential);
            showVerifyLoading();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, e.getMessage(), e);
            binding.tvwReSend.setVisibility(View.VISIBLE);
            loginViewModel.sendingSMSData.postValue(0);
            showAlert(getString(R.string.login_couldnt_send_message));
            hideLoginLoading();
            showLogin();
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            loginViewModel.sendingSMSData.postValue(0);
            loginViewModel.verificationIdData.postValue(verificationId);

            hideLoginLoading();
            showVerify();
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
                    showLoginLoading();
                    binding.tvwReSend.setOnClickListener(this);
                    loginViewModel.sendingSMSData.setValue(1);
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
                        showVerifyLoading();
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
        Integer sendingData = loginViewModel.sendingSMSData.getValue();
        if (sendingData != null && sendingData == 1) {
            showLogin();
            showLoginLoading();
        } else if (sendingData != null && sendingData == 2) {
            hideLoginLoading();
            showVerify();
        } else if (sendingData != null && sendingData == 3) {
            showVerify();
            showVerifyLoading();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.ctlVerify.getVisibility() == View.VISIBLE)
            showLogin();
        else
            super.onBackPressed();
    }

    private void showLoginLoading() {
        binding.btnLogin.setText("");
        binding.lteAnimLogin.setVisibility(View.VISIBLE);
        binding.lteAnimLogin.setRepeatCount(LottieDrawable.INFINITE);
        binding.lteAnimLogin.playAnimation();
    }

    private void hideLoginLoading() {
        binding.btnLogin.setText(R.string.login_login);
        binding.viwOverlay.setVisibility(View.GONE);
        binding.lteAnimLogin.setVisibility(View.GONE);
        binding.lteAnimLogin.cancelAnimation();
    }

    private void showLogin() {
        binding.ctlLogin.setVisibility(View.VISIBLE);
        binding.ctlVerify.setVisibility(View.GONE);
    }

    private void showVerify() {
        binding.ctlVerify.setVisibility(View.VISIBLE);
        binding.ctlLogin.setVisibility(View.GONE);
    }

    private void showVerifyLoading() {
        binding.btnVerify.setText("");
        binding.viwOverlay.setVisibility(View.VISIBLE);
        binding.lteAnim.setVisibility(View.VISIBLE);
        binding.lteAnim.setRepeatCount(LottieDrawable.INFINITE);
        binding.lteAnim.playAnimation();
    }

    private void hideVerifyLoading() {
        binding.btnVerify.setText(R.string.login_verify_number);
        binding.viwOverlay.setVisibility(View.GONE);
        binding.lteAnim.setVisibility(View.GONE);
        binding.lteAnim.cancelAnimation();
    }

    private void signInWithPhoneNumber(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            AuthResult result = task.getResult();
                            if (result != null) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, binding.ivwSplash, "icpLogo");
                                startActivity(RegisterActivity.intent(LoginActivity.this, result.getUser().getUid()), options.toBundle());
                                hideVerifyLoading();
                            } else {
                                hideLoginLoading();
                                showLogin();
                            }
                        } else {
                            binding.edtPhone.setVisibility(View.VISIBLE);
                            binding.ivwContact.setVisibility(View.VISIBLE);

                            binding.edtCode.setVisibility(View.GONE);
                            hideVerifyLoading();
                            showVerify();
                        }
                    }
                });
    }
}

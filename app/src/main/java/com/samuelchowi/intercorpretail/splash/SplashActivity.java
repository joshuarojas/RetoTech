package com.samuelchowi.intercorpretail.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.samuelchowi.intercorpretail.BaseActivity;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.databinding.ActivitySplashBinding;
import com.samuelchowi.intercorpretail.login.LoginActivity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        binding.lteAnim.setRepeatCount(2);
        binding.lteAnim.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                goToLogin();

                // I hide the loading to avoid to have the view freezed on the screen, it's inside a
                // Handler to avoid and ugly transition between the shared element animation and the reposition
                // of the imageView with the logo of Intercorp.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.lteAnim.setVisibility(View.GONE);
                    }
                }, 400);
            }
        });
    }

    private void goToLogin() {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, binding.ivwSplash, "icpLogo");
        startActivity(LoginActivity.intent(SplashActivity.this), options.toBundle());
        supportFinishAfterTransition();
    }
}

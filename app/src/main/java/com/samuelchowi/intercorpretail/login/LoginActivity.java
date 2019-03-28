package com.samuelchowi.intercorpretail.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.databinding.ActivityLoginBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    public static Intent intent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}

package com.samuelchowi.intercorpretail.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.databinding.ActivityMainBinding;
import com.samuelchowi.intercorpretail.login.LoginActivity;
import com.samuelchowi.intercorpretail.splash.SplashActivity;

public class MainActivity extends AppCompatActivity {

    private static final String NAME_KEY = "name_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String name = "Default";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            name = bundle.getString(NAME_KEY);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.tvwName.setText(name);
        binding.tvwClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.intent(MainActivity.this));
                finish();
            }
        });
    }

    public static Intent intent(Context context, String name) {
        return new Intent(context, MainActivity.class).putExtra(NAME_KEY, name);
    }
}

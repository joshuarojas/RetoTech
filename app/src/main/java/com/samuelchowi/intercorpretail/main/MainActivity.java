package com.samuelchowi.intercorpretail.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.databinding.ActivityMainBinding;
import com.samuelchowi.intercorpretail.login.LoginActivity;
import com.samuelchowi.intercorpretail.register.UserModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.tvwClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null)
                        binding.tvwName.setText(String.format("%s %s", user.getName(), user.getLastName()));
                    else
                        goToLogin();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    goToLogin();
                }
            });
        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.intent(MainActivity.this));
        finish();
    }

    public static Intent intent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}

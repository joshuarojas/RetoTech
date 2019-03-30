package com.samuelchowi.intercorpretail.register;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.samuelchowi.intercorpretail.BaseActivity;
import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.databinding.ActivityRegisterBinding;
import com.samuelchowi.intercorpretail.main.MainActivity;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class RegisterActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private static final String UID_KEY = "uid_key";

    private RegisterViewModel viewModel;
    private ActivityRegisterBinding binding;
    private FirebaseDatabase database;

    public static Intent intent(Context context, String uid) {
        return new Intent(context, RegisterActivity.class).putExtra(UID_KEY, uid);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        viewModel.userData.setValue(new UserModel());

        database = FirebaseDatabase.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatePicker();
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBlankInput(binding.edtName) || checkBlankInput(binding.edtLastName) ||
                        checkBlankInput(binding.edtAge) || checkBlankInput(binding.edtBirthDate)) {
                    showAlert(getString(R.string.register_uncompleted));
                } else {
                    String uid = "default-01";
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null)
                        uid = bundle.getString(UID_KEY, "default-01");

                    binding.btnRegister.setText("");
                    binding.viwOverlay.setVisibility(View.VISIBLE);
                    binding.lteAnim.setVisibility(View.VISIBLE);
                    binding.lteAnim.setRepeatCount(LottieDrawable.INFINITE);
                    binding.lteAnim.playAnimation();

                    UserModel user = viewModel.userData.getValue();
                    if (user == null) user = new UserModel();
                    user.setName(binding.edtName.getText().toString());
                    user.setLastName(binding.edtLastName.getText().toString());
                    user.setAge(Integer.parseInt(binding.edtAge.getText().toString()));
                    user.setDate(binding.edtBirthDate.getText().toString());
                    viewModel.userData.setValue(user);

                    database.getReference().child("users").child(uid).setValue(viewModel.userData.getValue())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(MainActivity.intent(RegisterActivity.this, binding.edtName.getText().toString()));
                                    } else {
                                        showAlert(getString(R.string.register_error));
                                        binding.btnRegister.setText(R.string.register_register);
                                        binding.viwOverlay.setVisibility(View.GONE);
                                        binding.lteAnim.setVisibility(View.GONE);
                                        binding.lteAnim.cancelAnimation();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format(Locale.US, "%02d/%02d/%02d", dayOfMonth, month, year);
        binding.edtBirthDate.setText(date);

        UserModel user = viewModel.userData.getValue();
        if (user == null) user = new UserModel();
        user.setDate(date);
        viewModel.userData.setValue(user);
    }

    private void createDatePicker() {
        Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private boolean checkBlankInput(EditText input) {
        boolean isEmpty = input.getText() == null || input.getText().toString().trim().length() == 0;
        if (isEmpty)
            input.setError(getString(R.string.register_uncompleted_field));
        return isEmpty;
    }

}

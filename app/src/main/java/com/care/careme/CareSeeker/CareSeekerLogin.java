package com.care.careme.CareSeeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.care.careme.R;
import com.care.careme.verifyOtp;

import java.util.concurrent.TimeUnit;

public class CareSeekerLogin extends AppCompatActivity {

    private EditText enterPhone;
    private Button getOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_careseeker_login);

        enterPhone = findViewById(R.id.inputPhone);
        getOtpButton = findViewById(R.id.buttonGetOtp);
        ProgressBar progressBar =  findViewById(R.id.progressBarSendingOtp);

        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!enterPhone.getText().toString().trim().isEmpty()) {
                    if ((enterPhone.getText().toString().trim()).length() == 9) {

                        progressBar.setVisibility(View.VISIBLE);
                        getOtpButton.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+254" + enterPhone.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                CareSeekerLogin.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        getOtpButton.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        getOtpButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(CareSeekerLogin.this,"Please check Internet Connection",Toast.LENGTH_LONG).show();
                                    }


                                    @Override
                                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar.setVisibility(View.GONE);
                                        getOtpButton.setVisibility(View.VISIBLE);

                                        Intent intent = new Intent(getApplicationContext(), verifyOtp.class);
                                        intent.putExtra("mobile", enterPhone.getText().toString());
                                        intent.putExtra("backendotp", backendotp);
                                        startActivity(intent);
                                    }
                                }
                        );

                    } else {
                        Toast.makeText(CareSeekerLogin.this, "Please enter correct number", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(CareSeekerLogin.this,"Enter mobile number",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
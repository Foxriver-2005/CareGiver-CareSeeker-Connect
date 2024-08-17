package com.care.careme.CareGiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Admin.Admin_Activity;
import com.care.careme.ForgotPassword;
import com.care.careme.Helpers.CareGiver_Session_Mangement;
import com.care.careme.Models.CareGiver_Email_Id;
import com.care.careme.R;

public class CareGiverLogin extends AppCompatActivity implements View.OnClickListener {


    private TextView register, forgotPassword;
    private EditText editTextEmailMain, editTextPasswordMain;

    private Button signIn;
    private int flag = 0;
    private FirebaseAuth myAuth;
    private String encoded_email;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_login);

        signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmailMain = (EditText) findViewById(R.id.emailMain);
        editTextPasswordMain = (EditText) findViewById(R.id.passwordMain);
        progressBar = (ProgressBar) findViewById(R.id.progressbarlogin);

        myAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPasswordText);
        forgotPassword.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Users");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.loginButton) {
            userLogin();
        }
        else if (id == R.id.forgotPasswordText) {
            startActivity(new Intent(this, ForgotPassword.class));
        }

    }

    private void userLogin() {
        String emailMain = editTextEmailMain.getText().toString().trim();
        String passwordMain = editTextPasswordMain.getText().toString().trim();

        // Check if admin credentials are provided
        if (emailMain.equals("admin@gmail.com") && passwordMain.equals("12345678")) {
            startActivity(new Intent(CareGiverLogin.this, Admin_Activity.class));
            return;
        }

        // Proceed with Firebase authentication for non-admin users
        encoded_email = EncodeString(emailMain);
        if (emailMain.isEmpty()) {
            editTextEmailMain.setError("Email is a required field !");
            editTextEmailMain.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailMain).matches()) {
            editTextEmailMain.setError("Please provide Valid Email !");
            editTextEmailMain.requestFocus();
            return;
        }
        if (passwordMain.isEmpty()) {
            editTextPasswordMain.setError("Password is a required field !");
            editTextPasswordMain.requestFocus();
            return;
        }
        if (passwordMain.length() < 6) {
            editTextPasswordMain.setError("Minimum length of password should be 6 characters !");
            editTextPasswordMain.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        myAuth.signInWithEmailAndPassword(emailMain, passwordMain).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        databaseReference.child(encoded_email).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String usertype = dataSnapshot.child("user_type").getValue(String.class);
                                    if (usertype.equals("CareGiver")) {
                                        CareGiver_Email_Id doctor_email_id = new CareGiver_Email_Id(emailMain, "CareGiver");
                                        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(CareGiverLogin.this);
                                        doctors_session_mangement.saveDoctorSession(doctor_email_id);
                                        startActivity(new Intent(CareGiverLogin.this, CareGiver.class));
                                    } else {
                                        CareGiver_Email_Id doctor_email_id = new CareGiver_Email_Id(emailMain, "Admin");
                                        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(CareGiverLogin.this);
                                        doctors_session_mangement.saveDoctorSession(doctor_email_id);
                                        startActivity(new Intent(CareGiverLogin.this, Admin_Activity.class));
                                    }
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else if (flag == 0) {
                        showChangePasswordDialog();
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(CareGiverLogin.this, "Check your Email to verify your account", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CareGiverLogin.this, "Failed to Login ! Please check your credentials", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(CareGiverLogin.this).inflate(R.layout.update_password, null);
        final EditText passwordold = view.findViewById(R.id.passwordEt);
        final EditText passwordnew = view.findViewById(R.id.newpasswordEt);
        Button updatepass = view.findViewById(R.id.updatePasswordBtn);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CareGiverLogin.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        updatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpass = passwordold.getText().toString().trim();
                String newpass = passwordnew.getText().toString().trim();
                if (TextUtils.isEmpty(oldpass)) {
                    Toast.makeText(CareGiverLogin.this, "Enter Your Current Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newpass.length() < 6) {
                    Toast.makeText(CareGiverLogin.this, "Password Length must have at least 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                flag = 1;
                updatePassword(oldpass, newpass);
            }
        });
    }

    private void updatePassword(String oldpass, String newpass) {
        FirebaseUser user = myAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldpass);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newpass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CareGiverLogin.this, "Password Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CareGiverLogin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CareGiverLogin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

}
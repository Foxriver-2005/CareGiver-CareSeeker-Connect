package com.care.careme.CareSeeker;

import static com.care.careme.Mpesa.Constants.BUSINESS_SHORT_CODE;
import static com.care.careme.Mpesa.Constants.CALLBACKURL;
import static com.care.careme.Mpesa.Constants.PARTYB;
import static com.care.careme.Mpesa.Constants.PASSKEY;
import static com.care.careme.Mpesa.Constants.TRANSACTION_TYPE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Helpers.CareSeeker_Session_Management;
import com.care.careme.Models.Admin_Payment_Class;
import com.care.careme.Models.Booking_Appointments;
import com.care.careme.Models.CareSeeker_Chosen_Slot_Class;
import com.care.careme.Mpesa.AccessToken;
import com.care.careme.Mpesa.DarajaApiClient;
import com.care.careme.Mpesa.STKPush;
import com.care.careme.Mpesa.Utils;
import com.care.careme.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CareSeeker_Payment_Appt extends AppCompatActivity {

    private String check, date_val, fees, phone, email, chosen_time, question_data, pname, slot_val, fees_val;
    private TextView fees_show;
    private EditText tid;
    private DatabaseReference reference_user, reference_doctor, reference_booking, reference_patient, reference_details, reference_doctor_appt, reference_payment;
    private Button pay_app;
    private TextView paymentLink;
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private boolean isAuthTokenReceived = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        reference_doctor = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Data");
        reference_doctor_appt = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Appointments");
        reference_booking = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Chosen_Slots");
        reference_patient = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareSeeker_Chosen_Slots");
        reference_details = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareSeeker_Details");
        reference_payment = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Admin_Payment");

        CareSeeker_Session_Management session = new CareSeeker_Session_Management(CareSeeker_Payment_Appt.this);
        phone = session.getSession();
        pname = getIntent().getStringExtra("pname");
        email = getIntent().getStringExtra("email");
        chosen_time = getIntent().getStringExtra("chosen_time");
        question_data = getIntent().getStringExtra("question");
        slot_val = getIntent().getStringExtra("slot_val");
        date_val = getIntent().getStringExtra("date");
        fees_val = getIntent().getStringExtra("fees");
        check = getIntent().getStringExtra("check");

        tid = findViewById(R.id.paymentsinput);
        fees_show = findViewById(R.id.fees);
        pay_app = findViewById(R.id.book_button);
        paymentLink = findViewById(R.id.linkPayment);

        fees_show.setText("Please Pay Ksh. " + fees_val);

        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true);

        paymentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
            }
        });

        pay_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transactionid = tid.getText().toString().trim();
                if (transactionid.isEmpty() || transactionid.length()<10 || transactionid.length()>10 || !transactionid.startsWith("0")) {
                    tid.setError("Correct Mpesa Paying Number required");
                    tid.requestFocus();
                    return;
                }

                // Call getAccessToken and perform the rest of the logic inside its onResponse
                getAccessToken(new AccessTokenListener() {
                    @Override
                    public void onTokenReceived() {
                        // This code executes when access token is received and STK push is performed
                        String finalSlot_val = slot_val;

                        reference_booking.child(email).child(date_val).child(slot_val).child("Count").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    int count = snapshot.getValue(Integer.class);
                                    count = count + 1;
                                    Booking_Appointments booking_appointments = new Booking_Appointments(1, phone);
                                    reference_booking.child(email).child(date_val).child(slot_val).child(check).setValue(booking_appointments);
                                    reference_booking.child(email).child(date_val).child(finalSlot_val).child("Count").setValue(count);
                                    CareSeeker_Chosen_Slot_Class patient = new CareSeeker_Chosen_Slot_Class(chosen_time, 0, question_data, pname, 0, transactionid);
                                    reference_patient.child(phone).child(email).child(date_val).child(chosen_time).setValue(patient);
                                    reference_doctor.child(email).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String dname = snapshot.getValue(String.class);
                                                Admin_Payment_Class payment = new Admin_Payment_Class(transactionid, dname, email, phone, pname, 0, date_val, chosen_time, 0);
                                                reference_payment.child("Payment0").child(phone).child(date_val).child(chosen_time).setValue(payment);
                                                Toast.makeText(CareSeeker_Payment_Appt.this, "Payment Done! Please Wait for Confirmation!", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(CareSeeker_Payment_Appt.this, CareSeeker.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(CareSeeker_Payment_Appt.this, "The CareGiver is not available! Select another slot with the same transaction ID!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(CareSeeker_Payment_Appt.this, CareSeeker_Booking_Appointments.class);
                                    intent.putExtra("Email ID", email);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onTokenError() {
                        // Handle token retrieval error
                        Toast.makeText(CareSeeker_Payment_Appt.this, "Failed to get access token. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void performSTKPush(String phone_number, String amount) {
        mProgressDialog.setMessage("Processing Payment....");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "Care Connect", // Account reference
                "Happy to serve you"  // Transaction description
        );

        mApiClient.setGetAccessToken(false);

        // Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                System.out.println("post submitted to API. %s " + response.body());
                                tid.setError("Enter Valid Phone Number");
                            } else {
                                System.out.println("Response %s " + response.errorBody().string());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }

    public void getAccessToken(AccessTokenListener listener) {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                    isAuthTokenReceived = true;
                    // Now that the token is received, you can perform the STK push
                    performSTKPush(tid.getText().toString().trim(), fees_val);
                    listener.onTokenReceived();
                } else {
                    listener.onTokenError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                listener.onTokenError();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Booking_Appointments booking_appointments = new Booking_Appointments(0, "null");
        reference_booking.child(email).child(date_val).child(slot_val).child(check).setValue(booking_appointments);
        startActivity(new Intent(CareSeeker_Payment_Appt.this, CareSeeker.class));
    }

    // Define AccessTokenListener interface
    interface AccessTokenListener {
        void onTokenReceived();
        void onTokenError();
    }

}
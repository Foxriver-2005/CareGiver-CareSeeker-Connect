package com.care.careme.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Models.CareGiver_Images;
import com.care.careme.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class Admin_CareGiver_Details extends AppCompatActivity {

    private TextView doctor_name, doctor_spec, doctor_experience, doctor_fee, doctor_slots, doctor_about, emailid;
    private ImageView doctor_image;
    private CareGiver_Images careGiver_images;
    private DatabaseReference reference_doctor, reference_booking;
    private int start, end;
    private String encoded_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_caregiver_details);

        doctor_name = (TextView) findViewById(R.id.doc_name_textview);
        doctor_spec = (TextView) findViewById(R.id.doc_spec_textview);
        doctor_experience = (TextView) findViewById(R.id.Exp_textview_val);
        doctor_fee = (TextView) findViewById(R.id.consult_charges_text_val);
        doctor_slots = (TextView) findViewById(R.id.Available_text_val);
        doctor_about = (TextView) findViewById(R.id.about_doctor);
        emailid = (TextView) findViewById(R.id.email_text_val);
        doctor_image = (ImageView) findViewById(R.id.imageView_doc_display);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        String date_val = DateFormat.format("d MMM yyyy", startDate).toString();
        String email = getIntent().getSerializableExtra("Email ID").toString();
        String name = getIntent().getSerializableExtra("Doctors Name").toString();
        String speciality = getIntent().getSerializableExtra("Speciality").toString();
        encoded_email = email.replace(".", ",");
        reference_doctor = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Data");
        reference_booking = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Chosen_Slots");
        emailid.setText(email);

        reference_doctor.child(encoded_email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()) {
                    doctor_name.setText(datasnapshot.child("name").getValue(String.class));
                    doctor_spec.setText(datasnapshot.child("type").getValue(String.class));
                    doctor_about.setText(datasnapshot.child("desc").getValue(String.class));
                    doctor_experience.setText(datasnapshot.child("experience").getValue(String.class) + " years");
                    doctor_fee.setText("Ksh. " + datasnapshot.child("fees").getValue(String.class));
                    careGiver_images = datasnapshot.child("doc_pic").getValue(CareGiver_Images.class);
                    if (careGiver_images != null) {
                        Picasso.get().load(careGiver_images.getUrl()).into(doctor_image);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        doctor_slots.setText("");
        reference_booking.child(encoded_email).child(date_val).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String time = "";
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        start = Integer.parseInt(dataSnapshot.getKey().split(" - ", 5)[0]);
                        end = Integer.parseInt(dataSnapshot.getKey().split(" - ", 5)[1]);
                        time = time + String.format("%d:00 -%d:00", start, end) + "\n";
                    }
                    doctor_slots.setText(time);
                } else {
                    doctor_slots.setText("CareGiver is not available today!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
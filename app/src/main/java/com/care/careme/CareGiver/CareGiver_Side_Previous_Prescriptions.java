package com.care.careme.CareGiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.care.careme.Helpers.CareGiver_Previous_Prescription_Adapter;
import com.care.careme.Helpers.CareGiver_Session_Mangement;
import com.care.careme.Models.Get_Previous_Prescription_Details;
import com.care.careme.R;

import java.util.ArrayList;

public class CareGiver_Side_Previous_Prescriptions extends AppCompatActivity {
    private RecyclerView rv;
    private String[] gender;
    private String email,dname;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference prescription_doctor;
    private ArrayAdapter<String> gender_adapter;
    private ArrayList<Get_Previous_Prescription_Details> presc;
    private CareGiver_Previous_Prescription_Adapter adapter;

    String name,phone,date,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_side_previous_prescriptions);

        rv=findViewById(R.id.recycler_previous_prescription);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        presc = new ArrayList<>();

        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(this);
        email = doctors_session_mangement.getDoctorSession()[0].replace(".",",");
        name = getIntent().getStringExtra("pname");
        phone = getIntent().getStringExtra("phone");

        prescription_doctor = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Prescription_By_CareGiver");
        prescription_doctor.child(email).child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                presc = new ArrayList<>();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        date=dataSnapshot.getKey();
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            time=dataSnapshot1.getKey();
                            Get_Previous_Prescription_Details gpd= new Get_Previous_Prescription_Details(name,date,time,phone);
                            presc.add(gpd);

                        }


                    }
                    adapter = new CareGiver_Previous_Prescription_Adapter(presc, CareGiver_Side_Previous_Prescriptions.this);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
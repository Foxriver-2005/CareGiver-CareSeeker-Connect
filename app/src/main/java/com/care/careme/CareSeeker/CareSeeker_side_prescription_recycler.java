package com.care.careme.CareSeeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.care.careme.Helpers.CareSeeker_Session_Management;
import com.care.careme.Helpers.Prescription_careseeker_adapter;
import com.care.careme.Models.Get_Prescription_Details;
import com.care.careme.Models.PrescriptionDetails;
import com.care.careme.R;

import java.util.ArrayList;

public class CareSeeker_side_prescription_recycler extends AppCompatActivity {

    private RecyclerView rv;
    private String[] gender;
    private String phone_No,email,dname;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference prescription_doctor;
    private ArrayAdapter<String> gender_adapter;
    private ArrayList<Get_Prescription_Details> presc;
    private Prescription_careseeker_adapter adapter;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_careseeker_side_prescription_recycler);
        search=findViewById(R.id.editTextSearchp);
        rv=findViewById(R.id.recycler_available_prescription);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        presc = new ArrayList<>();

        email = getIntent().getStringExtra("email");
        dname = getIntent().getStringExtra("dr_name");

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        CareSeeker_Session_Management session = new CareSeeker_Session_Management(CareSeeker_side_prescription_recycler.this);
        phone_No = session.getSession();

        prescription_doctor = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Prescription_By_CareGiver");
        prescription_doctor.child(email).child(phone_No).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    presc = new ArrayList<>();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            PrescriptionDetails prescriptionDetails=dataSnapshot1.getValue(PrescriptionDetails.class);
                            Get_Prescription_Details gpd= new Get_Prescription_Details(email,prescriptionDetails.getConsultation_Date(),dataSnapshot1.getKey(),dname, prescriptionDetails.getFlag(), phone_No);
                            presc.add(gpd);
                        }
                    }
                    adapter = new Prescription_careseeker_adapter(presc, CareSeeker_side_prescription_recycler.this);
                    rv.setAdapter(adapter);
                }
                else {
                    Toast.makeText(CareSeeker_side_prescription_recycler.this, "No Prescriptions by CareGiver!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void filter(String text) {

        ArrayList<Get_Prescription_Details> filterdNames = new ArrayList<>();
        for (Get_Prescription_Details gpd: presc) {
            //if the existing elements contains the search input
            if (gpd.getDate().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(gpd);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

}
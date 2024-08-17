package com.care.careme.CareSeeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Helpers.CareGiver_Adapter;
import com.care.careme.Models.CareGiver_Profile;
import com.care.careme.R;

import java.util.ArrayList;
import java.util.List;

public class Available_CareGivers extends AppCompatActivity {

    private List<CareGiver_Profile> listData;
    private List<String> emaildata;
    private EditText search;

    private RecyclerView rv;
    private CareGiver_Adapter adapter;
    private FirebaseUser user;


    private ProgressBar progressBar;
    private DatabaseReference reference_doc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_caregivers);

        search = (EditText) findViewById(R.id.editTextSearch);

        progressBar = findViewById(R.id.progressbarpatient);
        progressBar.setVisibility(View.VISIBLE);
        rv = (RecyclerView) findViewById(R.id.recycler_available_doc);

        String speciality_type = getIntent().getStringExtra("Speciality_type");
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        listData = new ArrayList<>();
        emaildata = new ArrayList<>();
        String flag = getIntent().getStringExtra("flag");

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

        final DatabaseReference nm = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Data");
        nm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listData = new ArrayList<>();
                emaildata = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        String email = npsnapshot.getKey();
                        email = email.replace(",", ".");
                        CareGiver_Profile l = npsnapshot.getValue(CareGiver_Profile.class);
                        if (flag.equals("1")) {
                            if (speciality_type.equals(l.getType())) {
                                listData.add(l);
                                emaildata.add(email);
                            }
                        } else if (flag.equals("0")) {
                            listData.add(l);
                            emaildata.add(email);
                        }

                    }
                    if(listData.isEmpty()){
                        Toast.makeText(Available_CareGivers.this,"No CareGiver is available",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    adapter = new CareGiver_Adapter(listData, emaildata, Available_CareGivers.this);
                    rv.setAdapter(adapter);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void filter(String text) {

        ArrayList<CareGiver_Profile> filterdNames = new ArrayList<>();
        for (CareGiver_Profile doc_data : listData) {
            //if the existing elements contains the search input
            if (doc_data.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(doc_data);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }
}
package com.care.careme.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.AskRole;
import com.care.careme.Helpers.Admin_All_CareGiver_Adapter;
import com.care.careme.Helpers.CareGiver_Session_Mangement;
import com.care.careme.Models.CareGiver_Profile;
import com.care.careme.R;

import java.util.ArrayList;
import java.util.List;

public class Admin_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rv;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private DatabaseReference reference_doc,patient;
    private int flag;
    private String encodedemail;
    private Admin_All_CareGiver_Adapter adapter ;
    private EditText search;
    private Toast backToast;
    private long backPressedTime;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private List<CareGiver_Profile> listData;
    private List<String> emaildata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        search= findViewById(R.id.editTextSearch_appointment);
        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(this);
        encodedemail=doctors_session_mangement.getDoctorSession()[0].replace(".",",");
        rv=(RecyclerView)findViewById(R.id.recycler_doc);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        listData=new ArrayList<>();
        emaildata=new ArrayList<>();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        final DatabaseReference nm= FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Data");
        nm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listData=new ArrayList<>();
                emaildata=new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        String email=npsnapshot.getKey();
                        email = email.replace(",",".");
                        CareGiver_Profile l=npsnapshot.getValue(CareGiver_Profile.class);
                        listData.add(l);
                        emaildata.add(email);
                    }

                    adapter=new Admin_All_CareGiver_Adapter(listData,emaildata, Admin_Activity.this);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, AskRole.class));
            }
            else if (id == R.id.appointment_doc) {
                startActivity(new Intent(this, Admin_Available_Appointments.class));
            }
            else if (id == R.id.payment_doc) {
                startActivity(new Intent(this, Admin_Payments.class));
            }
            else if (id == R.id.chat) {
                startActivity(new Intent(Admin_Activity.this, Admin_ChatDisplay.class));
            }
            else if (id == R.id.Add_doc) {
                startActivity(new Intent(Admin_Activity.this, CareGiver_Add_Admin.class));
            }
            else if (id == R.id.feedback_doc) {
                startActivity(new Intent(Admin_Activity.this, Admin_FeedBack.class));
            }

            else if (id == R.id.settingsApp) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivity(intent);
            }

            else if (id == R.id.logout) {
                CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(Admin_Activity.this);
                doctors_session_mangement.removeSession();
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(Admin_Activity.this, AskRole.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void filter(String text) {
        ArrayList<CareGiver_Profile> filterdNames = new ArrayList<>();
        if(adapter != null) {
            for (CareGiver_Profile doc_data: listData) {
                //if the existing elements contain the search input
                if (doc_data.getName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(doc_data);
                }
            }

            //calling a method of the adapter class and passing the filtered list
            adapter.filterList(filterdNames);
        } else {
            // Adapter is not initialized, log or handle the situation accordingly
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            if(backPressedTime+2000>System.currentTimeMillis())
            {
                finishAffinity();
                backToast.cancel();
                super.onBackPressed();
                return;
            }
            else
            {
                backToast = Toast.makeText(getBaseContext(),"Press Back again to exit",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }
}
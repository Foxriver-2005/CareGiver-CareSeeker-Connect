package com.care.careme.CareGiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Helpers.CareGiver_UserAdapter;
import com.care.careme.Helpers.CareGiver_Session_Mangement;
import com.care.careme.Models.Chat;
import com.care.careme.Models.CareSeeker_Details;
import com.care.careme.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CareGiver_Chat_Display extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CareGiver_UserAdapter doctorUserAdapter;
    private List<CareSeeker_Details> mUsers;
    private DatabaseReference reference, reference_patient;
    private List<String> usersList, status_val;
    private FirebaseUser fuser;
    private String email;
    private HashSet<CareSeeker_Details> hashSet;
    private ArrayList<String> phone_num;
    private TextView chatview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_display_chats);
        chatview = (TextView) findViewById(R.id.textChat);
        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(this);
        email = doctors_session_mangement.getDoctorSession()[0].replace(".", ",");
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                int unread = 0;
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Chat chat = snapshot1.getValue(Chat.class);

                        if (chat.getSender().equals(email)) {
                            usersList.add(chat.getReceiver());
                        }
                        if (chat.getReceiver().equals(email)) {
                            usersList.add(chat.getSender());
                        }
                        if (chat.getReceiver().equals(email) && !chat.isSeen()) {
                            unread++;
                        }
                    }
                    if (unread != 0) {

                        chatview.setText("Your Chats (" + unread + ")");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUsers = new ArrayList<>();
        phone_num = new ArrayList<>();
        status_val = new ArrayList<>();
        reference_patient = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareSeeker_Details");
        reference_patient.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                phone_num = new ArrayList<>();
                status_val = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    CareSeeker_Details details = snapshot1.getValue(CareSeeker_Details.class);
                    mUsers.add(details);
                    phone_num.add(details.getPhone());
                }

                reference_patient.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (String phone : phone_num) {
                            if (snapshot.child(phone).child("status").exists()) {
                                status_val.add(snapshot.child(phone).child("status").getValue(String.class));
                            }
                            else{
                                status_val.add("offline");
                            }
                        }

                        doctorUserAdapter = new CareGiver_UserAdapter(CareGiver_Chat_Display.this, mUsers, status_val, true);
                        recyclerView.setAdapter(doctorUserAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
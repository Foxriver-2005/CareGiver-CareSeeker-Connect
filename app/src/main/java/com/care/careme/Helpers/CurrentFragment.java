package com.care.careme.Helpers;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.Models.Appointment_notif;
import com.care.careme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CurrentFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ArrayList<Appointment_notif> current_appt;
    private String email;
    private Date d1, d2;
    private Appointment_Show_Adapter adapter;
    private EditText search;

    public CurrentFragment(){

    }

    public static CurrentFragment getInstance()
    {
        CurrentFragment currentFragment=new CurrentFragment();
        return currentFragment;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.row_current,container,false);
        search=(EditText) view.findViewById(R.id.editTextSearch_current);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Appointment_Show_Adapter(current_appt);
        recyclerView.setAdapter(adapter);
        current_appt = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Appointments");
        CareGiver_Session_Mangement doctors_session_mangement = new CareGiver_Session_Mangement(getActivity());
        email = doctors_session_mangement.getDoctorSession()[0].replace(".",",");

        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        String month = monthName[cal.get(Calendar.MONTH)];
        int year = cal.get(Calendar.YEAR);
        String value = day + " " + month + " " + year;
        SimpleDateFormat sdformat = new SimpleDateFormat("d-MMM-yyyy");
        try {
            d1 = sdformat.parse(day+"-"+month+"-"+year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                current_appt = new ArrayList<>();
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value_2 = dataSnapshot.getKey();
                        value_2 = value_2.replace(" ", "-");
                        try {
                            d2 = sdformat.parse(value_2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (d1.compareTo(d2) <= 0) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Appointment_notif appointment_notif = dataSnapshot1.getValue(Appointment_notif.class);
                                current_appt.add(appointment_notif);
                            }
                        }

                    }
                    //adapter = new Appointment_Show_Adapter(current_appt);
                    recyclerView.setAdapter(adapter);

                }
                else{
                    Toast.makeText(getActivity(), "There are no Current Appointments!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void filter(String text) {
        ArrayList<Appointment_notif> filterdNames = new ArrayList<>();
        for (Appointment_notif doc_data: current_appt) {
            //if the existing elements contains the search input
            if (doc_data.getDate().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(doc_data);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }
}

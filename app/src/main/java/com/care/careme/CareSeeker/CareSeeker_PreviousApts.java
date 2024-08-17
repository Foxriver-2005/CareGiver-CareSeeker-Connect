package com.care.careme.CareSeeker;

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
import com.care.careme.Helpers.CareSeeker_Appointment_Adapter;
import com.care.careme.Helpers.CareSeeker_Session_Management;
import com.care.careme.Models.Admin_Payment_Class;
import com.care.careme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CareSeeker_PreviousApts extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ArrayList<Admin_Payment_Class> previous_appt;
    private String phone;
    private Date d1, d2;
    private CareSeeker_Appointment_Adapter adapter;
    private EditText search;

    public CareSeeker_PreviousApts(){

    }

    public static CareSeeker_PreviousApts getInstance()
    {
        CareSeeker_PreviousApts previousFragment=new CareSeeker_PreviousApts();
        return previousFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.row_previous,container,false);
        CareSeeker_Session_Management session = new CareSeeker_Session_Management(getContext());
        phone = session.getSession();
        search=(EditText) view.findViewById(R.id.editTextSearch_previous);
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
        previous_appt = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("Admin_Payment");
        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        String month = monthName[cal.get(Calendar.MONTH)];
        int year = cal.get(Calendar.YEAR);
        String value = day + " " + month + " " + year;
        SimpleDateFormat sdformat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            d1 = sdformat.parse(day+"-"+month+"-"+year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reference.child("Payment1").child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                previous_appt = new ArrayList<>();
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value_2 = dataSnapshot.getKey();
                        value_2 = value_2.replace(" ", "-");
                        try {
                            d2 = sdformat.parse(value_2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (d1.compareTo(d2) > 0) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Admin_Payment_Class payment = dataSnapshot1.getValue(Admin_Payment_Class.class);
                                value_2 = value_2.replace("-", " ");
                                reference.child("Payment1").child(phone).child(value_2).child(dataSnapshot1.getKey()).child("status").setValue(1);
                                previous_appt.add(payment);
                            }
                        }

                    }
                }
                reference.child("Payment0").child(phone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String value_2 = dataSnapshot.getKey();
                                value_2 = value_2.replace(" ", "-");
                                try {
                                    d2 = sdformat.parse(value_2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (d1.compareTo(d2) > 0) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        Admin_Payment_Class payment = dataSnapshot1.getValue(Admin_Payment_Class.class);
                                        value_2 = value_2.replace("-", " ");
                                        reference.child("Payment0").child(phone).child(value_2).child(dataSnapshot1.getKey()).child("status").setValue(1);
                                        previous_appt.add(payment);
                                    }
                                }

                            }
                        }
                        if(previous_appt.size() == 0){
                            if(getActivity() != null) {
                                Toast.makeText(getActivity(), "There are no Previous Appointments!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        adapter = new CareSeeker_Appointment_Adapter(previous_appt);
                        recyclerView.setAdapter(adapter);
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
        return view;
    }

    private void filter(String text) {

        ArrayList<Admin_Payment_Class> filterdNames = new ArrayList<>();
        for (Admin_Payment_Class doc_data: previous_appt) {
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
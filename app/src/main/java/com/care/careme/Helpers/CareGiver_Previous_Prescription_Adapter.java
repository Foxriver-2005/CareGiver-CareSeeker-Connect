package com.care.careme.Helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.care.careme.CareGiver.CareGiver_Show_Previous_Prescription_Doctors;
import com.care.careme.Models.Get_Previous_Prescription_Details;
import com.care.careme.R;

import java.util.ArrayList;

public class CareGiver_Previous_Prescription_Adapter extends RecyclerView.Adapter<CareGiver_Previous_Prescription_Adapter.ViewHolder> {
    private ArrayList<Get_Previous_Prescription_Details> get_prescription_details;
    private Context mContext;

    public CareGiver_Previous_Prescription_Adapter(ArrayList<Get_Previous_Prescription_Details> get_prescription_details, Context mContext) {
        this.get_prescription_details = get_prescription_details;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_data_careseeker, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Get_Previous_Prescription_Details obj=get_prescription_details.get(viewHolder.getAdapterPosition());
                Intent intent1 = new Intent(view.getContext(), CareGiver_Show_Previous_Prescription_Doctors.class);
                intent1.putExtra("phone",obj.getPhone_no());
                intent1.putExtra("date",obj.getDate());
                intent1.putExtra("time",obj.getTime());
                view.getContext().startActivity(intent1);
            }
        });
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Get_Previous_Prescription_Details obj=get_prescription_details.get(position);
        holder.p.setText("Data: "+ obj.getDate()+"\n"+"Time: "+obj.getTime());
    }

    @Override
    public int getItemCount() {
        return get_prescription_details.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView p;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            p=itemView.findViewById(R.id.prescriptionTextView);
        }
    }
}

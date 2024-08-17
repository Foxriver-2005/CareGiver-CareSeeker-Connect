package com.care.careme.Helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.care.careme.CareGiver.CareGiver_MessageActivity;
import com.care.careme.Models.CareSeeker_Details;
import com.care.careme.R;

import java.util.List;

public class CareGiver_UserAdapter extends RecyclerView.Adapter<CareGiver_UserAdapter.ViewHolder> {
    private Context mContext;
    private List<CareSeeker_Details> mUsers;
    private boolean isChat;
    private List<String> status;


    public CareGiver_UserAdapter(Context mContext, List<CareSeeker_Details> mUsers, List<String> status, boolean isChat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.status = status;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public CareGiver_UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CareGiver_UserAdapter.ViewHolder holder, int position) {
        CareSeeker_Details detail = mUsers.get(position);
        String info = detail.getName() + " ("+detail.getPhone()+")";
        holder.username.setText(info);
        holder.profile_image.setImageResource(R.drawable.patient_dp);
        if(isChat && status.size()!=0){
            String status_val = status.get(position);
            if(status_val.equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CareGiver_MessageActivity.class);
                intent.putExtra("phone", detail.getPhone());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            profile_image = itemView.findViewById(R.id.profileImage);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.care.careme.CareSeeker.CareSeeker_MessageActivity;
import com.care.careme.Models.Users;
import com.care.careme.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CareSeeker_UserAdapter extends RecyclerView.Adapter<CareSeeker_UserAdapter.ViewHolder> {
    private Context mContext;
    private List<Users> mUsers;
    private boolean isChat;

    public CareSeeker_UserAdapter(Context mContext, List<Users> mUsers, boolean isChat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public CareSeeker_UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new CareSeeker_UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareSeeker_UserAdapter.ViewHolder holder, int position) {
        Users detail = mUsers.get(position);
        String info = detail.getName();
        String email = info.replace(".",",");
        holder.username.setText(info);
        String url = "";
        DatabaseReference reference = FirebaseDatabase.getInstance("https://careconnect-2b4ca-default-rtdb.firebaseio.com/").getReference("CareGiver_Data").child(email).child("doc_pic");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String url = snapshot.child("url").getValue(String.class);
                    Picasso.get().load(url).into(holder.profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(isChat){
            if(detail.getStatus().equals("online")){
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
                Intent intent = new Intent(mContext, CareSeeker_MessageActivity.class);
                intent.putExtra("email", detail.getEmail());
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

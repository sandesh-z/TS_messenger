package com.example.tsmessenger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    Context mainActivity;
    ArrayList<User> usersArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<User> usersArrayList) {
        this.mainActivity=mainActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {

        User users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        holder.userstatus.setText(users.status);
        Glide.with(mainActivity).load(users.imageUri).circleCrop().error(R.drawable.profile_icon).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, ChatActivity.class);
                intent.putExtra("name",users.getUserName());
                intent.putExtra("uid",users.getUserId());
                intent.putExtra("profile_image",users.getImageUri());
                mainActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {

        TextView username;
        TextView userstatus;
        ImageView imageView;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
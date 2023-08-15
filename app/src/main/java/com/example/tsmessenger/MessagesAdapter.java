package com.example.tsmessenger;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<MessageModel> messagesAdapterArrayList;
    String profileImageUrl;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    FirebaseAuth auth;


    public MessagesAdapter(Context context, ArrayList<MessageModel> messagesAdapterArrayList, String profileImageUrl) {
        this.context = context;
        this.messagesAdapterArrayList = messagesAdapterArrayList;
        this.profileImageUrl = profileImageUrl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_view, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_view, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messages = messagesAdapterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.msgText.setText(messages.getMessage());
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.msgText.setText(messages.getMessage());
            Glide.with(context).load(profileImageUrl).circleCrop().error(R.drawable.profile_icon).into(((ReceiverViewHolder) holder).imageView);

        }
    }

    @Override
    public int getItemCount() {
        return messagesAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        MessageModel messages = messagesAdapterArrayList.get(position);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView msgText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            msgText = itemView.findViewById(R.id.sender_text_message);

        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView msgText;
        ImageView imageView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            msgText = itemView.findViewById(R.id.receiver_text_message);
            imageView = itemView.findViewById(R.id.receiver_profile_Image);
        }
    }
}
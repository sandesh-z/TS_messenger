package com.example.tsmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tsmessenger.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String  receiverUid,receiverName,SenderUID;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    String senderRoom,receiverRoom,profileImageUrl;

    RecyclerView message_recycler_view;
    ArrayList<MessageModel> messagesArrayList;
    MessagesAdapter messagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        profileImageUrl = getIntent().getStringExtra("profile_image");


        messagesArrayList = new ArrayList<>();
        message_recycler_view = (RecyclerView) binding.chatRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        message_recycler_view.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messagesArrayList,profileImageUrl);
        message_recycler_view.setAdapter(messagesAdapter);
//        binding.receiverNameText.setText(""+receiverName);

        SenderUID =  firebaseAuth.getUid();
        System.out.println(SenderUID+"senderUID");

        senderRoom = SenderUID+receiverUid;
        receiverRoom = receiverUid+SenderUID;



        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference  chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messages = dataSnapshot.getValue(MessageModel.class);
                    System.out.println(messages.getSenderId()+"senderid here");
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.editTextMessage.getText().toString();
            if (message.isEmpty()){
                Toast.makeText(ChatActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.editTextMessage.setText("");
            Date date = new Date();
            MessageModel messagesModel = new MessageModel(message,SenderUID,date.getTime());

            database=FirebaseDatabase.getInstance();

            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push().setValue(messagesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push().setValue(messagesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                    });

        });
    }
}




package com.example.betterchat;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mensajes.MessagesAdapter;
import mensajes.MessagesList;

public class Activity_chat extends AppCompatActivity {

    // Otros códigos de tu actividad del chat

    private final List<MessagesList> messagesLists = new ArrayList<>();

    private String name;
    private int unseenMessages = 0;

    private String lastMessage = " ";

    private String chatKey = " ";

    private boolean dataSet = false;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://betterchatbd-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        final CircleImageView userProfilePicture = findViewById(R.id.userProfilePicture);
        messagesRecyclerView = findViewById(R.id.mensajesRecyclerView);

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //
        messagesAdapter = new MessagesAdapter(messagesLists, Activity_chat.this);

        messagesRecyclerView.setAdapter(messagesAdapter);


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        final String email = currentUser.getEmail();

        // Obteniendo imagen de FireBase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePictUrl = snapshot.child("username").child(email).child("coverPhotoUrl").getValue(String.class);

                if (profilePictUrl.isEmpty()) {

                    // Imagen de usuario en imagen circular vista de chats
                    Picasso.get().load(profilePictUrl).into(userProfilePicture);

                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesLists.clear();
                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";


                for (DataSnapshot dataSnapshot : snapshot.child("username").getChildren()) {
                    final String getEmail = dataSnapshot.getKey();

                    dataSet= false;

                    if (!getEmail.equals(email)) {


                        final String getName = dataSnapshot.child("displayname").getValue(String.class);
                        final String getProfilePicture = dataSnapshot.child("coverPhotoUrl").getValue(String.class);

                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")) {
                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                            if ((getUserOne.equals(getEmail) && getUserTwo.equals(email)) || getUserOne.equals(email) && getUserTwo.equals(getEmail)) {

                                                for (DataSnapshot chatDataSnapShot : dataSnapshot1.child("messages").getChildren()) {

                                                    final long getMessageKey = Long.parseLong(chatDataSnapShot.getKey());

                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(Activity_chat.this, getKey));

                                                    lastMessage = chatDataSnapShot.child("msg").getValue(String.class);

                                                    if (getMessageKey > getLastSeenMessage) {

                                                        unseenMessages++;

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (!dataSet){
                                    dataSet=true;
                                    MessagesList messagesList = new MessagesList(getName, getEmail, lastMessage, getProfilePicture, unseenMessages, chatKey);
                                    messagesLists.add(messagesList);
                                    messagesAdapter.updateData(messagesLists);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}


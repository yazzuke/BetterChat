package com.example.betterchat;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mensajes.MessagesAdapter;
import mensajes.MessagesList;

public class Activity_chat extends AppCompatActivity {

    private final List<MessagesList> messagesLists = new ArrayList<>();

    private String name;
    private int unseenMessages = 0;
    private String lastMessage = " ";
    private String chatKey = " ";
    private boolean dataSet = false;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://betterchatbd-default-rtdb.firebaseio.com/");
        firestore = FirebaseFirestore.getInstance();

        final CircleImageView userProfilePicture = findViewById(R.id.userProfilePicture);
        messagesRecyclerView = findViewById(R.id.mensajesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessagesAdapter(messagesLists, Activity_chat.this);
        messagesRecyclerView.setAdapter(messagesAdapter);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String email = currentUser.getEmail();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("Usuario").child("coverPhotoUrl").exists()) {
                    String profilePictUrl = snapshot.child("Usuario").child("coverPhotoUrl").getValue(String.class);
                    if (profilePictUrl != null && !profilePictUrl.isEmpty()) {
                        Picasso.get().load(profilePictUrl).into(userProfilePicture);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        firestore.collection("chat").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    messagesLists.clear();
                    unseenMessages = 0;
                    lastMessage = "";
                    chatKey = "";

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String getEmail = document.getId();
                        dataSet = false;

                        if (!getEmail.equals(email)) {
                            String getName = document.getString("displayname");
                            String getProfilePicture = document.getString("coverPhotoUrl");
                            List<Map<String, Object>> messages = (List<Map<String, Object>>) document.get("messages");

                            if (messages != null && !messages.isEmpty()) {
                                for (Map<String, Object> message : messages) {
                                    long getMessageKey = (long) message.get("key");
                                    long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(Activity_chat.this, getEmail));
                                    lastMessage = (String) message.get("mensaje");

                                    if (getMessageKey > getLastSeenMessage) {
                                        unseenMessages++;
                                    }
                                }
                            }

                            if (!dataSet) {
                                dataSet = true;
                                MessagesList messagesList = new MessagesList(getName, getEmail, lastMessage, getProfilePicture, unseenMessages, chatKey);
                                messagesLists.add(messagesList);
                                messagesAdapter.updateData(messagesLists);
                            }
                        }
                    }
                }
            }
        });

        progressDialog.show();
    }
}

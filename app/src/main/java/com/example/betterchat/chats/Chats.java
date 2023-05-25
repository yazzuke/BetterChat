package com.example.betterchat.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.betterchat.MemoryData;
import com.example.betterchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chats extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://betterchatbd-default-rtdb.firebaseio.com/");

    String getUserEmail = "";
    private String chatKey="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        final ImageView backbtn = findViewById(R.id.backBtn);
        final TextView nameTV = findViewById(R.id.userName);
        final EditText messageEditTxt = findViewById(R.id.messageEditText);
        final CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);

        //Obteniendo informacion de MessagesAdapter class

        final String getName = getIntent().getStringExtra("name");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getEmail = getIntent().getStringExtra("email");

        //Obteniendo email de usuario desde memoria

        getUserEmail = MemoryData.getData(Chats.this);

        nameTV.setText(getName);

        Picasso.get().load(getProfilePic).into(profilePic);

        if (chatKey.isEmpty()){

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //Generando llave de chat, por defecto es 1
                    chatKey="1";
                    if (snapshot.hasChild("chat")){

                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String getTxtMessage = messageEditTxt.getText().toString();

                //Obteniendo tiempo actual
                final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

                MemoryData.saveLastMsgTS(currentTimestamp, chatKey, Chats.this);
                databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserEmail);
                databaseReference.child("chat").child(chatKey).child("user_2").setValue(getEmail);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("email").setValue(getUserEmail);



            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
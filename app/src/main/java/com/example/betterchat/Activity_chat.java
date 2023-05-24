package com.example.betterchat;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Activity_chat extends AppCompatActivity {
    // Otros códigos de tu actividad del chat

    private Chatadapter chatAdapter;
    private List<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializa el RecyclerView y el adaptador
        RecyclerView recyclerViewChat = findViewById(R.id.recyclerViewChat);
        chatAdapter = new Chatadapter(chatMessages, this);
        recyclerViewChat.setAdapter(chatAdapter);

        // Otros códigos de tu actividad del chat
    }

    // Otros métodos y códigos de tu actividad del chat
}


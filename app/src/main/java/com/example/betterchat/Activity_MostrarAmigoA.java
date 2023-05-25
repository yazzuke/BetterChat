package com.example.betterchat;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Activity_MostrarAmigoA extends AppCompatActivity {

    private RecyclerView recyclerViewAmigos;
    private AmigosAdapter amigosAdapter;
    private List<Amigo> amigosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_amigos);

        recyclerViewAmigos = findViewById(R.id.recyclerViewAmigos);
        recyclerViewAmigos.setLayoutManager(new LinearLayoutManager(this));

        amigosList = new ArrayList<>();

        // Obtener el ID del usuario actualmente autenticado
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener una instancia de la base de datos de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Realizar la consulta para obtener los amigos del usuario actual
        Query amigosQuery = db.collection("Usuario")
                .document(userID)
                .collection("Amigos");

        amigosQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Limpiar la lista de amigos antes de agregar los nuevos datos
                amigosList.clear();

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        // Obtener los datos del amigo y agregarlos a la lista
                        String nombre = documentSnapshot.getString("usuarioEnviadorDisplayName");
                        String fotoPerfil = documentSnapshot.getString("usuarioEnviadorFotoPerfil");
                        amigosList.add(new Amigo(nombre, fotoPerfil));
                    }

                    // Notificar al adaptador del cambio de datos
                    amigosAdapter.notifyDataSetChanged();
                }
            } else {
                // Manejar el error si la consulta no se completó correctamente
            }
        });

        amigosAdapter = new AmigosAdapter(amigosList);
        recyclerViewAmigos.setAdapter(amigosAdapter);
    }
}


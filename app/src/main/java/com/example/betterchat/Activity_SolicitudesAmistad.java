package com.example.betterchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Activity_SolicitudesAmistad extends AppCompatActivity {

    private RecyclerView recyclerViewSolicitudes;
    private SolicitudAdapter solicitudAdapter;
    private List<SolicitudAmistad> listaSolicitudes;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes_amistad);

        recyclerViewSolicitudes = findViewById(R.id.recyclerViewSolicitudes);

        // Configurar el RecyclerView para mostrar las solicitudes de amistad
        listaSolicitudes = new ArrayList<>();
        solicitudAdapter = new SolicitudAdapter(listaSolicitudes);
        recyclerViewSolicitudes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSolicitudes.setAdapter(solicitudAdapter);

        // Obtener el usuario actual
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Cargar las solicitudes de amistad del usuario actual
        cargarSolicitudesAmistad();

        // Configurar el OnClickListener para aceptar una solicitud
        solicitudAdapter.setOnAcceptClickListener(new SolicitudAdapter.OnAcceptClickListener() {
            @Override
            public void onAcceptClick(int position) {
                SolicitudAmistad solicitud = listaSolicitudes.get(position);
                aceptarSolicitudAmistad(solicitud);
            }
        });

        // Configurar el OnClickListener para rechazar una solicitud
        solicitudAdapter.setOnRejectClickListener(new SolicitudAdapter.OnRejectClickListener() {
            @Override
            public void onRejectClick(int position) {
                SolicitudAmistad solicitud = listaSolicitudes.get(position);
                rechazarSolicitudAmistad(solicitud);
            }
        });
    }

    private void cargarSolicitudesAmistad() {
        String usuarioActualId = currentUser.getUid();

        // Consultar las solicitudes de amistad recibidas del usuario actual
        db.collection("Usuario")
                .document(usuarioActualId)
                .collection("SolicitudesEnviadas")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listaSolicitudes.clear();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SolicitudAmistad solicitud = documentSnapshot.toObject(SolicitudAmistad.class);
                            listaSolicitudes.add(solicitud);
                        }

                        solicitudAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
    }

    private void aceptarSolicitudAmistad(SolicitudAmistad solicitud) {
        String usuarioActualId = currentUser.getUid();
        String usuarioEnviadorId = solicitud.getUsuarioEnviadorId();

        // Eliminar la solicitud de amistad recibida
        db.collection("Usuario")
                .document(usuarioActualId)
                .collection("SolicitudesEnviadas")
                .document(usuarioEnviadorId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Manejar la eliminación exitosa, si es necesario
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });


        // Añadir la amistad en la colección de amigos del usuario actual
        db.collection("Usuario")
                .document(usuarioActualId)
                .collection("Amigos")
                .document(usuarioEnviadorId)
                .set(solicitud)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Manejar la adición exitosa, si es necesario
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });

        // Añadir la amistad en la colección de amigos del usuario que envió la solicitud
        db.collection("Usuario")
                .document(usuarioEnviadorId)
                .collection("Amigos")
                .document(usuarioActualId)
                .set(solicitud)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Manejar la adición exitosa, si es necesario
                        Toast.makeText(getApplicationContext(), "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
                    listaSolicitudes.remove(solicitud);
                    solicitudAdapter.notifyDataSetChanged();

    }

    private void rechazarSolicitudAmistad(SolicitudAmistad solicitud) {
        String usuarioActualId = currentUser.getUid();
        String usuarioEnviadorId = solicitud.getUsuarioEnviadorId();

        // Eliminar la solicitud de amistad recibida
        db.collection("Usuario")
                .document(usuarioActualId)
                .collection("SolicitudesEnviadas")
                .document(usuarioEnviadorId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Manejar la eliminación exitosa, si es necesario
                        Toast.makeText(getApplicationContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
                        listaSolicitudes.remove(solicitud);
                        solicitudAdapter.notifyDataSetChanged();

    }
}
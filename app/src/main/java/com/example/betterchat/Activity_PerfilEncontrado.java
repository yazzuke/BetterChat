package com.example.betterchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Activity_PerfilEncontrado extends AppCompatActivity {

    private ImageView imageViewProfilePicture, imageViewCoverPhoto;
    private TextView textViewDisplayName, textViewUserName, textViewBiografia;
    private Button buttonEnviarSolicitud;
    private RecyclerView recyclerViewEstados;
    private EstadoAdapter estadoAdapter;
    private List<Estado> listaEstados;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private boolean solicitudEnviada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_encontrado);

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);
        textViewDisplayName = findViewById(R.id.textViewDisplayName);
        textViewUserName = findViewById(R.id.textView_VerUserName);
        textViewBiografia = findViewById(R.id.textViewBiografia);
        buttonEnviarSolicitud = findViewById(R.id.buttonEnviarSolicitud);
        recyclerViewEstados = findViewById(R.id.recyclerViewEstados);

        // Configurar el RecyclerView para mostrar los estados
        listaEstados = new ArrayList<>();
        estadoAdapter = new EstadoAdapter(listaEstados);
        recyclerViewEstados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEstados.setAdapter(estadoAdapter);

        // Obtener el usuario actual
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Obtener los datos del usuario encontrado a través del intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("usuarioEncontradoId")) {
            String usuarioEncontradoId = intent.getStringExtra("usuarioEncontradoId");
            cargarDatosUsuarioEncontrado(usuarioEncontradoId);
            cargarEstadosUsuarioEncontrado(usuarioEncontradoId);
        }

        // Verificar el estado de la solicitud y actualizar el botón
        verificarEstadoSolicitud();

        // Configurar el OnClickListener para el botón "Enviar Solicitud"
        buttonEnviarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del usuario encontrado
                String usuarioEncontradoId = getIntent().getStringExtra("usuarioEncontradoId");

                // Obtener el ID y los datos del usuario actual
                String usuarioActualId = currentUser.getUid();
                String nombreUsuarioActual = currentUser.getDisplayName();
                String fotoPerfilUsuarioActual = currentUser.getPhotoUrl().toString();

                // Obtener el username del usuario actual desde Firestore
                db.collection("Usuario").document(usuarioActualId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String usernameUsuarioActual = documentSnapshot.getString("username");

                                    // Crear un objeto SolicitudAmistad y asignar los valores
                                    SolicitudAmistad solicitudAmistad = new SolicitudAmistad();
                                    solicitudAmistad.setUsuarioEnviadorId(usuarioActualId);
                                    solicitudAmistad.setUsuarioReceptorId(usuarioEncontradoId);
                                    solicitudAmistad.setUsuarioEnviadorNombre(nombreUsuarioActual);
                                    solicitudAmistad.setUsuarioEnviadorFotoPerfil(fotoPerfilUsuarioActual);
                                    solicitudAmistad.setUsuarioEnviadorUsername(usernameUsuarioActual);

                                    // Guardar la solicitud en Firestore
                                    db.collection("Usuario").document(usuarioEncontradoId)
                                            .collection("SolicitudesEnviadas").document(usuarioActualId).set(solicitudAmistad)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // La solicitud se ha enviado correctamente
                                                    Toast.makeText(getApplicationContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show();
                                                    solicitudEnviada = true;
                                                    buttonEnviarSolicitud.setText("Solicitud enviada");
                                                    buttonEnviarSolicitud.setEnabled(false);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Manejar el error, si es necesario
                                                    Toast.makeText(getApplicationContext(), "Error al enviar la solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Manejar el error, si es necesario
                            }
                        });
            }
        });
    }

    private void cargarDatosUsuarioEncontrado(String usuarioEncontradoId) {
        db.collection("Usuario").document(usuarioEncontradoId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String displayName = documentSnapshot.getString("displayname");
                            String userName = documentSnapshot.getString("username");
                            String coverPhotoUrl = documentSnapshot.getString("coverPhotoUrl");
                            String biografia = documentSnapshot.getString("biografia");

                            // Cargar la foto de perfil desde Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference profilePictureRef = storageRef.child("FotosdePerfil").child(usuarioEncontradoId + ".jpg");
                            profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profilePictureUrl = uri.toString();

                                    if (displayName != null) {
                                        textViewDisplayName.setText(displayName);
                                    }

                                    if (userName != null) {
                                        textViewUserName.setText(userName);
                                    }

                                    if (profilePictureUrl != null) {
                                        Glide.with(Activity_PerfilEncontrado.this)
                                                .load(profilePictureUrl)
                                                .placeholder(R.drawable.profile_default)
                                                .error(R.drawable.profile_default)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .into(imageViewProfilePicture);
                                    }

                                    if (coverPhotoUrl != null) {
                                        Glide.with(Activity_PerfilEncontrado.this)
                                                .load(coverPhotoUrl)
                                                .placeholder(R.drawable.portadadefault)
                                                .error(R.drawable.portadadefault)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .into(imageViewCoverPhoto);
                                    }

                                    if (biografia != null) {
                                        textViewBiografia.setText(biografia);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Manejar el error, si es necesario
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
    }

    private void cargarEstadosUsuarioEncontrado(String usuarioEncontradoId) {
        db.collection("Usuario").document(usuarioEncontradoId).collection("Estados")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Estado estado = documentSnapshot.toObject(Estado.class);
                            listaEstados.add(estado);
                        }
                        estadoAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
    }

    private void verificarEstadoSolicitud() {
        String usuarioEncontradoId = getIntent().getStringExtra("usuarioEncontradoId");
        String usuarioActualId = currentUser.getUid();

        db.collection("Usuario").document(usuarioEncontradoId)
                .collection("SolicitudesEnviadas").document(usuarioActualId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            solicitudEnviada = true;
                            buttonEnviarSolicitud.setText("Solicitud enviada");
                            buttonEnviarSolicitud.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error, si es necesario
                    }
                });
    }
}
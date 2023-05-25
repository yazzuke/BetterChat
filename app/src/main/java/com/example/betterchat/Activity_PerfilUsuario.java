    package com.example.betterchat;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bumptech.glide.Glide;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.Query;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;

    import java.util.ArrayList;
    import java.util.List;

    public class Activity_PerfilUsuario extends AppCompatActivity {


        private ImageView imageViewProfilePicture, imageViewCoverPhoto, imageViewEditPhoto,imageViewLupaBuscar, imageViewIrChats;
        private TextView textViewDisplayName, textViewUserName, textViewBiografia, textViewSubeunEstado;
        private Button buttonPostStatus;
        private RecyclerView recyclerView;
        private EstadoAdapter adapter;
        private List<Estado> estadoList;

        private FirebaseUser currentUser;
        private FirebaseFirestore db;
        private StorageReference storageReference;
        private static final int IMAGE_PICK_CODE = 123;
        private static final int SEARCH_USER_REQUEST_CODE = 1;
        private Uri selectedImageUri; // Variable para almacenar la URI de la imagen seleccionada

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_perfil_usuario);

            imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
            imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);
            textViewDisplayName = findViewById(R.id.textViewDisplayName);
            textViewUserName = findViewById(R.id.textView_VerUserName);
            textViewBiografia = findViewById(R.id.textViewBiografia);
            imageViewEditPhoto = findViewById(R.id.imageViewEditarPerfil);
            imageViewLupaBuscar = findViewById(R.id.imageView_LupaBuscar);
            buttonPostStatus = findViewById(R.id.buttonPostStatus);
            recyclerView = findViewById(R.id.recyclerViewEstados);
            imageViewIrChats=findViewById(R.id.irChats);

            // Obtener el usuario actual
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();

            // Mostrar la foto de perfil, el nombre de usuario y el nombre de visualización del usuario
            updateProfileInformation();

            // Configurar el OnClickListener para el botón "Editar Perfil"
            imageViewEditPhoto.setOnClickListener(v -> {
                // Abrir la actividad para editar el perfil
                Intent intent = new Intent(Activity_PerfilUsuario.this, Activity_EditarPerfil.class);
                startActivity(intent);
            });


            // Configurar el OnClickListener para el botón "Publicar Estado"
            buttonPostStatus.setOnClickListener(v -> {
                Intent intent = new Intent(Activity_PerfilUsuario.this, Activity_SubirEstado.class);
                startActivity(intent);
            });

            imageViewIrChats.setOnClickListener(v -> {
                // Abrir la actividad para editar el perfil
                Intent intent = new Intent(Activity_PerfilUsuario.this, Activity_chat.class);
                startActivity(intent);
            });

            // Configurar el RecyclerView para mostrar los estados
            estadoList = new ArrayList<>();
            adapter = new EstadoAdapter(estadoList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected void onResume() {
            super.onResume();

            // Actualizar la información del perfil cada vez que la actividad se reanude
            updateProfileInformation();
            // Obtener los estados del usuario desde Firestore
            getEstadosFromFirestore();
        }

            public void onSearchIconClicked(View view) {
                // Abrir el activity de búsqueda de usuarios
                Intent intent = new Intent(this, Activity_BuscarUsuario.class);
                startActivityForResult(intent, SEARCH_USER_REQUEST_CODE);
            }


        private void updateProfileInformation() {
            if (currentUser != null) {
                String displayName = currentUser.getDisplayName();
                Uri photoUrl = currentUser.getPhotoUrl();

                if (displayName != null) {
                    textViewDisplayName.setText(displayName);
                }

                if (photoUrl != null) {
                    Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.profile_default)
                            .error(R.drawable.profile_default)
                            .into(imageViewProfilePicture);
                }

                db.collection("Usuario").document(currentUser.getUid()).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String username = document.getString("username");
                                    String biografia = document.getString("biografia");
                                    String coverPhotoUrl = document.getString("coverPhotoUrl");

                                    if (username != null) {
                                        textViewUserName.setText(username);
                                    }

                                    if (biografia != null) {
                                        textViewBiografia.setText(biografia);
                                    }

                                    if (coverPhotoUrl != null) {
                                        Glide.with(this)
                                                .load(coverPhotoUrl)
                                                .placeholder(R.drawable.portadadefault)
                                                .error(R.drawable.portadadefault)
                                                .into(imageViewCoverPhoto);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Manejar el error, si es necesario
                        });
            }
        }

        private void getEstadosFromFirestore() {
            db.collection("Usuario").document(currentUser.getUid()).collection("Estados")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        estadoList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Estado estado = documentSnapshot.toObject(Estado.class);
                            estadoList.add(estado);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error, si es necesario
                    });
        }
    }

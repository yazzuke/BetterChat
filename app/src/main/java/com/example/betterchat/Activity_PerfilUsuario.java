package com.example.betterchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Activity_PerfilUsuario extends AppCompatActivity {

    private ImageView imageViewProfilePicture, imageViewCoverPhoto;
    private TextView textViewDisplayName, textViewUserName, textViewBiografia;
    private Button buttonEditProfile, buttonPostStatus;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private static final int IMAGE_PICK_CODE = 123;

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
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonPostStatus = findViewById(R.id.buttonPostStatus);

        // Obtener el usuario actual
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Mostrar la foto de perfil, el nombre de usuario y el nombre de visualización del usuario
        updateProfileInformation();

        // Configurar el OnClickListener para el botón "Editar Perfil"
        buttonEditProfile.setOnClickListener(v -> {
            // Abrir la actividad para editar el perfil
            Intent intent = new Intent(Activity_PerfilUsuario.this, Activity_EditarPerfil.class);
            startActivity(intent);
        });

        // Configurar el OnClickListener para el botón "Publicar Estado"
        buttonPostStatus.setOnClickListener(v -> {
            // Abrir el selector de imágenes
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizar la información del perfil cada vez que la actividad se reanude
        updateProfileInformation();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadStatusImage(imageUri);
        }
    }

    private void uploadStatusImage(Uri imageUri) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Crear una referencia al almacenamiento de Firebase para guardar la imagen del estado
            StorageReference imageRef = storageReference.child("estado").child(userId).child(imageUri.getLastPathSegment());

            // Cargar la imagen del estado en Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de descarga de la imagen del estado
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Guardar la URL de la imagen del estado en Firestore
                            saveStatusImageToFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error, si es necesario
                        Toast.makeText(Activity_PerfilUsuario.this, "Error al cargar la imagen del estado", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveStatusImageToFirestore(String imageUrl) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Crear un nuevo documento de estado en la colección "Estados" del usuario actual
            db.collection("Usuario").document(userId).collection("Estados").add(new Estado(imageUrl))
                    .addOnSuccessListener(documentReference -> {
                        // La imagen del estado se guardó correctamente en Firestore
                        Toast.makeText(Activity_PerfilUsuario.this, "Estado publicado correctamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error, si es necesario
                        Toast.makeText(Activity_PerfilUsuario.this, "Error al publicar el estado", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

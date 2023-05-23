package com.example.betterchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Activity_SeteoFotoPerfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewProfilePicture;
    private Button buttonChoosePicture, buttonContinue;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seteo_foto_perfil);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        imageViewProfilePicture = findViewById(R.id.imageView_FotoPerfil);
        buttonChoosePicture = findViewById(R.id.button_ElegirFoto);
        buttonContinue = findViewById(R.id.button_ContinuarFoto);

        buttonChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                StorageReference imageRef = mStorageRef.child("FotosdePerfil/" + user.getUid() + ".jpg");

                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Obtén la URL de descarga de la imagen subida
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Actualiza el perfil del usuario con la URL de la imagen
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Activity_SeteoFotoPerfil.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Activity_SeteoFotoPerfil.this, Activity_PerfilUsuario.class);
                                                            startActivity(intent);
                                                            finish();
                                                            // Continúa a la siguiente actividad o finaliza el flujo de registro
                                                        } else {
                                                            Toast.makeText(Activity_SeteoFotoPerfil.this, "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Activity_SeteoFotoPerfil.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageViewProfilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
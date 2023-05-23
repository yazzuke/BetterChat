package com.example.betterchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Activity_EditarPerfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_COVER_PHOTO = 2;
    private ImageView imageViewProfilePicture;
    private ImageView imageViewCoverPhoto;
    private EditText editTextDisplayName, editTextBiografia;
    private Button buttonChoosePicture;
    private Button buttonChooseCoverPhoto;
    private Button buttonGuardarCambios;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;
    private Uri selectedImageUri;
    private Uri selectedCoverPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        db = FirebaseFirestore.getInstance();

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        imageViewCoverPhoto = findViewById(R.id.imageViewCoverPhoto);
        editTextDisplayName = findViewById(R.id.editTextDisplayNombre);
        editTextBiografia = findViewById(R.id.editTextBiografiaUser);
        buttonChoosePicture = findViewById(R.id.button_ElegirFoto);
        buttonChooseCoverPhoto = findViewById(R.id.button_ElegirFoto2);
        buttonGuardarCambios = findViewById(R.id.buttonGuardarCambios);

        buttonChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonChooseCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCoverPhoto();
            }
        });

        buttonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    private void chooseCoverPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen de portada"), REQUEST_CODE_COVER_PHOTO);
    }

    private void saveChanges() {
        final String displayName = editTextDisplayName.getText().toString().trim();
        final String biografia = editTextBiografia.getText().toString().trim();

        if (!displayName.isEmpty() && !biografia.isEmpty()) {
            if (selectedImageUri != null) {
                uploadProfilePicture(displayName, biografia);
            } else {
                updateProfile(displayName, biografia, null, null);
            }
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfilePicture(final String displayName, final String biografia) {
        if (selectedImageUri != null) {
            if (currentUser != null) {
                StorageReference profilePictureRef = mStorageRef.child("profilePictures/" + currentUser.getUid() + ".jpg");
                profilePictureRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profilePictureUrl = uri.toString();
                                        if (selectedCoverPhotoUri != null) {
                                            uploadCoverPhoto(displayName, biografia, profilePictureUrl);
                                        } else {
                                            updateProfile(displayName, biografia, profilePictureUrl, null);
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Activity_EditarPerfil.this, "Error al subir la imagen de perfil", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            if (selectedCoverPhotoUri != null) {
                uploadCoverPhoto(displayName, biografia, null);
            } else {
                updateProfile(displayName, biografia, null, null);
            }
        }
    }

    private void uploadCoverPhoto(final String displayName, final String biografia, final String profilePictureUrl) {
        if (selectedCoverPhotoUri != null) {
            if (currentUser != null) {
                StorageReference coverPhotoRef = mStorageRef.child("coverPhotos/" + currentUser.getUid() + ".jpg");
                coverPhotoRef.putFile(selectedCoverPhotoUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                coverPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String coverPhotoUrl = uri.toString();
                                        updateProfile(displayName, biografia, profilePictureUrl, coverPhotoUrl);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Activity_EditarPerfil.this, "Error al subir la foto de portada", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            updateProfile(displayName, biografia, profilePictureUrl, null);
        }
    }

    private void updateProfile(final String displayName, final String biografia, @Nullable String profilePictureUrl, @Nullable String coverPhotoUrl) {
        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();

        if (!displayName.isEmpty()) {
            profileUpdatesBuilder.setDisplayName(displayName);
        }

        if (profilePictureUrl != null) {
            profileUpdatesBuilder.setPhotoUri(Uri.parse(profilePictureUrl));
        }

        UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUserDocument(displayName, biografia, coverPhotoUrl);
                        } else {
                            Toast.makeText(Activity_EditarPerfil.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserDocument(String displayName, String biografia, String coverPhotoUrl) {
        if (currentUser != null) {
            db.collection("Usuario").document(currentUser.getUid())
                    .update("displayname", displayName, "biografia", biografia, "coverPhotoUrl", coverPhotoUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Activity_EditarPerfil.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Activity_EditarPerfil.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
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
        } else if (requestCode == REQUEST_CODE_COVER_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedCoverPhotoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedCoverPhotoUri);
                imageViewCoverPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

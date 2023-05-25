package com.example.betterchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Activity_SubirEstado extends AppCompatActivity {

    private Button buttonChooseImage, buttonUploadStatus;
    private ImageView imageViewSelectedImage, ImagenView_SubirImagen;
    private EditText editTextStatusText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_estado);

        ImagenView_SubirImagen = findViewById(R.id.imageView_SubirImagen);
        buttonUploadStatus = findViewById(R.id.button_SubirEstado);
        imageViewSelectedImage = findViewById(R.id.imageView_SubirImagen);
        editTextStatusText = findViewById(R.id.editText_EditaTuEstado);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        ImagenView_SubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonUploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStatus();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void uploadStatus() {
        String statusText = editTextStatusText.getText().toString().trim();

        if (statusText.isEmpty()) {
            Toast.makeText(this, "Please enter a status text", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("status_images/" + currentUser.getUid() + "/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    uploadStatusToFirestore(statusText, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Activity_SubirEstado.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            uploadStatusToFirestore(statusText, null);
        }
    }

    private void uploadStatusToFirestore(String statusText, String imageUrl) {
        Estado estado = new Estado(statusText, imageUrl, currentUser.getUid(), System.currentTimeMillis());
        db.collection("Usuario").document(currentUser.getUid()).collection("Estados")
                .add(estado)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Activity_SubirEstado.this, "Status uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Activity_SubirEstado.this, "Failed to upload status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewSelectedImage.setImageURI(imageUri);
        }
    }
}

package com.example.betterchat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_SeteoName extends AppCompatActivity {
    private EditText editTextDisplayName;
    private Button buttonSaveDisplayName;
    private FirebaseAuth mAuth;
    private TextView textViewVerUserName;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seteo_name);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        editTextDisplayName = findViewById(R.id.editTextDisplayNombre);
        buttonSaveDisplayName = findViewById(R.id.button_CambiarNombre);
        textViewVerUserName = findViewById(R.id.textView_VerUserName);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            textViewVerUserName.setText("Tu Usuario es \"" + displayName + "\", pero puedes colocar el nombre que quieres que vean los demás.");
        }

        buttonSaveDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = editTextDisplayName.getText().toString().trim();
                if (!displayName.isEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            updateFirestoreDisplayName(user.getUid(), displayName);
                                            Toast.makeText(Activity_SeteoName.this, "Tu nombre se guardó exitosamente!", Toast.LENGTH_SHORT).show();
                                            // Continúa a la siguiente actividad
                                            Intent intent = new Intent(Activity_SeteoName.this, Activity_SeteoFotoPerfil.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Activity_SeteoName.this, "Error al guardar el nombre", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(Activity_SeteoName.this, "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity_SeteoName.this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateFirestoreDisplayName(String userId, String displayName) {
        DocumentReference userRef = mFirestore.collection("Usuario").document(userId);
        userRef.update("displayname", displayName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Actualización exitosa en Firestore
                        } else {
                            // Error al actualizar el displayName en Firestore
                        }
                    }
                });
    }
}

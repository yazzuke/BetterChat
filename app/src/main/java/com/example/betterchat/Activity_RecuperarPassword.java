package com.example.betterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Activity_RecuperarPassword extends AppCompatActivity {

    Button recuperar;

    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);
        recuperar=findViewById(R.id.boton_recuperar);
        email=findViewById(R.id.correoRecuperacion);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    public void validate(){
        String email1 = email.getText().toString().trim();

        if (email1.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            email.setError("Correo Inválido");
            return;
        }

        sendEmail(email1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Activity_RecuperarPassword.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendEmail(String email){
        FirebaseAuth auth= FirebaseAuth.getInstance();
        String  emailAddress = email;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Activity_RecuperarPassword.this,"Correo Enviado!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Activity_RecuperarPassword.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(Activity_RecuperarPassword.this,"Correo Inválido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
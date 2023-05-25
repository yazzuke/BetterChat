package com.example.betterchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Activity_BuscarUsuario extends AppCompatActivity {

    private EditText editTextBuscarUsuario;
    private Button buttonBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_usuario);

        // Obtener referencias a los elementos del diseño
        editTextBuscarUsuario = findViewById(R.id.editText_BuscarUsuario);
        buttonBuscar = findViewById(R.id.button_BuscarUsuario);

        // Configurar el botón de búsqueda
        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUsuario();
            }
        });
    }

    private void buscarUsuario() {
        // Obtener el nombre de usuario ingresado
        String username = editTextBuscarUsuario.getText().toString().trim();

        // Obtener la referencia a la colección de usuarios en Firestore
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Usuario");

        // Construir la consulta para buscar usuarios con el nombre ingresado
        Query query = usersRef.whereEqualTo("username", username);

        // Ejecutar la consulta
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Se encontraron usuarios con el nombre ingresado
                    // Supongamos que tomas el primer usuario encontrado
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String userID = documentSnapshot.getId();

                    // Abrir el Activity del perfil del usuario encontrado
                    Intent intent = new Intent(Activity_BuscarUsuario.this, Activity_PerfilEncontrado.class);
                    intent.putExtra("usuarioEncontradoId", userID);
                    startActivity(intent);

                } else {
                    // No se encontraron usuarios con el nombre ingresado
                    // Aquí puedes mostrar un mensaje o realizar alguna acción adicional
                    Toast.makeText(Activity_BuscarUsuario.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Ocurrió un error al realizar la consulta
                // Aquí puedes mostrar un mensaje de error o realizar alguna acción adicional
                Toast.makeText(Activity_BuscarUsuario.this, "Error al buscar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

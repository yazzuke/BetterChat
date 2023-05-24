package com.example.betterchat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Activity_BuscarUsuario extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextBuscarUsuario;
    private FirebaseRecyclerAdapter<Usuario, UsuarioViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_usuario);

        recyclerView = findViewById(R.id.recyclerView_usuariosEncontrados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextBuscarUsuario = findViewById(R.id.editText_BuscarUsuario);
        editTextBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username = editTextBuscarUsuario.getText().toString().trim();
                if (!username.isEmpty()) {
                    buscarUsuario(username);
                } else {
                    adapter.stopListening();
                }
            }
        });
    }

    private void buscarUsuario(String username) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Usuario");

        Query query = usersRef.orderByChild("username").equalTo(username);

        FirebaseRecyclerOptions<Usuario> options = new FirebaseRecyclerOptions.Builder<Usuario>()
                .setQuery(query, Usuario.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Usuario, UsuarioViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position, @NonNull Usuario model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
                return new UsuarioViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textView_nombreUsuario);
        }

        public void bind(Usuario usuario) {
            textViewNombreUsuario.setText(usuario.getUsername());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}


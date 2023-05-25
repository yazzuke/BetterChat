package com.example.betterchat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private List<Amigo> amigosList;

    public AmigosAdapter(List<Amigo> amigosList) {
        this.amigosList = amigosList;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        Amigo amigo = amigosList.get(position);

        holder.textViewNombre.setText(amigo.getNombre());
        Picasso.get().load(amigo.getFotoPerfil()).into(holder.imageViewFotoPerfil);

        holder.imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para abrir el chat con el usuario
                // Puedes usar el amigo.getNombre() para obtener el nombre del usuario
                // y realizar la acción correspondiente
                // Por ejemplo, puedes abrir una actividad de chat pasando el nombre del usuario como parámetro
                Intent intent = new Intent(v.getContext(), Activity_MostrarAmigoA.class);
                intent.putExtra("nombreUsuario", amigo.getNombre());
                v.getContext().startActivity(intent);
            }
        });

        holder.imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amigo amigo = amigosList.get(position);
                String nombreUsuario = amigo.getNombre();

                // Abre la actividad o fragmento del perfil del usuario
                Intent intent = new Intent(v.getContext(), Activity_PerfilEncontrado.class);
                intent.putExtra("nombreUsuario", nombreUsuario);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return amigosList.size();
    }

    public class AmigoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView imageViewFotoPerfil;
        ImageView imageViewChat;
        ImageView imageViewPerfil;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            imageViewFotoPerfil = itemView.findViewById(R.id.imageViewFotoPerfil);
            imageViewChat = itemView.findViewById(R.id.imageViewChat);
            imageViewPerfil = itemView.findViewById(R.id.imageViewPerfil);
        }
    }
}

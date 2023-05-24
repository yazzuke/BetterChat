package com.example.betterchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EstadoAdapter extends RecyclerView.Adapter<EstadoAdapter.EstadoViewHolder> {

    private List<Estado> estados;

    public EstadoAdapter(List<Estado> estados) {
        this.estados = estados;
    }

    @NonNull
    @Override
    public EstadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estado, parent, false);
        return new EstadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadoViewHolder holder, int position) {
        Estado estado = estados.get(position);
        holder.bind(estado);
    }

    @Override
    public int getItemCount() {
        return estados.size();
    }

    static class EstadoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTexto;
        private ImageView imageViewEstado;
        private TextView textViewTextoEstado;

        EstadoViewHolder(View itemView) {
            super(itemView);
            imageViewEstado = itemView.findViewById(R.id.imageViewEstado);
            textViewTextoEstado = itemView.findViewById(R.id.textViewTextoEstado);
            textViewTexto = itemView.findViewById(R.id.textViewTexto);
        }

        void bind(Estado estado) {
            textViewTextoEstado.setText(estado.getTexto());
            textViewTexto.setVisibility(View.GONE); // Ocultar el TextView de la URL

            // Cargar la miniatura de la imagen del estado desde Firebase Storage utilizando Glide
            RequestOptions options = new RequestOptions();

            Glide.with(itemView)
                    .load(estado.getImageUrl())
                    .apply(options)
                    .into(imageViewEstado);
        }
    }
}

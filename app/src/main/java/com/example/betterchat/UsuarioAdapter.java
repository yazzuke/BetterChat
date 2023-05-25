package com.example.betterchat;
import com.example.betterchat.Usuario;

import android.util.Log;
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

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarioList;

    public UsuarioAdapter(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);
        Log.d("UsuarioAdapter", "Username: " + usuario.getUsername());
        Log.d("UsuarioAdapter", "DisplayName: " + usuario.getdisplaymame());
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textView_nombreUsuario);
        }

        public void bind(Usuario usuario) {
            textViewNombreUsuario.setText(usuario.getUsername());

        }
    }
}

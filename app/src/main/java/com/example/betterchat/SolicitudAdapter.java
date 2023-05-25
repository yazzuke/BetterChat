package com.example.betterchat;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private List<SolicitudAmistad> listaSolicitudes;
    private OnAcceptClickListener acceptClickListener;
    private OnRejectClickListener rejectClickListener;

    public interface OnAcceptClickListener {
        void onAcceptClick(int position);
    }

    public interface OnRejectClickListener {
        void onRejectClick(int position);
    }

    public SolicitudAdapter(List<SolicitudAmistad> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public void setOnAcceptClickListener(OnAcceptClickListener listener) {
        this.acceptClickListener = listener;
    }

    public void setOnRejectClickListener(OnRejectClickListener listener) {
        this.rejectClickListener = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitud, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SolicitudAmistad solicitud = listaSolicitudes.get(position);

        holder.textViewNombreUsuario.setText(solicitud.getUsuarioEnviadorUsername());
        holder.buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptClickListener != null) {
                    acceptClickListener.onAcceptClick(position);
                }
            }
        });

        holder.buttonRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rejectClickListener != null) {
                    rejectClickListener.onRejectClick(position);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNombreUsuario;
        public Button buttonAceptar;
        public Button buttonRechazar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);
            buttonAceptar = itemView.findViewById(R.id.buttonAceptar);
            buttonRechazar = itemView.findViewById(R.id.buttonRechazar);
        }
    }
}

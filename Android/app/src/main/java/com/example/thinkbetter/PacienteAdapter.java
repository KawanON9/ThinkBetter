package com.example.thinkbetter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder> {

    private List<Paciente> listaPacientes;
    public PacienteAdapter(List<Paciente> listaPacientes) {
        this.listaPacientes = listaPacientes;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        Paciente paciente = listaPacientes.get(position);
        holder.tvNome.setText(paciente.getNome());
        holder.tvDetalhes.setText(paciente.getDetalhes());
    }

    @Override
    public int getItemCount() {
        return listaPacientes.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvDetalhes;

        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomePaciente);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesPaciente);
        }
    }

}
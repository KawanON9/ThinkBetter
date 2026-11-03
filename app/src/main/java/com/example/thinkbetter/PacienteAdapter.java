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

        // Toque no paciente → Prontuário, levando o ID para buscar os dados na API
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProntuarioActivity.class);
            intent.putExtra(ProntuarioActivity.EXTRA_ID_PACIENTE, paciente.getId());
            v.getContext().startActivity(intent);
        });
    }

    /** Troca a lista exibida (usado quando os dados chegam da API). */
    public void atualizar(List<Paciente> novos) {
        listaPacientes.clear();
        listaPacientes.addAll(novos);
        notifyDataSetChanged();
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
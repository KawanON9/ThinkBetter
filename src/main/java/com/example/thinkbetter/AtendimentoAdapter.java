package com.example.thinkbetter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AtendimentoAdapter extends RecyclerView.Adapter<AtendimentoAdapter.AtendimentoViewHolder> {

    private final List<Atendimento> listaAtendimentos;

    public AtendimentoAdapter(List<Atendimento> listaAtendimentos) {
        this.listaAtendimentos = listaAtendimentos;
    }

    @NonNull
    @Override
    public AtendimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atendimento, parent, false);
        return new AtendimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AtendimentoViewHolder holder, int position) {
        Atendimento atendimento = listaAtendimentos.get(position);
        holder.tvHora.setText(atendimento.getHora());
        holder.tvData.setText(atendimento.getData());
        holder.tvNome.setText(atendimento.getNomePaciente());
        holder.tvDetalhes.setText(atendimento.getDetalhes());
        holder.tvStatus.setText(atendimento.getStatus());

        // Estilização dinâmica baseada no status visual do Figma
        if (atendimento.getTipoStatus() == 0) {
            // Pendente (Laranja/Amarelo)
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
            holder.tvStatus.setTextColor(Color.parseColor("#B85D10"));
        } else if (atendimento.getTipoStatus() == 1) {
            // Confirmado (Verde claro)
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
            holder.tvStatus.setTextColor(Color.parseColor("#1E7E4A"));
        } else {
            // Realizado (Teal claro)
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_teal_light);
            holder.tvStatus.setTextColor(Color.parseColor("#0D5C52"));
        }
    }

    @Override
    public int getItemCount() {
        return listaAtendimentos.size();
    }

    public static class AtendimentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvData, tvNome, tvDetalhes, tvStatus;

        public AtendimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHoraAtendimento);
            tvData = itemView.findViewById(R.id.tvDataAtendimento);
            tvNome = itemView.findViewById(R.id.tvNomePacienteAtendimento);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesAtendimento);
            tvStatus = itemView.findViewById(R.id.tvStatusAtendimento);
        }
    }
}
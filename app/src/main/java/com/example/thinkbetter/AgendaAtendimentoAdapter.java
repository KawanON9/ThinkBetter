package com.example.thinkbetter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AgendaAtendimentoAdapter extends RecyclerView.Adapter<AgendaAtendimentoAdapter.ViewHolder> {

    private final List<Atendimento> listaAtendimentos;

    public AgendaAtendimentoAdapter(List<Atendimento> listaAtendimentos) {
        this.listaAtendimentos = listaAtendimentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atendimento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Atendimento atendimento = listaAtendimentos.get(position);
        holder.tvHora.setText(atendimento.getHora());
        holder.tvData.setText(atendimento.getData());
        holder.tvNome.setText(atendimento.getNomePaciente());
        holder.tvDetalhes.setText(atendimento.getDetalhes());
        holder.tvStatus.setText(atendimento.getStatus());

        // Na Agenda, garantimos que o botão de iniciar fica OCULTO e o de indisponibilizar VISÍVEL
        holder.btnIniciar.setVisibility(View.GONE);
        holder.btnIndisponibilizar.setVisibility(View.VISIBLE);

        // Estilização visual e controle do botão baseado no status
        if (atendimento.getTipoStatus() == 0) {
            // Pendente
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
            holder.tvStatus.setTextColor(Color.parseColor("#B85D10"));
            holder.btnIndisponibilizar.setVisibility(View.GONE); // Não indisponibiliza horário com paciente pendente direto por aqui
        } else if (atendimento.getTipoStatus() == 1) {
            // Confirmado
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
            holder.tvStatus.setTextColor(Color.parseColor("#1E7E4A"));
            holder.btnIndisponibilizar.setVisibility(View.GONE); // Não indisponibiliza horário confirmado
        } else if (atendimento.getTipoStatus() == 2) {
            // Livre
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_teal_light);
            holder.tvStatus.setTextColor(Color.parseColor("#0D5C52"));
            holder.btnIndisponibilizar.setText("Indisponibilizar");
            holder.btnIndisponibilizar.setTextColor(Color.parseColor("#E53E3E"));
            holder.btnIndisponibilizar.setVisibility(View.VISIBLE);
        } else {
            // Indisponível / Fechado
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_teal_light);
            holder.tvStatus.setTextColor(Color.parseColor("#777777"));
            holder.btnIndisponibilizar.setText("Tornar Livre");
            holder.btnIndisponibilizar.setTextColor(Color.parseColor("#2E8B82"));
            holder.btnIndisponibilizar.setVisibility(View.VISIBLE);
        }

        // Lógica de Alternância (Toggle) do botão de Indisponibilizar/Livre
        holder.btnIndisponibilizar.setOnClickListener(v -> {
            if (atendimento.getTipoStatus() == 2) {
                // De Livre para Indisponível
                atendimento.setNomePaciente("Indisponível");
                atendimento.setDetalhes("Horário bloqueado");
                atendimento.setStatus("indisponível");
                atendimento.setTipoStatus(3);
            } else if (atendimento.getTipoStatus() == 3) {
                // De Indisponível para Livre
                atendimento.setNomePaciente("Livre");
                atendimento.setDetalhes("Horário disponível");
                atendimento.setStatus("livre");
                atendimento.setTipoStatus(2);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return listaAtendimentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvData, tvNome, tvDetalhes, tvStatus;
        Button btnIndisponibilizar, btnIniciar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHoraAtendimento);
            tvData = itemView.findViewById(R.id.tvDataAtendimento);
            tvNome = itemView.findViewById(R.id.tvNomePacienteAtendimento);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesAtendimento);
            tvStatus = itemView.findViewById(R.id.tvStatusAtendimento);
            btnIndisponibilizar = itemView.findViewById(R.id.btnIndisponibilizar);
            btnIniciar = itemView.findViewById(R.id.btnIniciarAtendimento);
        }
    }
}

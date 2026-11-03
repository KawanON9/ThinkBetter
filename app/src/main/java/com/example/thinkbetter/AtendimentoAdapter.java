package com.example.thinkbetter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AtendimentoAdapter extends RecyclerView.Adapter<AtendimentoAdapter.AtendimentoViewHolder> {

    private final List<Atendimento> listaAtendimentos;
    private final AgendaAtendimentoAdapter.OnConfirmarListener onConfirmar;

    public AtendimentoAdapter(List<Atendimento> listaAtendimentos) {
        this(listaAtendimentos, null);
    }

    public AtendimentoAdapter(List<Atendimento> listaAtendimentos,
                              AgendaAtendimentoAdapter.OnConfirmarListener onConfirmar) {
        this.listaAtendimentos = listaAtendimentos;
        this.onConfirmar = onConfirmar;
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

        // Configuração visuais dos botões para a Tela de Atendimentos
        holder.btnIndisponibilizar.setVisibility(View.GONE);
        holder.btnIniciar.setVisibility(View.VISIBLE);

        if (atendimento.getTipoStatus() == 0) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
            holder.tvStatus.setTextColor(Color.parseColor("#B85D10"));
            // Pendente vindo do banco → botão "Confirmar" ao lado do "Iniciar"
            if (onConfirmar != null && atendimento.getIdConsulta() > 0) {
                holder.btnIndisponibilizar.setText("Confirmar");
                holder.btnIndisponibilizar.setTextColor(Color.parseColor("#1E7E4A"));
                holder.btnIndisponibilizar.setEnabled(true);
                holder.btnIndisponibilizar.setVisibility(View.VISIBLE);
                holder.btnIndisponibilizar.setOnClickListener(v -> {
                    holder.btnIndisponibilizar.setEnabled(false);
                    onConfirmar.onConfirmar(atendimento);
                });
            }
        } else if (atendimento.getTipoStatus() == 1) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
            holder.tvStatus.setTextColor(Color.parseColor("#1E7E4A"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_teal_light);
            holder.tvStatus.setTextColor(Color.parseColor("#0D5C52"));
        }

        // Simula o link do Google Meet
        holder.btnIniciar.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Redirecionando para o Google Meet...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaAtendimentos.size();
    }

    public static class AtendimentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvData, tvNome, tvDetalhes, tvStatus;
        Button btnIndisponibilizar, btnIniciar;

        public AtendimentoViewHolder(@NonNull View itemView) {
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

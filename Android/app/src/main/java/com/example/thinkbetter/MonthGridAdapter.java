package com.example.thinkbetter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthGridAdapter extends RecyclerView.Adapter<MonthGridAdapter.ViewHolder> {

    private List<DiaCalendario> diasList;
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(DiaCalendario dia);
    }

    public MonthGridAdapter(List<DiaCalendario> diasList, OnDayClickListener listener) {
        this.diasList = diasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_celula_mes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaCalendario dia = diasList.get(position);
        holder.tvNumeroDia.setText(String.valueOf(dia.getDiaDoMes()));

        if (!dia.isEDoMesAtual()) {
            holder.tvNumeroDia.setTextColor(Color.parseColor("#BBCCCC"));
        } else if (dia.isSelecionado()) {
            holder.tvNumeroDia.setBackgroundResource(R.drawable.bg_circle_teal);
            holder.tvNumeroDia.setTextColor(Color.WHITE);
        } else {
            holder.tvNumeroDia.setBackground(null);
            holder.tvNumeroDia.setTextColor(Color.parseColor("#222222"));
        }

        // Exibe indicador se houver consultas neste dia
        if (dia.getAtendimentos() != null && !dia.getAtendimentos().isEmpty()) {
            holder.indicatorStatus.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onDayClick(dia));
    }

    @Override
    public int getItemCount() {
        return diasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroDia;
        View indicatorStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroDia = itemView.findViewById(R.id.tvNumeroDia);
            indicatorStatus = itemView.findViewById(R.id.indicatorStatus);
        }
    }
}
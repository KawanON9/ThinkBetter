package com.example.thinkbetter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DocumentoAdapter extends RecyclerView.Adapter<DocumentoAdapter.DocumentoViewHolder> {

    private List<Documento> listaDocumentos;

    public DocumentoAdapter(List<Documento> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    @NonNull
    @Override
    public DocumentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_documento, parent, false);
        return new DocumentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentoViewHolder holder, int position) {
        Documento doc = listaDocumentos.get(position);
        holder.tvTitulo.setText(doc.getTitulo());
        holder.tvDetalhes.setText(doc.getProfissionalEData());

        holder.btnBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "A transferir: " + doc.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDocumentos.size();
    }

    public static class DocumentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalhes;
        Button btnBaixar;

        public DocumentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloDocumento);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesDocumento);
            btnBaixar = itemView.findViewById(R.id.btnBaixar);
        }
    }
}
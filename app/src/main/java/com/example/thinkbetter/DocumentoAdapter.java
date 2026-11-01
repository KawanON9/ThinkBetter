package com.example.thinkbetter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

    public void atualizarLista(List<Documento> novaLista) {
        this.listaDocumentos = novaLista;
        notifyDataSetChanged();
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
        holder.tvDetalhes.setText(doc.getDetalhes());

        holder.btnDownload.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Baixando: " + doc.getTitulo() + "...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaDocumentos.size();
    }

    public static class DocumentoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDetalhes;
        ImageButton btnDownload;

        public DocumentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloDocumento);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhesDocumento);
            btnDownload = itemView.findViewById(R.id.btnDownloadDoc);
        }
    }
}

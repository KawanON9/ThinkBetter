package com.example.thinkbetter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EmissaoDocumentoActivity extends MenuJava {

    private FrameLayout containerEtapas;
    private TextView step1, step2, step3, step4, step5, tvRecomecar;
    
    // Variáveis de estado do documento que está sendo criado
    private String pacienteSelecionado = "";
    private String tipoDocumentoSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissao_documento);

        setupMenuPsicologoButton(R.id.btnMenu);

        containerEtapas = findViewById(R.id.containerEtapas);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        step5 = findViewById(R.id.step5);
        tvRecomecar = findViewById(R.id.tvRecomecar);

        tvRecomecar.setOnClickListener(v -> irParaEtapa(1));

        // Começa na Etapa 1
        irParaEtapa(1);
    }

    public void irParaEtapa(int numeroEtapa) {
        containerEtapas.removeAllViews();
        atualizarSteppersVisuais(numeroEtapa);

        LayoutInflater inflater = LayoutInflater.from(this);
        View viewEtapa;

        switch (numeroEtapa) {
            case 1:
                viewEtapa = inflater.inflate(R.layout.etapa_1_documento ,containerEtapas, false);
                // Configurar clique no paciente para avançar (Ex: Carlos Mendes)
                viewEtapa.findViewById(R.id.cardCarlosMendes).setOnClickListener(v -> {
                    pacienteSelecionado = "Carlos Mendes";
                    irParaEtapa(2);
                });
                break;

            case 2:
                viewEtapa = inflater.inflate(R.layout.etapa_2_documento, containerEtapas, false);
                // Configurar clique no tipo de documento (Ex: Atestado)
                viewEtapa.findViewById(R.id.cardAtestado).setOnClickListener(v -> {
                    tipoDocumentoSelecionado = "Atestado";
                    irParaEtapa(3);
                });
                viewEtapa.findViewById(R.id.cardDeclaracao).setOnClickListener(v -> {
                    tipoDocumentoSelecionado = "Declaração";
                    irParaEtapa(3);
                });
                viewEtapa.findViewById(R.id.cardLaudo).setOnClickListener(v -> {
                    tipoDocumentoSelecionado = "Laudo";
                    irParaEtapa(3);
                });
                break;

            case 3:
                viewEtapa = inflater.inflate(R.layout.etapa_3_documento, containerEtapas, false);
                viewEtapa.findViewById(R.id.btnPreVisualizar).setOnClickListener(v -> {
                    irParaEtapa(4);
                });
                break;

            case 4:
                viewEtapa = inflater.inflate(R.layout.etapa_4_documento , containerEtapas, false);
                viewEtapa.findViewById(R.id.btnConfirmarDoc).setOnClickListener(v -> {
                    irParaEtapa(5);
                });
                viewEtapa.findViewById(R.id.btnEditarDoc).setOnClickListener(v -> {
                    irParaEtapa(3);
                });
                break;

            case 5:
                viewEtapa = inflater.inflate(R.layout.etapa_5_documento, containerEtapas, false);
                viewEtapa.findViewById(R.id.btnEnviarWhatsApp).setOnClickListener(v -> {
                    Toast.makeText(this, "Enviando por WhatsApp...", Toast.LENGTH_SHORT).show();
                });
                break;

            default:
                return;
        }

        containerEtapas.addView(viewEtapa);
    }

    private void atualizarSteppersVisuais(int etapaAtual) {
        TextView[] steps = {step1, step2, step3, step4, step5};
        for (int i = 0; i < steps.length; i++) {
            if ((i + 1) == etapaAtual) {
                steps[i].setBackgroundResource(R.drawable.bg_circle_teal);
                steps[i].setTextColor(getResources().getColor(android.R.color.white));
            } else if ((i + 1) < etapaAtual) {
                steps[i].setBackgroundResource(R.drawable.bg_circle_teal); // Passados concluídos
                steps[i].setText("✓");
                steps[i].setTextColor(getResources().getColor(android.R.color.white));
            } else {
                steps[i].setBackgroundResource(R.drawable.bg_circle_white_border);
                steps[i].setText(String.valueOf(i + 1));
                steps[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
}

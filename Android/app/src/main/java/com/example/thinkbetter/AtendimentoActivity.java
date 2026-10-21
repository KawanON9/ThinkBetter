package com.example.thinkbetter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AtendimentoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AtendimentoAdapter adapter;
    private List<Atendimento> atendimentoList;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimentos);

        btnMenu = findViewById(R.id.btnMenu);
        recyclerView = findViewById(R.id.recyclerViewAtendimentos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populando a lista com os dados exatos extraídos da imagem de referência
        atendimentoList = new ArrayList<>();
        atendimentoList.add(new Atendimento("14:00", "09/09", "Fernanda Lima", "Avaliação · Unimed", "pendente", 0));
        atendimentoList.add(new Atendimento("17:00", "09/09", "Luciana Rocha", "Retorno · Particular", "confirmado", 1));
        atendimentoList.add(new Atendimento("14:00", "08/09", "Fernanda Lima", "Avaliação · Unimed", "realizado", 2));

        adapter = new AtendimentoAdapter(atendimentoList);
        recyclerView.setAdapter(adapter);

        // Ação de clique do menu
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AtendimentoActivity.this, "Abrir menu lateral", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
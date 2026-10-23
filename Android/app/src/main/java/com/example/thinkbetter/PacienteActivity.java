package com.example.thinkbetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PacienteActivity extends MenuJava {

    private RecyclerView recyclerView;
    private PacienteAdapter adapter;
    private List<Paciente> pacienteList;
    private Button btnSort;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        // Ativa o botão de abrir o menu lateral (hambúrguer)
        setupMenuButton(R.id.btnMenu);

        btnSort = findViewById(R.id.btnSort);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewPacientes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populando a lista com os dados extraídos da imagem de referência
        pacienteList = new ArrayList<>();
        pacienteList.add(new Paciente("Carlos Mendes", "Particular · (11) 9 9000-0002"));
        pacienteList.add(new Paciente("Fernanda Lima", "Unimed · (11) 9 9000-0003"));
        pacienteList.add(new Paciente("João Pereira", "Bradesco Saúde · (11) 9 9000-0004"));
        pacienteList.add(new Paciente("Luciana Rocha", "Particular · (11) 9 9000-0005"));
        pacienteList.add(new Paciente("Maria Silva", "Amil · (11) 9 9000-0001"));
        pacienteList.add(new Paciente("Ricardo Barros", "Sul América · (11) 9 9000-0006"));

        adapter = new PacienteAdapter(pacienteList);
        recyclerView.setAdapter(adapter);


        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(PacienteActivity.this, "Ordenação A-Z ativada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
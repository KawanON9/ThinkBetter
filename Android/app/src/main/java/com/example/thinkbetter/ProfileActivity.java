package com.example.thinkbetter;

import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.thinkbetter.ConfiguracoesActivity;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends BaseActivity
{
    private ImageView imgProfile;
    private EditText edtNome, edtEmail, edtTelefone;
    private TextInputLayout tilEmail;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) showPhoto(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupHeader("Meu Perfil");

        imgProfile = findViewById(R.id.imgProfile);
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        tilEmail = findViewById(R.id.tilEmail);

        // Carrega dados salvos
        edtNome.setText(prefs().getString("nome", ""));
        edtEmail.setText(prefs().getString("email", ""));
        edtTelefone.setText(prefs().getString("telefone", ""));
        String photo = prefs().getString("photoUri", null);
        if (photo != null) showPhoto(Uri.parse(photo));

        findViewById(R.id.txtEditPhoto).setOnClickListener(v ->
                pickPhoto.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        findViewById(R.id.btnSalvar).setOnClickListener(v -> save());

        ((TextView) findViewById(R.id.txtIniciais)).setText(iniciais(edtNome.getText().toString()));
    }

    private void save() {
        String email = edtEmail.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            return;
        }
        tilEmail.setError(null);

        prefs().edit()
                .putString("nome", edtNome.getText().toString().trim())
                .putString("email", email)
                .putString("telefone", edtTelefone.getText().toString().trim())
                .apply();
        Toast.makeText(this, "Perfil salvo", Toast.LENGTH_SHORT).show();

        ((TextView) findViewById(R.id.txtIniciais)).setText(iniciais(edtNome.getText().toString()));
    }

    private String iniciais(String nome)
    {
        String[] partes = nome.trim().split("\\s+");
        if (partes[0].isEmpty()) return "DR";
        String a = partes[0].substring(0, 1);
        String b = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (a + b).toUpperCase();
    }

    private void showPhoto(Uri u) {
        imgProfile.setImageURI(u);
        findViewById(R.id.txtIniciais).setVisibility(View.GONE);
        prefs().edit().putString("photoUri", u.toString()).apply();
    }
}
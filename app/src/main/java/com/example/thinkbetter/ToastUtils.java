package com.example.thinkbetter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

    public static void mostrar(Context context, String mensagem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        
        // Infla o layout do toast personalizado
        View layoutToast = inflater.inflate(R.layout.toast_personalizado, null);

        // Define a mensagem personalizada
        TextView tvMensagem = layoutToast.findViewById(R.id.tvToastMensagem);
        if (tvMensagem != null && mensagem != null) {
            tvMensagem.setText(mensagem);
        }

        // Exibe o Toast customizado
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layoutToast);
        toast.show();
    }
}

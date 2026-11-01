package com.example.thinkbetter;

import android.os.Bundle;

public class NotificationsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupHeader("Notificações");

        bindSwitch(R.id.itemLembretes, "Lembretes",
                "Avisos de consultas e compromissos", "notif_lembretes", true);
        bindSwitch(R.id.itemConfirmacoes, "Confirmações",
                "Quando um paciente confirmar ou cancelar", "notif_confirmacoes", true);
        bindSwitch(R.id.itemAlertas, "Alertas",
                "Avisos importantes do sistema", "notif_alertas", true);
        bindSwitch(R.id.itemSilencio, "Modo Silencioso à Noite",
                "Sem notificações das 22h às 7h", "notif_silencio", false);
        bindSwitch(R.id.itemSom, "Som e Vibração",
                "Tocar som ao receber notificações", "notif_som", true);
    }
}
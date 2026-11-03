package com.example.thinkbetter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Regras de navegação do app, usadas pelos dois menus (MenuJava e MenuPacienteJava).
 *
 * Antes, o menu lateral fechava a tela atual a cada navegação — inclusive a tela
 * inicial. Sem nada "por baixo", o botão voltar do Android fechava o app.
 * Agora a pilha de telas fica sempre:  Tela inicial  →  tela escolhida no menu.
 */
final class Navegacao {

    private Navegacao() { }

    /** Quem quiser tratar o "voltar" antes do padrão (ex.: etapas da Emissão de Documentos). */
    interface TratadorDeVoltar {
        /** @return true se tratou o voltar (a tela continua aberta). */
        boolean tratarVoltar();
    }

    /**
     * Abre uma tela pelo menu lateral.
     * - Destino = tela inicial: volta para a que já está aberta por baixo (sem criar outra).
     * - Tela atual = tela inicial: ela continua aberta por baixo.
     * - Tela atual = outra tela: é fechada, para a pilha não crescer a cada toque no menu.
     */
    static void irPara(AppCompatActivity atual, Class<?> destino, Class<?> telaInicial) {
        if (destino.isInstance(atual)) return; // já está nela

        Intent intent = new Intent(atual, destino);
        if (destino == telaInicial) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        atual.startActivity(intent);
        if (!telaInicial.isInstance(atual)) {
            atual.finish();
        }
    }

    /**
     * Comportamento do botão/gesto "voltar" do Android:
     *  1. menu lateral aberto → fecha o menu;
     *  2. a tela tem um voltar próprio (ex.: etapa anterior) → usa ele;
     *  3. tela inicial → pede um segundo toque para sair do app;
     *  4. demais telas → volta para a tela anterior.
     *
     * Usa o OnBackPressedDispatcher: com targetSdk 36+, o Android não chama mais
     * o antigo onBackPressed() (gesto de voltar "preditivo").
     */
    static void configurarVoltar(AppCompatActivity tela, DrawerLayout drawer,
                                 boolean ehTelaInicial, TratadorDeVoltar tratador) {
        tela.getOnBackPressedDispatcher().addCallback(tela, new OnBackPressedCallback(true) {
            private long ultimoToque = 0;

            @Override
            public void handleOnBackPressed() {
                if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                if (tratador != null && tratador.tratarVoltar()) {
                    return;
                }
                if (ehTelaInicial) {
                    long agora = SystemClock.elapsedRealtime();
                    if (agora - ultimoToque < 2000) {
                        tela.finish();
                    } else {
                        ultimoToque = agora;
                        Toast.makeText(tela, "Toque em voltar novamente para sair", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                tela.finish();
            }
        });
    }

    /**
     * Coloca um botão "←" ao lado do botão de menu (☰), com o mesmo visual.
     * Fica em um lugar só para não precisar editar o layout de cada tela.
     * Telas que já têm um voltar próprio (btnVoltar / btnBack) são ignoradas.
     */
    static void adicionarBotaoVoltar(AppCompatActivity tela) {
        View btnMenu = tela.findViewById(R.id.btnMenu);
        if (btnMenu == null) return;
        if (tela.findViewById(R.id.btnVoltar) != null || tela.findViewById(R.id.btnBack) != null) return;

        ViewGroup pai = (ViewGroup) btnMenu.getParent();
        float dp = tela.getResources().getDisplayMetrics().density;
        int tamanho = Math.round(48 * dp);
        int espaco = Math.round(8 * dp);

        ImageButton voltar = new ImageButton(tela);
        voltar.setId(View.generateViewId());
        voltar.setBackgroundResource(R.drawable.bg_circle_white_border);
        voltar.setImageResource(R.drawable.baseline_arrow_back_24);
        voltar.setImageTintList(ColorStateList.valueOf(Color.parseColor("#2E8B82")));
        voltar.setContentDescription("Voltar");
        voltar.setOnClickListener(v -> tela.getOnBackPressedDispatcher().onBackPressed());

        if (pai instanceof ConstraintLayout) {
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(tamanho, tamanho);
            lp.startToEnd = btnMenu.getId();
            lp.topToTop = btnMenu.getId();
            lp.bottomToBottom = btnMenu.getId();
            lp.setMarginStart(espaco);
            pai.addView(voltar, lp);
        } else if (pai instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(tamanho, tamanho);
            lp.addRule(RelativeLayout.END_OF, btnMenu.getId());
            lp.addRule(RelativeLayout.ALIGN_TOP, btnMenu.getId());
            lp.setMarginStart(espaco);
            pai.addView(voltar, lp);
        } else if (pai instanceof LinearLayout) {
            // Coloca ☰ e ← lado a lado numa linha, no lugar onde o ☰ estava
            int posicao = pai.indexOfChild(btnMenu);
            ViewGroup.LayoutParams lpMenu = btnMenu.getLayoutParams();
            pai.removeView(btnMenu);

            LinearLayout linha = new LinearLayout(tela);
            linha.setOrientation(LinearLayout.HORIZONTAL);
            linha.addView(btnMenu, new LinearLayout.LayoutParams(tamanho, tamanho));
            LinearLayout.LayoutParams lpVoltar = new LinearLayout.LayoutParams(tamanho, tamanho);
            lpVoltar.setMarginStart(espaco);
            linha.addView(voltar, lpVoltar);

            // Mesma posição e margens do ☰, mas largura/altura para caber os dois botões
            ViewGroup.LayoutParams lpLinha = lpMenu instanceof LinearLayout.LayoutParams
                    ? new LinearLayout.LayoutParams((LinearLayout.LayoutParams) lpMenu)
                    : new LinearLayout.LayoutParams(lpMenu);
            lpLinha.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lpLinha.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            pai.addView(linha, posicao, lpLinha);
        }
    }
}

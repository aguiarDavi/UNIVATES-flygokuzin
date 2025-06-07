package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ParallaxActivity {

    private Bitmap image;      // Imagem de fundo (camada do parallax)
    private float speed;       // Velocidade com que a imagem se move (efeito de parallax)
    private int screenHeight;  // Altura da tela

    // Coordenadas verticais de duas cópias da imagem
    private float y1, y2;

    public ParallaxActivity(Bitmap image, float speed, int screenHeight) {
        this.image = image;
        this.speed = speed;
        this.screenHeight = screenHeight;

        y1 = 0;
        y2 = -screenHeight; // Começa acima da tela para que desça e cause o efeito de loop
    }

    // Atualiza a posição vertical das imagens para criar o efeito de fundo em movimento
    public void update() {
        y1 += speed;
        y2 += speed;

        // Se uma imagem sair da tela por baixo, reposiciona para cima
        if (y1 >= screenHeight) {
            y1 = y2 - screenHeight;
        } else if (y2 >= screenHeight) {
            y2 = y1 - screenHeight;
        }
    }

    // Desenha as duas imagens uma embaixo da outra, criando um loop contínuo
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, 0, Math.round(y1), null);
        canvas.drawBitmap(image, 0, Math.round(y2), null);
    }
}

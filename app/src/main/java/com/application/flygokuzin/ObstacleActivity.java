package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

public class ObstacleActivity {

    // Posição e dimensões do obstáculo
    private float x, y, width, height, speed;
    private float screenHeight;

    // Imagem do obstáculo
    private Bitmap imagem;

    // Retângulo usado para verificar colisões
    private RectF rect = new RectF();

    public ObstacleActivity(Context context, float startX, float screenHeight) {
        this.x = startX;
        this.y = 0;
        this.width = 180;
        this.height = 180;
        this.speed = 10;
        this.screenHeight = screenHeight;

        // Lista de imagens possíveis dos obstáculos
        int[] imagens = {
                R.drawable.kuririn,
                R.drawable.majin_boo,
                R.drawable.piccolo,
                R.drawable.vegeta,
                R.drawable.cell,
                R.drawable.freeza_dourado,
                R.drawable.android_20
        };

        // Seleciona uma imagem aleatória da lista acima
        int index = new Random().nextInt(imagens.length);
        imagem = BitmapFactory.decodeResource(context.getResources(), imagens[index]);
        imagem = Bitmap.createScaledBitmap(imagem, (int) width, (int) height, false);
    }

    // Atualiza a posição vertical do obstáculo
    public void update() {
        y += speed;
    }

    // Verifica se o obstáculo saiu da tela (parte inferior)
    public boolean isOffScreen() {
        return y > screenHeight;
    }

    // Desenha a imagem do obstáculo na tela
    public void draw(Canvas canvas) {
        if (imagem != null) {
            canvas.drawBitmap(imagem, x, y, null);
        }
    }

    // Verifica colisão simples entre o obstáculo e o personagem
    public boolean colideCom(PlayerActivity player) {
        rect.set(x, y, x + width, y + height);
        return rect.contains(player.getX(), player.getY());
    }

    // Aumenta a velocidade do obstáculo com base em um fator
    public void aumentarVelocidade(float fator) {
        speed *= fator;
    }

    // Retorna a coordenada X atual
    public float getX() {
        return x;
    }

    // Retorna a coordenada Y atual
    public float getY() {
        return y;
    }
}

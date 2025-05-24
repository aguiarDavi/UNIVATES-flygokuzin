package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerActivity {
    private float x, y, radius;
    private float canvasWidth; // largura da tela para limitar o movimento
    private Bitmap[] spritesDireita;
    private Bitmap[] spritesEsquerda;
    private int spriteIndex = 0;
    private boolean indoEsquerda = false;
    private boolean direcaoAtualEsquerda = false;

    public PlayerActivity(float x, float y, float radius,
                          Bitmap[] spritesDireita, Bitmap[] spritesEsquerda) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.spritesDireita = spritesDireita;
        this.spritesEsquerda = spritesEsquerda;
    }

    public void setCanvasWidth(float width) {
        this.canvasWidth = width;
    }

    public void update(float sensorX) {
        // Movimento lateral via acelerômetro
        if (sensorX > 1) {
            x -= sensorX * 5;
            indoEsquerda = true;
        } else if (sensorX < -1) {
            x -= sensorX * 5;
            indoEsquerda = false;
        }

        // Impedir que o personagem saia da tela
        if (x < radius) x = radius;
        if (x > canvasWidth - radius) x = canvasWidth - radius;

        // Alterna animação apenas se estiver se movendo
        if (Math.abs(sensorX) > 1) {
            if (indoEsquerda != direcaoAtualEsquerda) {
                spriteIndex = 0;
                direcaoAtualEsquerda = indoEsquerda;
            } else {
                spriteIndex = (spriteIndex + 1) % spritesDireita.length;
            }
        }
    }

    public void draw(Canvas canvas) {
        Bitmap spriteAtual = direcaoAtualEsquerda
                ? spritesEsquerda[spriteIndex]
                : spritesDireita[spriteIndex];

        canvas.drawBitmap(spriteAtual,
                x - spriteAtual.getWidth() / 2,
                y - spriteAtual.getHeight() / 2,
                null);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getRadius() { return radius; }
}

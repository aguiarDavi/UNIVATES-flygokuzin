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
    private float velocityX = 0;
    private final float FRICTION = 0.9f;


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
        // Aplicar aceleração baseada no sensor
        velocityX -= sensorX * 1.0f;  // fator de aceleração ajustável (1.0f ou outro)

        // Aplicar atrito para suavizar movimento
        velocityX *= FRICTION;

        x += velocityX;

        // Limitar dentro da tela
        if (x < radius) {
            x = radius;
            velocityX = 0; // para não ficar tentando sair
        }
        if (x > canvasWidth - radius) {
            x = canvasWidth - radius;
            velocityX = 0;
        }

        // Alterna animação apenas se estiver se movendo
        if (Math.abs(velocityX) > 1) {  // Usa velocityX para detectar movimento
            boolean indoEsquerdaAgora = velocityX < 0;

            if (indoEsquerdaAgora != direcaoAtualEsquerda) {
                spriteIndex = 0;
                direcaoAtualEsquerda = indoEsquerdaAgora;
            } else {
                if (direcaoAtualEsquerda) {
                    spriteIndex = (spriteIndex + 1) % spritesEsquerda.length;
                } else {
                    spriteIndex = (spriteIndex + 1) % spritesDireita.length;
                }
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

package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerActivity {
    private float x, y, radius;
    private float canvasWidth; // Largura da tela, usada para limitar movimento do personagem
    private Bitmap[] spritesDireita;  // Sprites de animação para movimento à direita
    private Bitmap[] spritesEsquerda; // Sprites de animação para movimento à esquerda
    private int spriteIndex = 0;      // Índice atual do sprite sendo desenhado
    private boolean indoEsquerda = false;          // Indicador temporário de direção
    private boolean direcaoAtualEsquerda = false;  // Direção atual persistente
    private float velocityX = 0;                   // Velocidade horizontal
    private final float FRICTION = 0.9f;           // Fator de atrito para suavizar movimento

    // Construtor: recebe posição, raio e os sprites de ambas as direções
    public PlayerActivity(float x, float y, float radius,
                          Bitmap[] spritesDireita, Bitmap[] spritesEsquerda) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.spritesDireita = spritesDireita;
        this.spritesEsquerda = spritesEsquerda;
    }

    // Define a largura do canvas para limitar movimento horizontal
    public void setCanvasWidth(float width) {
        this.canvasWidth = width;
    }

    // Atualiza a posição e animação com base no sensor (acelerômetro)
    public void update(float sensorX) {
        // Aplica aceleração com base no movimento do celular
        velocityX -= sensorX * 1.0f;  // Fator ajustável de sensibilidade

        // Aplica atrito para suavizar a desaceleração
        velocityX *= FRICTION;

        // Atualiza a posição do jogador
        x += velocityX;

        // Impede o jogador de sair da tela
        if (x < radius) {
            x = radius;
            velocityX = 0; // Impede empurrar contra a borda
        }
        if (x > canvasWidth - radius) {
            x = canvasWidth - radius;
            velocityX = 0;
        }

        // Atualiza a animação apenas se estiver se movendo
        if (Math.abs(velocityX) > 1) {
            boolean indoEsquerdaAgora = velocityX < 0;

            if (indoEsquerdaAgora != direcaoAtualEsquerda) {
                // Mudou de direção, reinicia animação
                spriteIndex = 0;
                direcaoAtualEsquerda = indoEsquerdaAgora;
            } else {
                // Continua na mesma direção, avança quadro da animação
                if (direcaoAtualEsquerda) {
                    spriteIndex = (spriteIndex + 1) % spritesEsquerda.length;
                } else {
                    spriteIndex = (spriteIndex + 1) % spritesDireita.length;
                }
            }
        }
    }

    // Desenha o sprite atual na posição x e y
    public void draw(Canvas canvas) {
        Bitmap spriteAtual = direcaoAtualEsquerda
                ? spritesEsquerda[spriteIndex]
                : spritesDireita[spriteIndex];

        canvas.drawBitmap(spriteAtual,
                x - spriteAtual.getWidth() / 2,
                y - spriteAtual.getHeight() / 2,
                null);
    }

    // Retorna coordenadas e raio do jogador (usado para colisões)
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}

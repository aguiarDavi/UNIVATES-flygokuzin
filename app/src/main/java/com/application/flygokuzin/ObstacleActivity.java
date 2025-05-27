package com.application.flygokuzin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

public class ObstacleActivity {

    private float x, y, width, height, speed;
    private float screenHeight;
    private Bitmap imagem;

    public ObstacleActivity(Context context, float startX, float screenHeight) {
        this.x = startX;
        this.y = 0;
        this.width = 180;
        this.height = 180;
        this.speed = 10;
        this.screenHeight = screenHeight;

        // Lista de imagens disponíveis
        int[] imagens = {
                R.drawable.kuririn,
                R.drawable.majin_boo,
                R.drawable.piccolo,
                R.drawable.vegeta,
                R.drawable.cell,
                R.drawable.freeza_dourado,
                R.drawable.android_20
        };

        // Escolhe uma imagem aleatória
        int index = new Random().nextInt(imagens.length);
        imagem = BitmapFactory.decodeResource(context.getResources(), imagens[index]);
        imagem = Bitmap.createScaledBitmap(imagem, (int) width, (int) height, false);
    }

    public void update() {
        y += speed;
    }

    public boolean isOffScreen() {
        return y > screenHeight;
    }

    public void draw(Canvas canvas) {
        if (imagem != null) {
            canvas.drawBitmap(imagem, x, y, null);
        }
    }

    public boolean colideCom(PlayerActivity player) {
        float px = player.getX();
        float py = player.getY();
        float pr = player.getRadius();

        RectF rect = new RectF(x, y, x + width, y + height);
        return rect.contains(px, py);
    }

    public void aumentarVelocidade(float fator) {
        speed *= fator;
    }
}

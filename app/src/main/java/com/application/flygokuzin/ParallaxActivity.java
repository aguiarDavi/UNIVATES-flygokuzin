package com.application.flygokuzin;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ParallaxActivity {

    private Bitmap image;
    private float speed;
    private int screenHeight;

    private float y1, y2;

    public ParallaxActivity(Bitmap image, float speed, int screenHeight) {
        this.image = Bitmap.createScaledBitmap(image, image.getWidth(), screenHeight, true);
        this.speed = speed;
        this.screenHeight = screenHeight;

        y1 = 0;
        y2 = -screenHeight; // Começa fora da tela para cima
    }

    public void update() {
        y1 += speed;
        y2 += speed;

        // Reinicia posições para loop
        if (y1 >= screenHeight) {
            y1 = y2 - screenHeight;
        }

        if (y2 >= screenHeight) {
            y2 = y1 - screenHeight;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, 0, y1, null);
        canvas.drawBitmap(image, 0, y2, null);
    }
}

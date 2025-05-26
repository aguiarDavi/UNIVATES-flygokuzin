package com.application.flygokuzin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ObstacleActivity {

    private float x, y, width, height, speed;
    private float screenHeight;
    private Paint paint;
    private RectF rect = new RectF();

    public ObstacleActivity(Context context, float startX, float screenHeight) {
        this.x = startX;
        this.y = 0;
        this.width = 100;
        this.height = 100;
        this.speed = 10;
        this.screenHeight = screenHeight;

        paint = new Paint();
        paint.setColor(0xFFFF0000);
    }

    public void update() {
        y += speed;
    }

    public boolean isOffScreen() {
        return y > screenHeight;
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public boolean colideCom(PlayerActivity player) {
        rect.set(x, y, x + width, y + height);
        return rect.contains(player.getX(), player.getY());
    }

    public void aumentarVelocidade(float fator) {
        speed *= fator;
    }
}

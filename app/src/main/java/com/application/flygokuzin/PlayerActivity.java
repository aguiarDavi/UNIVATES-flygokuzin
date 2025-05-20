package com.application.flygokuzin;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PlayerActivity {
    private float x, y, radius;
    private Paint paint;
    private Canvas canvas;

    public PlayerActivity(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public void update(float sensorX) {
        x -= sensorX * 5;
        if (x < radius) {
            x = radius;
        }
        if (x > canvas.getWidth() - radius) {
            x = canvas.getWidth() - radius;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public float getRadius() { return radius; }
}

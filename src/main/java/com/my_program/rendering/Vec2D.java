package com.my_program.rendering;

public class Vec2D {
    private float x;
    private float y;

    public Vec2D(float num) {
        this.x = num;
        this.y = num;
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vec2D clone () {
        return new Vec2D(x, y);
    }

    //блок сетерів і гетерів
    public float x () {
        return this.x;
    }

    public float y () {
        return this.y;
    }

    public void x (float X) {
        this.x = X;
    }

    public void y (float Y) {
        this.y = Y;
    }

    public Vec2D add (Vec2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vec2D add (float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Vec2D add (Vec2D first, Vec2D second) {
       return new Vec2D(first.x + second.x, first.y + second.y);
    }

    public Vec2D sub (Vec2D other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vec2D sub (float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Vec2D sub (Vec2D first, Vec2D second) {
        return new Vec2D(first.x - second.x, first.y - second.y);
    }

    public Vec2D mult (Vec2D other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }

    public Vec2D mult (float num) {
        this.x *= num;
        this.y *= num;
        return this;
    }

    public static Vec2D mult (Vec2D first, Vec2D second) {
        return new Vec2D(first.x * second.x, first.y * second.y);
    }

    public static Vec2D mult (Vec2D first, float num) {
        return new Vec2D(first.x * num, first.y * num);
    }

    public Vec2D div (float num) {
        this.x /= num;
        this.y /= num;
        return this;
    }

    //функція для малювання точки в кординатах відповідному цьому вектору
    //використовувалась при написанні коду
    @Deprecated
    public void draw (int color) {
        if (this.x >= 0 && this.x < GS.getScreenWidth() &&
        this.y >= 0 && this.y < GS.getScreenHeight()) {
            int pixelID = (int) this.y * GS.getScreenWidth() + (int) this.x;
            GS.pixels[pixelID] = color;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

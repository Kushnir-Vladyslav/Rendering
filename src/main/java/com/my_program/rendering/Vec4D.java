package com.my_program.rendering;

public class Vec4D {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vec4D(float num) {
        this.x = num;
        this.y = num;
        this.z = num;
        this.w = num;
    }

    public Vec4D(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    @Override
    public Vec4D clone () {
        return new Vec4D(x, y, z, w);
    }

    public Vec4D add (float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vec4D add (Vec4D other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }

    public Vec4D sub (float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    //змінює знак всіх змінних
    public Vec4D sub (Vec4D other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
        return this;
    }

    public Vec4D negative () {
        return new Vec4D(
                -this.x,
                -this.y,
                -this.z
        );
    }

    // довжина вектору
    public float length () {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    // нормалізація вектору
    public Vec4D normalize () {
        float Length = this.length();

        this.x /= Length;
        this.y /= Length;
        this.z /= Length;

        return this;
    }

    static public Vec4D normalize (Vec4D Vector) {
        float Length = Vector.length();

        float X = Vector.x() / Length;
        float Y = Vector.y() / Length;
        float Z = Vector.z() / Length;

        return new Vec4D(X, Y, Z);
    }

    static public Vec4D add (Vec4D First, Vec4D Second) {
        return new Vec4D(First.x + Second.x,
                First.y + Second.y,
                First.z + Second.z,
                First.w + Second.w
        );
    }

    public Vec4D mult (float num) {
        this.x *= num;
        this.y *= num;
        this.z *= num;
        this.w *= num;
        return this;
    }

    public static Vec4D mult (Vec4D vector, float num) {
        return new Vec4D(vector.x * num, vector.y * num, vector.z * num, vector.w * num);
    }

    public Vec4D div (float num) {
        this.x /= num;
        this.y /= num;
        this.z /= num;
        this.w /= num;
        return this;
    }

    public Vec4D div3D(float num) {
        this.x /= num;
        this.y /= num;
        this.z /= num;
        return this;
    }

    public static Vec4D div3D(Vec4D vector, float num) {
        return new Vec4D(vector.x / num, vector.y / num, vector.z / num, vector.w / num);
    }

    //для використання його як вектора та кординат чотирьох вимірного точки
    public float x () {
        return this.x;
    }

    public float y () {
        return this.y;
    }

    public float z () {
        return this.z;
    }

    public float w () {
        return this.w;
    }

    //для використання його як кольора що містить RGBA в діапазоні 0...1
    public float r () {
        return this.x;
    }

    public float g () {
        return this.y;
    }

    public float b () {
        return this.z;
    }

    public float a () {
        return this.w;
    }

    //проектування трьовимірної точки на двовимірну площину екрану
    //використовувалась, до введення матриці перспективи
    //її замінила функція "NdcToPixels"
    @Deprecated
    public Vec2D Perspective () {
        Vec2D result = new Vec2D(this.x / this.z, -this.y / this.z);
        result.add(new Vec2D(1, 1)).mult(0.5f);
        return result.mult(new Vec2D(GS.getScreenWidth(), GS.getScreenHeight()));
    }

    //проектування трьовимірної точки на двовимірну площину екрану
    public Vec2D NdcToPixels () {
        Vec2D result = new Vec2D(this.x, -this.y);
        result.add(new Vec2D(1, 1)).mult(0.5f);
        return result.mult(new Vec2D(GS.getScreenWidth(), GS.getScreenHeight()));
    }

    //функція для  проектування та малювання точки в кординатах відповідному цьому вектору
    public void draw (int color) {
        Perspective().draw(color);
    }
}

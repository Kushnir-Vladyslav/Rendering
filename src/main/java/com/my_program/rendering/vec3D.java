package com.my_program.rendering;

// замінено на вектор розмірністю 4, який може виконувати роль 3-х мірного вектору
@Deprecated
public class vec3D {
    private float x;
    private float y;
    private float z;

    public vec3D(float num) {
        this.x = num;
        this.y = num;
        this.z = num;
    }

    public vec3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public vec3D add (float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public vec3D add (vec3D other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public vec3D mult (float num) {
        this.x *= num;
        this.y *= num;
        this.z *= num;
        return this;
    }

    public static vec3D mult (vec3D vector, float num) {
        return new vec3D(vector.x * num, vector.y * num, vector.z * num);
    }

    //для використання його як вектора та кординат тривимірної точки
    public float x () {
        return this.x;
    }

    public float y () {
        return this.y;
    }

    public float z () {
        return this.z;
    }

    //для використання його як кольора що містить RGB в діапазоні 0...1
    public float r () {
        return this.x;
    }

    public float g () {
        return this.y;
    }

    public float b () {
        return this.z;
    }

    public Vec2D xy () {
        return new Vec2D(this.x, this.y);
    }

    //проектування трьовимірної точки на двовимірну площину екрану
    public Vec2D Perspective () {
        Vec2D result = new Vec2D(this.x / this.z, -this.y / this.z);
        result.add(new Vec2D(1, 1)).mult(0.5f);
        return result.mult(new Vec2D(GS.getScreenWidth(), GS.getScreenHeight()));
    }

    //функція для  проектування та малювання точки в кординатах відповідному цьому вектору
    public void draw (int color) {
        Perspective().draw(color);
    }
}

package com.my_program.rendering;

public class Matrix4D {

    // матриця буде представлення 4-мя 4-х мірними векторами, за схемою Ax...AwBx...BwCx...Cw...
    // тобто в кожному векторі буде один стовпчик
    private Vec4D[] V;

    public Matrix4D(Vec4D A, Vec4D B, Vec4D C, Vec4D D) {
        V = new Vec4D[] {A.clone(), B.clone(), C.clone(), D.clone()};
    }

    //творення транспонованої (оберненної) матриці, з одиничнихх векторів
    static public Matrix4D transposedNormalizedMatrix (Vec4D Right, Vec4D Up, Vec4D LookAt) {
        return new Matrix4D(
                new Vec4D(Right.x(), Up.x(), LookAt.x(), 0f),
                new Vec4D(Right.y(), Up.y(), LookAt.y(), 0f),
                new Vec4D(Right.z(), Up.z(), LookAt.z(), 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );
    }

    //матриця проекції, що відповідає за кут огляду та правельне відображення при різних пропорція екрану
    static public Matrix4D PerspectiveMatrix (float ViewAngle, float AspectRatio, float NeatZ, float FarZ) {
        float R1С1 = 1.f / (AspectRatio * (float) Math.tan(Math.toRadians(ViewAngle) / 2));
        float R2С2 = 1.f /  (float) Math.tan(Math.toRadians(ViewAngle) / 2);
        float R3C3 = -FarZ / (NeatZ - FarZ);
        float R4C3 = NeatZ * FarZ / (NeatZ - FarZ);

        return new Matrix4D(
                new Vec4D(R1С1, 0f, 0f, 0f),
                new Vec4D(0f, R2С2, 0f, 0f),
                new Vec4D(0f, 0f, R3C3, 1f),
                new Vec4D(0f, 0f, R4C3, 0f)
        );
    }

    public static Matrix4D identityMatrix4 (){
        return new Matrix4D(
                new Vec4D(1f, 0f, 0f, 0f),
                new Vec4D(0f, 1f, 0f, 0f),
                new Vec4D(0f, 0f, 1f, 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );
    }

    // матриця зміни розміру
    public static Matrix4D scaleMatrix4 (float X, float Y, float Z){
        return new Matrix4D(
                new Vec4D(X, 0f, 0f, 0f),
                new Vec4D(0f, Y, 0f, 0f),
                new Vec4D(0f, 0f, Z, 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );
    }
    public static Matrix4D scaleMatrix4 (Vec4D Vector){
        return scaleMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця преміщення
    public static Matrix4D translationMatrix4 (float X, float Y, float Z){
        return new Matrix4D(
                new Vec4D(1f, 0f, 0f, 0f),
                new Vec4D(0f, 1f, 0f, 0f),
                new Vec4D(0f, 0f, 1f, 0f),
                new Vec4D(X, Y, Z, 1f)
        );
    }

    public static Matrix4D translationMatrix4 (Vec4D Vector){
        return translationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця обертання
    // x, y, z це кути
    public static Matrix4D rotationMatrix4 (float X, float Y, float Z){
        Matrix4D RotationX = new Matrix4D(
                new Vec4D(1f, 0f, 0f, 0f),
                new Vec4D(0f, cos(X), sin(X), 0f),
                new Vec4D(0f, -sin(X), cos(X), 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );

        Matrix4D RotationY = new Matrix4D(
                new Vec4D(cos(Y), 0f, sin(Y), 0f),
                new Vec4D(0f, 1f, 0f, 0f),
                new Vec4D(-sin(Y), 0f, cos(Y), 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );

        Matrix4D RotationZ = new Matrix4D(
                new Vec4D(cos(Z), sin(Z), 0f, 0f),
                new Vec4D(-sin(Z), cos(Z), 0f, 0f),
                new Vec4D(0f, 0f, 1f, 0f),
                new Vec4D(0f, 0f, 0f, 1f)
        );

        return RotationZ.mult(RotationY).mult(RotationX);
    }

    public static Matrix4D rotationMatrix4 (Vec4D Vector){
        return rotationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    @Override
    public Matrix4D clone () {
        return new Matrix4D(
                this.V[0].clone(),
                this.V[1].clone(),
                this.V[2].clone(),
                this.V[3].clone()
        );
    }

    public Vec4D mult (Vec4D Vector) {
        Vec4D S1 = Vec4D.mult(this.V[0].clone(), Vector.x());
        Vec4D S2 = Vec4D.mult(this.V[1].clone(), Vector.y());
        Vec4D S3 = Vec4D.mult(this.V[2].clone(), Vector.z());
        Vec4D S4 = Vec4D.mult(this.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public static Vec4D mult (Matrix4D Matr, Vec4D Vector) {
        Vec4D S1 = Vec4D.mult(Matr.V[0].clone(), Vector.x());
        Vec4D S2 = Vec4D.mult(Matr.V[1].clone(), Vector.y());
        Vec4D S3 = Vec4D.mult(Matr.V[2].clone(), Vector.z());
        Vec4D S4 = Vec4D.mult(Matr.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public Matrix4D mult (Matrix4D other) {
        this.V[0] = other.clone().mult(this.V[0]);
        this.V[1] = other.clone().mult(this.V[1]);
        this.V[2] = other.clone().mult(this.V[2]);
        this.V[3] = other.clone().mult(this.V[3]);
        return this;
    }

    public static Matrix4D mult (Matrix4D first, Matrix4D second) {
        return new Matrix4D(
                first.clone().mult(second.V[0]),
                first.clone().mult(second.V[1]),
                first.clone().mult(second.V[2]),
                first.clone().mult(second.V[3])
        );
    }

    private static float sin(float num) {
        return (float) Math.sin(Math.toRadians(num));
    }

    private static float cos(float num) {
        return (float) Math.cos(Math.toRadians(num));
    }
}

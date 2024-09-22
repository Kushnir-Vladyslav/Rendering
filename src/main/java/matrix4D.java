

public class matrix4D {

    // матриця буде представлення 4-мя 4-х мірними векторами, за схемою Ax...AwBx...BwCx...Cw...
    // тобто в кожному векторі буде один стовпчик
    private vec4D[] V;

    public matrix4D(vec4D A, vec4D B, vec4D C, vec4D D) {
        V = new vec4D[] {A.clone(), B.clone(), C.clone(), D.clone()};
    }

    //творення транспонованої (оберненної) матриці, з одиничнихх векторів
    static public matrix4D transposedNormalizedMatrix (vec4D Right, vec4D Up, vec4D LookAt) {
        return new matrix4D(
                new vec4D(Right.x(), Up.x(), LookAt.x(), 0f),
                new vec4D(Right.y(), Up.y(), LookAt.y(), 0f),
                new vec4D(Right.z(), Up.z(), LookAt.z(), 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );
    }

    //матриця проекції, що відповідає за кут огляду та правельне відображення при різних пропорція екрану
    static public matrix4D PerspectiveMatrix (float ViewAngle, float AspectRatio, float NeatZ, float FarZ) {
        float R1С1 = 1.f / (AspectRatio * (float) Math.tan(Math.toRadians(ViewAngle) / 2));
        float R2С2 = 1.f /  (float) Math.tan(Math.toRadians(ViewAngle) / 2);
        float R3C3 = -FarZ / (NeatZ - FarZ);
        float R4C3 = NeatZ * FarZ / (NeatZ - FarZ);

        return new matrix4D(
                new vec4D(R1С1, 0f, 0f, 0f),
                new vec4D(0f, R2С2, 0f, 0f),
                new vec4D(0f, 0f, R3C3, 1f),
                new vec4D(0f, 0f, R4C3, 0f)
        );
    }

    public static matrix4D identityMatrix4 (){
        return new matrix4D(
                new vec4D(1f, 0f, 0f, 0f),
                new vec4D(0f, 1f, 0f, 0f),
                new vec4D(0f, 0f, 1f, 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );
    }

    // матриця зміни розміру
    public static matrix4D scaleMatrix4 (float X, float Y, float Z){
        return new matrix4D(
                new vec4D(X, 0f, 0f, 0f),
                new vec4D(0f, Y, 0f, 0f),
                new vec4D(0f, 0f, Z, 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );
    }
    public static matrix4D scaleMatrix4 (vec4D Vector){
        return scaleMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця преміщення
    public static matrix4D translationMatrix4 (float X, float Y, float Z){
        return new matrix4D(
                new vec4D(1f, 0f, 0f, 0f),
                new vec4D(0f, 1f, 0f, 0f),
                new vec4D(0f, 0f, 1f, 0f),
                new vec4D(X, Y, Z, 1f)
        );
    }

    public static matrix4D translationMatrix4 (vec4D Vector){
        return translationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця обертання
    // x, y, z це кути
    public static matrix4D rotationMatrix4 (float X, float Y, float Z){
        matrix4D RotationX = new matrix4D(
                new vec4D(1f, 0f, 0f, 0f),
                new vec4D(0f, cos(X), sin(X), 0f),
                new vec4D(0f, -sin(X), cos(X), 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );

        matrix4D RotationY = new matrix4D(
                new vec4D(cos(Y), 0f, sin(Y), 0f),
                new vec4D(0f, 1f, 0f, 0f),
                new vec4D(-sin(Y), 0f, cos(Y), 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );

        matrix4D RotationZ = new matrix4D(
                new vec4D(cos(Z), sin(Z), 0f, 0f),
                new vec4D(-sin(Z), cos(Z), 0f, 0f),
                new vec4D(0f, 0f, 1f, 0f),
                new vec4D(0f, 0f, 0f, 1f)
        );

        return RotationZ.mult(RotationY).mult(RotationX);
    }

    public static matrix4D rotationMatrix4 (vec4D Vector){
        return rotationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    @Override
    public matrix4D clone () {
        return new matrix4D(
                this.V[0].clone(),
                this.V[1].clone(),
                this.V[2].clone(),
                this.V[3].clone()
        );
    }

    public vec4D mult (vec4D Vector) {
        vec4D S1 = vec4D.mult(this.V[0].clone(), Vector.x());
        vec4D S2 = vec4D.mult(this.V[1].clone(), Vector.y());
        vec4D S3 = vec4D.mult(this.V[2].clone(), Vector.z());
        vec4D S4 = vec4D.mult(this.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public static vec4D mult (matrix4D Matr, vec4D Vector) {
        vec4D S1 = vec4D.mult(Matr.V[0].clone(), Vector.x());
        vec4D S2 = vec4D.mult(Matr.V[1].clone(), Vector.y());
        vec4D S3 = vec4D.mult(Matr.V[2].clone(), Vector.z());
        vec4D S4 = vec4D.mult(Matr.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public matrix4D mult (matrix4D other) {
        this.V[0] = other.clone().mult(this.V[0]);
        this.V[1] = other.clone().mult(this.V[1]);
        this.V[2] = other.clone().mult(this.V[2]);
        this.V[3] = other.clone().mult(this.V[3]);
        return this;
    }

    public static matrix4D mult (matrix4D first, matrix4D second) {
        return new matrix4D(
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

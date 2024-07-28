import java.rmi.MarshalException;

public class Matrix4 {

    // матриця буде представлення 4-мя 4-х мірними векторами, за схемою Ax...AwBx...BwCx...Cw...
    // тобто в кожному векторі буде один стовпчик
    private Vec4[] V;

    public Matrix4 (Vec4 A, Vec4 B, Vec4 C, Vec4 D) {
        V = new Vec4[] {A.clone(), B.clone(), C.clone(), D.clone()};
    }

    public static Matrix4 identityMatrix4 (){
        return new Matrix4(
                new Vec4(1f, 0f, 0f, 0f),
                new Vec4(0f, 1f, 0f, 0f),
                new Vec4(0f, 0f, 1f, 0f),
                new Vec4(0f, 0f, 0f, 1f)
        );
    }

    // матриця зміни розміру
    public static Matrix4 scaleMatrix4 (float X, float Y, float Z){
        return new Matrix4(
                new Vec4(X, 0f, 0f, 0f),
                new Vec4(0f, Y, 0f, 0f),
                new Vec4(0f, 0f, Z, 0f),
                new Vec4(0f, 0f, 0f, 1f)
        );
    }
    public static Matrix4 scaleMatrix4 (Vec4 Vector){
        return scaleMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця преміщення
    public static Matrix4 translationMatrix4 (float X, float Y, float Z){
        return new Matrix4(
                new Vec4(1f, 0f, 0f, 0f),
                new Vec4(0f, 1f, 0f, 0f),
                new Vec4(0f, 0f, 1f, 0f),
                new Vec4(X, Y, Z, 1f)
        );
    }

    public static Matrix4 translationMatrix4 (Vec4 Vector){
        return translationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    // матриця обертання
    // x, y, z це кути
    public static Matrix4 rotationMatrix4 (float X, float Y, float Z){
        Matrix4 RotationX = new Matrix4(
                new Vec4(1f, 0f, 0f, 0f),
                new Vec4(0f, cos(X), sin(X), 0f),
                new Vec4(0f, -sin(X), cos(X), 0f),
                new Vec4(0f, 0f, 0f, 1f)
        );

        Matrix4 RotationY = new Matrix4(
                new Vec4(cos(Y), 0f, sin(Y), 0f),
                new Vec4(0f, 1f, 0f, 0f),
                new Vec4(-sin(Y), 0f, cos(Y), 0f),
                new Vec4(0f, 0f, 0f, 1f)
        );

        Matrix4 RotationZ = new Matrix4(
                new Vec4(cos(Z), sin(Z), 0f, 0f),
                new Vec4(-sin(Z), cos(Z), 0f, 0f),
                new Vec4(0f, 0f, 1f, 0f),
                new Vec4(0f, 0f, 0f, 1f)
        );

        return RotationZ.mult(RotationY).mult(RotationX);
    }

    public static Matrix4 rotationMatrix4 (Vec4 Vector){
        return rotationMatrix4(Vector.x(), Vector.y(),Vector.z());
    }

    @Override
    public Matrix4 clone () {
        return new Matrix4(
                this.V[0].clone(),
                this.V[1].clone(),
                this.V[2].clone(),
                this.V[3].clone()
        );
    }

    public Vec4 mult (Vec4 Vector) {
        Vec4 S1 = Vec4.mult(this.V[0].clone(), Vector.x());
        Vec4 S2 = Vec4.mult(this.V[1].clone(), Vector.y());
        Vec4 S3 = Vec4.mult(this.V[2].clone(), Vector.z());
        Vec4 S4 = Vec4.mult(this.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public static Vec4 mult (Matrix4 Matr, Vec4 Vector) {
        Vec4 S1 = Vec4.mult(Matr.V[0].clone(), Vector.x());
        Vec4 S2 = Vec4.mult(Matr.V[1].clone(), Vector.y());
        Vec4 S3 = Vec4.mult(Matr.V[2].clone(), Vector.z());
        Vec4 S4 = Vec4.mult(Matr.V[3].clone(), Vector.w());
        return S1.add(S2).add(S3).add(S4);
    }

    public Matrix4 mult (Matrix4 other) {
        this.V[0] = other.clone().mult(this.V[0]);
        this.V[1] = other.clone().mult(this.V[1]);
        this.V[2] = other.clone().mult(this.V[2]);
        this.V[3] = other.clone().mult(this.V[3]);
        return this;
    }

    public static Matrix4 mult (Matrix4 first, Matrix4 second) {
        return new Matrix4 (
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

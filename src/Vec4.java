import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Vec4 {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vec4 (float num) {
        this.x = num;
        this.y = num;
        this.z = num;
        this.w = num;
    }

    public Vec4 (float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4 (Vec3 V) {
        this.x = V.x();
        this.y = V.y();
        this.z = V.z();
        this.w = 1;
    }

    public Vec4 (float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vec3 toVec3 () {
        return new Vec3(this.x, this.y, this.z);
    }

    @Override
    public Vec4 clone () {
        return new Vec4(x, y, z, w);
    }

    public Vec4 add (float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vec4 add (Vec4 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }

    static public Vec4 add (Vec4 First, Vec4 Second) {
        return new Vec4(First.x + Second.x,
                First.y + Second.y,
                First.z + Second.z,
                First.w + Second.w
        );
    }

    public Vec4 mult (float num) {
        this.x *= num;
        this.y *= num;
        this.z *= num;
        this.w *= num;
        return this;
    }

    public static Vec4 mult (Vec4 vector, float num) {
        return new Vec4(vector.x * num, vector.y * num, vector.z * num, vector.w * num);
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
    public Vec2 Perspective () {
        Vec2 result = new Vec2(this.x / this.z, -this.y / this.z);
        result.add(new Vec2(1, 1)).mult(0.5f);
        return result.mult(new Vec2(GlobalState.getScreenWidth(), GlobalState.getScreenHeight()));
    }

    //функція для  проектування та малювання точки в кординатах відповідному цьому вектору
    public void draw (int color) {
        Perspective().draw(color);
    }
}

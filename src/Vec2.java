public class Vec2 {
    private float x;
    private float y;

    public Vec2 (float num) {
        this.x = num;
        this.y = num;
    }

    public Vec2 (float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x () {
        return this.x;
    }

    public float y () {
        return this.y;
    }

    public Vec2 add (Vec2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public static Vec2 add (Vec2 first, Vec2 second) {
       return new Vec2(first.x + second.x, first.y + second.y);
    }

    public Vec2 mult (Vec2 other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }

    public Vec2 mult (float num) {
        this.x *= num;
        this.y *= num;
        return this;
    }

    public static Vec2 mult (Vec2 first, Vec2 second) {
        return new Vec2(first.x * second.x, first.y * second.y);
    }

    public Vec2 div (float num) {
        this.x /= num;
        this.y /= num;
        return this;
    }
}

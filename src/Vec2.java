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

    public void x (float X) {
        this.x = X;
    }

    public void y (float Y) {
        this.y = Y;
    }

    public Vec2 add (Vec2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vec2 add (float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Vec2 add (Vec2 first, Vec2 second) {
       return new Vec2(first.x + second.x, first.y + second.y);
    }

    public Vec2 sub (Vec2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vec2 sub (float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Vec2 sub (Vec2 first, Vec2 second) {
        return new Vec2(first.x - second.x, first.y - second.y);
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

    //функція для малювання точки в кординатах відповідному цьому вектору
    public void draw (int color) {
        if (this.x >= 0 && this.x < GlobalState.getScreenWidth() &&
        this.y >= 0 && this.y < GlobalState.getScreenHeight()) {
            int pixelID = (int) this.y * GlobalState.getScreenWidth() + (int) this.x;
            GlobalState.Pixels[pixelID] = color;
        }
    }
}

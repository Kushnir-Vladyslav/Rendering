public class vec2D {
    private float x;
    private float y;

    public vec2D(float num) {
        this.x = num;
        this.y = num;
    }

    public vec2D(float x, float y) {
        this.x = x;
        this.y = y;
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

    public vec2D add (vec2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public vec2D add (float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static vec2D add (vec2D first, vec2D second) {
       return new vec2D(first.x + second.x, first.y + second.y);
    }

    public vec2D sub (vec2D other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public vec2D sub (float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static vec2D sub (vec2D first, vec2D second) {
        return new vec2D(first.x - second.x, first.y - second.y);
    }

    public vec2D mult (vec2D other) {
        this.x *= other.x;
        this.y *= other.y;
        return this;
    }

    public vec2D mult (float num) {
        this.x *= num;
        this.y *= num;
        return this;
    }

    public static vec2D mult (vec2D first, vec2D second) {
        return new vec2D(first.x * second.x, first.y * second.y);
    }

    public static vec2D mult (vec2D first, float num) {
        return new vec2D(first.x * num, first.y * num);
    }

    public vec2D div (float num) {
        this.x /= num;
        this.y /= num;
        return this;
    }

    //функція для малювання точки в кординатах відповідному цьому вектору
    //використовувалась при написанні коду
    @Deprecated
    public void draw (int color) {
        if (this.x >= 0 && this.x < GlobalState.getScreenWidth() &&
        this.y >= 0 && this.y < GlobalState.getScreenHeight()) {
            int pixelID = (int) this.y * GlobalState.getScreenWidth() + (int) this.x;
            GlobalState.Pixels[pixelID] = color;
        }
    }
}

public class Vec3 {
    private float x;
    private float y;
    private float z;

    public Vec3 (float num) {
        this.x = num;
        this.y = num;
        this.z = num;
    }

    public Vec3 (float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add (float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3 add (Vec3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public float x () {
        return this.x;
    }

    public float y () {
        return this.y;
    }

    public float z () {
        return this.z;
    }

    public Vec2 xy () {
        return new Vec2(this.x, this.y);
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

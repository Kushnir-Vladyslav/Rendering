public class Triangle3 {
    public Vec3 p1;
    public Vec3 p2;
    public Vec3 p3;

    public Triangle3(Vec3 p1, Vec3 p2, Vec3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Vec2 getPoint2 (int num) {
        if (num == 0) return p1.Prespective();
        if (num == 1) return p2.Prespective();
        if (num == 2) return p3.Prespective();
        return new Vec2(0);
    }

    public Vec3 getPoint3 (int num) {
        if (num == 0) return p1;
        if (num == 1) return p2;
        if (num == 2) return p3;
        return new Vec3(0);
    }
}

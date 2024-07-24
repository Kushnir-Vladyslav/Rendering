public class Triangle3 {
    public Vec3 Point1;
    public Vec3 Point2;
    public Vec3 Point3;

    public Vec3[] Colors;

    public Triangle3(Vec3 P1, Vec3 P2, Vec3 P3, Vec3[] Colors) {
        this.Point1 = P1;
        this.Point2 = P2;
        this.Point3 = P3;

        this.Colors = Colors;
    }

    public Vec2 getPoint2 (int num) {
        if (num == 0) return Point1.Perspective();
        if (num == 1) return Point2.Perspective();
        if (num == 2) return Point3.Perspective();
        return new Vec2(0);
    }

    public Vec3 getPoint3 (int num) {
        if (num == 0) return Point1;
        if (num == 1) return Point2;
        if (num == 2) return Point3;
        return new Vec3(0);
    }

    //малювання трикутника заповнюючи його одним певним кольором
    public void draw () {
        new Triangle2(Point1.Perspective(), Point2.Perspective(), Point3.Perspective(),
                Colors,
                new float[] {Point1.z(), Point2.z(), Point3.z()}).draw();
    }
}

public class Camera {
    Vec4 Position;
    Vec4 Direction;

    Camera () {
        this.Position = new Vec4(0, 0, 1);
        this.Direction = new Vec4(0, 0, 1);
    }

    Camera (Vec4 Position, Vec4 Direction) {
        this.Position = Position;
        this.Direction = Direction;
    }

    public Vec4 right() {
        return new Vec4(1, 0, 0);
    }

    public Matrix4 getCameraTransform () {
        return Matrix4.translationMatrix4(Position.negative());
    }

}

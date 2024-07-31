public class Camera {
    public Vec4 Position;
    public Matrix4 Direction = Matrix4.identityMatrix4();

    public Vec4 Right = new Vec4(1, 0, 0);
    public Vec4 LookAt = new Vec4(0, 0, 1);

    private Vec2 PreviousMousePosition = new Vec2(0.f, 0.f);

    private float Yaw = 0.f;
    private float Pitch = 0.f;

    Camera () {
        this.Position = new Vec4(0, 0, 1);
    }

    Camera (Vec4 Position, float Yaw, float Pitch) {
        this.Position = Position;
        this.Yaw = Yaw;
        this.Pitch = Pitch;
    }

    public Vec4 right() {
        return Right.clone();
    }

    public Vec4 lookAt() {
        return LookAt.clone();
    }

    public Matrix4 getCameraMoving () {
        return Matrix4.translationMatrix4(Position.negative());
    }

    public Matrix4 getCameraRotation () {
        return Direction.clone();
    }

    public Matrix4 getCameraTransform () {
        return Matrix4.mult(Direction, getCameraMoving());
//        return getCameraMoving().mult(Direction.clone());
    }

    public void MousePressed (float X, float Y) {
        this.PreviousMousePosition.x(X);
        this.PreviousMousePosition.y(Y);
    }

    public void MouseMoved (float X, float Y) {
        Pitch += (Y - PreviousMousePosition.y()) * 100;
        Yaw += (X - PreviousMousePosition.x()) * 100;

        this.PreviousMousePosition.x(X);
        this.PreviousMousePosition.y(Y);

        updateDirection();
    }

    private void updateDirection () {
        Matrix4 YawTransform = Matrix4.rotationMatrix4( 0, Yaw, 0);
        Matrix4 PitchTransform = Matrix4.rotationMatrix4( Pitch, 0, 0);
//        Matrix4 CameraTransform = PitchTransform.mult(YawTransform);
        Matrix4 CameraTransform = YawTransform.mult(PitchTransform);

        this.Right = Matrix4.mult(CameraTransform, new Vec4(1, 0, 0, 0)).normalize();
        Vec4 Up = Matrix4.mult(CameraTransform, new Vec4(0, 1, 0, 0)).normalize();
        this.LookAt = Matrix4.mult(CameraTransform, new Vec4(0, 0, 1, 0)).normalize();

        Direction = Matrix4.transposedNormalizedMatrix(this.Right, Up, this.LookAt);
    }

}

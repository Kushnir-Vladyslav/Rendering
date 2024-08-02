public class Camera {
    public Vec4 Position;
    public Matrix4 Direction = Matrix4.identityMatrix4();

    public Vec4 Right = new Vec4(1, 0, 0);
    public Vec4 LookAt = new Vec4(0, 0, 1);

    private Vec2 PreviousMousePosition = new Vec2(0.f, 0.f);

    private float Yaw = 0.f;
    private float Pitch = 0.f;

    //параметри дял перспективи
    public Matrix4 PerspectiveMatrix;

    public float ViewAngle = 120;
    public float NeatZ = 0.05f;
    public float FarZ = 2f;

    Camera () {
        this.Position = new Vec4(0, 0, 1);
        this.updatePerspective();
    }

    Camera (Vec4 Position, float Yaw, float Pitch) {
        this.Position = Position;
        this.Yaw = Yaw;
        this.Pitch = Pitch;
        updatePerspective();
    }

    public Vec4 right() {
        return Right.clone();
    }

    public Vec4 lookAt() {
        return LookAt.clone();
    }

    //трасформація камери, преміщення та обертання
    public Matrix4 getCameraMoving () {
        return Matrix4.translationMatrix4(Position.negative());
    }

    public Matrix4 getCameraRotation () {
        return Direction.clone();
    }

    //сумарна трансформація камери, послідовність: обертання, переміщення
    public Matrix4 getCameraTransform () {
        return Matrix4.mult(Direction, getCameraMoving());
    }

    //перспектива
    //встановлення кута огляду
    public void setViewAngle (float Angle) {
        this.ViewAngle = Angle;
    }

    //встановлення ближньої межі відмальовування
    public void setNeatZ (float Near) {
        if (Near < this.FarZ) {
            this.NeatZ = Near;
        }
    }

    //встановлення дальної межі відмальовування
    public void setFarZ (float Far) {
        if (Far > this.NeatZ) {
            this.FarZ = Far;
        }
    }

    //Оновлення матриці перспективи
    public void updatePerspective () {
        float AcpectRatio = (float) GlobalState.getScreenWidth() / (float) GlobalState.getScreenHeight();
        this.PerspectiveMatrix = Matrix4.PerspectiveMatrix(ViewAngle, AcpectRatio, NeatZ, FarZ);
    }

    //повернення матриці перспективи
    public Matrix4 getPerspectiveMatrix () {
        return this.PerspectiveMatrix;
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

    //Створненя матриці офертання всіх обєктів в координатах світу в відповідності до повороту камери
    private void updateDirection () {
        Matrix4 YawTransform = Matrix4.rotationMatrix4( 0, Yaw, 0);
        Matrix4 PitchTransform = Matrix4.rotationMatrix4( Pitch, 0, 0);
//        Matrix4 CameraTransform = PitchTransform.mult(YawTransform);
        Matrix4 CameraTransform = YawTransform.mult(PitchTransform);

        //змінні "Right" та "LookAt" отрібні для руху камерри в правельному напрму
        //в фкнкції що відповідає за зміщення при натисканні клавіш
        this.Right = Matrix4.mult(CameraTransform, new Vec4(1, 0, 0, 0)).normalize();
        Vec4 Up = Matrix4.mult(CameraTransform, new Vec4(0, 1, 0, 0)).normalize();
        this.LookAt = Matrix4.mult(CameraTransform, new Vec4(0, 0, 1, 0)).normalize();

        Direction = Matrix4.transposedNormalizedMatrix(this.Right, Up, this.LookAt);
    }

}

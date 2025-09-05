package com.my_program.rendering;

public class camera {
    public Vec4D Position;
    public Matrix4D Direction = Matrix4D.identityMatrix4();

    public Vec4D Right = new Vec4D(1, 0, 0);
    public Vec4D LookAt = new Vec4D(0, 0, 1);

    private Vec2D PreviousMousePosition = new Vec2D(0.f, 0.f);

    private float Yaw = 0.f;
    private float Pitch = 0.f;

    //параметри дял перспективи
    public Matrix4D PerspectiveMatrix;

    public float ViewAngle = 60;
    public float NeatZ = 0.05f;
    public float FarZ = 10000;

    camera() {
        this.Position = new Vec4D(0, 0, -2);
        this.updatePerspective();
    }

    camera(Vec4D Position, float Yaw, float Pitch) {
        this.Position = Position;
        this.Yaw = Yaw;
        this.Pitch = Pitch;
        updatePerspective();
    }

    public Vec4D right() {
        return Right.clone();
    }

    public Vec4D lookAt() {
        return LookAt.clone();
    }

    //трасформація камери, преміщення та обертання
    public Matrix4D getCameraMoving () {
        return Matrix4D.translationMatrix4(Position.negative());
    }

    public Matrix4D getCameraRotation () {
        return Direction.clone();
    }

    //сумарна трансформація камери, послідовність: обертання, переміщення
    public Matrix4D getCameraTransform () {
        return Matrix4D.mult(Direction, getCameraMoving());
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
        float AcpectRatio = (float) GS.getScreenWidth() / (float) GS.getScreenHeight();
        this.PerspectiveMatrix = Matrix4D.PerspectiveMatrix(ViewAngle, AcpectRatio, NeatZ, FarZ);
    }

    //повернення матриці перспективи
    public Matrix4D getPerspectiveMatrix () {
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
        Matrix4D YawTransform = Matrix4D.rotationMatrix4( 0, Yaw, 0);
        Matrix4D PitchTransform = Matrix4D.rotationMatrix4( Pitch, 0, 0);
        Matrix4D CameraTransform = PitchTransform.mult(YawTransform);

        //змінні "Right" та "LookAt" отрібні для руху камерри в правельному напрму
        //в фкнкції що відповідає за зміщення при натисканні клавіш
        this.Right = Matrix4D.mult(CameraTransform, new Vec4D(1, 0, 0, 0)).normalize();
        Vec4D Up = Matrix4D.mult(CameraTransform, new Vec4D(0, 1, 0, 0)).normalize();
        this.LookAt = Matrix4D.mult(CameraTransform, new Vec4D(0, 0, 1, 0)).normalize();

        Direction = Matrix4D.transposedNormalizedMatrix(this.Right, Up, this.LookAt);
    }

}

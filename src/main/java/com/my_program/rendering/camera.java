package com.my_program.rendering;

public class camera {
    public vec4D Position;
    public matrix4D Direction = matrix4D.identityMatrix4();

    public vec4D Right = new vec4D(1, 0, 0);
    public vec4D LookAt = new vec4D(0, 0, 1);

    private vec2D PreviousMousePosition = new vec2D(0.f, 0.f);

    private float Yaw = 0.f;
    private float Pitch = 0.f;

    //параметри дял перспективи
    public matrix4D PerspectiveMatrix;

    public float ViewAngle = 60;
    public float NeatZ = 0.05f;
    public float FarZ = 10000;

    camera() {
        this.Position = new vec4D(0, 0, 1);
        this.updatePerspective();
    }

    camera(vec4D Position, float Yaw, float Pitch) {
        this.Position = Position;
        this.Yaw = Yaw;
        this.Pitch = Pitch;
        updatePerspective();
    }

    public vec4D right() {
        return Right.clone();
    }

    public vec4D lookAt() {
        return LookAt.clone();
    }

    //трасформація камери, преміщення та обертання
    public matrix4D getCameraMoving () {
        return matrix4D.translationMatrix4(Position.negative());
    }

    public matrix4D getCameraRotation () {
        return Direction.clone();
    }

    //сумарна трансформація камери, послідовність: обертання, переміщення
    public matrix4D getCameraTransform () {
        return matrix4D.mult(Direction, getCameraMoving());
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
        this.PerspectiveMatrix = matrix4D.PerspectiveMatrix(ViewAngle, AcpectRatio, NeatZ, FarZ);
    }

    //повернення матриці перспективи
    public matrix4D getPerspectiveMatrix () {
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
        matrix4D YawTransform = matrix4D.rotationMatrix4( 0, Yaw, 0);
        matrix4D PitchTransform = matrix4D.rotationMatrix4( Pitch, 0, 0);
        matrix4D CameraTransform = PitchTransform.mult(YawTransform);

        //змінні "Right" та "LookAt" отрібні для руху камерри в правельному напрму
        //в фкнкції що відповідає за зміщення при натисканні клавіш
        this.Right = matrix4D.mult(CameraTransform, new vec4D(1, 0, 0, 0)).normalize();
        vec4D Up = matrix4D.mult(CameraTransform, new vec4D(0, 1, 0, 0)).normalize();
        this.LookAt = matrix4D.mult(CameraTransform, new vec4D(0, 0, 1, 0)).normalize();

        Direction = matrix4D.transposedNormalizedMatrix(this.Right, Up, this.LookAt);
    }

}

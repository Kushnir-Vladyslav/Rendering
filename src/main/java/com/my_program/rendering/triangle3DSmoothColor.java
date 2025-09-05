package com.my_program.rendering;

//Використовувася для відмальовування трикутників з заданими кольорами на вершинах
//після додванання тестур застарів
@Deprecated
public class triangle3DSmoothColor {
    private Vec4D Point1;
    private Vec4D Point2;
    private Vec4D Point3;

    private Matrix4D Transforms;

    private Vec4D[] Colors;

    public triangle3DSmoothColor(Vec4D P1, Vec4D P2, Vec4D P3, Matrix4D InitialPosition, Vec4D[] Colors) {
        this.Point1 = P1;
        this.Point2 = P2;
        this.Point3 = P3;

        this.Transforms = InitialPosition;

        this.Colors = Colors;
    }

    public void tansform (Matrix4D moveMatrix) {
        this.Transforms.mult(moveMatrix);
    }

    public void newColor(Vec4D[] Colors) {
        this.Colors = Colors;
    }

    public Vec2D getPoint2 (int num) {
        if (num == 0) return Point1.Perspective();
        if (num == 1) return Point2.Perspective();
        if (num == 2) return Point3.Perspective();
        return new Vec2D(0);
    }

    //малювання трикутника заповнюючи його одним певним кольором
    public void draw () {
        Vec4D TransformPoint1 = Transforms.mult(Point1);
        Vec4D TransformPoint2 = Transforms.mult(Point2);
        Vec4D TransformPoint3 = Transforms.mult(Point3);

        TransformPoint1.div3D(TransformPoint1.w());
        TransformPoint2.div3D(TransformPoint2.w());
        TransformPoint3.div3D(TransformPoint3.w());

        // Проектування точок на екран
        Vec2D ProjectionPoint1 = TransformPoint1.NdcToPixels();
        Vec2D ProjectionPoint2 = TransformPoint2.NdcToPixels();
        Vec2D ProjectionPoint3 = TransformPoint3.NdcToPixels();

        //визначення меж в якому розташований трикутник
        //Пошук області в якій розташовано трикутник
        int minX = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));
        int maxX = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));

        int minY = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));
        int maxY = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));

        //перевірка щоб, облатись не виходила за межі екрану
        if (minX < 0) minX = 0;
        if (minY < 0) minY = 0;

        if (maxX >= GS.getScreenWidth()) maxX = GS.getScreenWidth() - 1;
        if (maxY >= GS.getScreenHeight()) maxY = GS.getScreenHeight() - 1;

        //знаходження векторів ребер
        Vec2D Edge1 = Vec2D.sub(ProjectionPoint2, ProjectionPoint1);
        Vec2D Edge2 = Vec2D.sub(ProjectionPoint3, ProjectionPoint2);
        Vec2D Edge3 = Vec2D.sub(ProjectionPoint1, ProjectionPoint3);

        //перевіряємо чи є ребро верхнім лівим, щоб знати чи малювати його
        boolean isTopLeft1 = (Edge1.x() >= 0.f && Edge1.y() > 0.f) || (Edge1.x() > 0.f && Edge1.y() == 0.f);
        boolean isTopLeft2 = (Edge2.x() >= 0.f && Edge2.y() > 0.f) || (Edge2.x() > 0.f && Edge2.y() == 0.f);
        boolean isTopLeft3 = (Edge3.x() >= 0.f && Edge3.y() > 0.f) || (Edge3.x() > 0.f && Edge3.y() == 0.f);

        //спільний дільник для барецентричних координат
        float baryCentricDiv = vectorProduct(Vec2D.sub(ProjectionPoint2, ProjectionPoint1), Vec2D.sub(ProjectionPoint3, ProjectionPoint1));

        //проходимо по всім пікселям по екрану та перевіряємо чи порапляють вони в трикутник
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                //координати точки що потрпила що перевіряється
                Vec2D PixelPoint = new Vec2D(x, y).add(0.5f, 0.5f);

                //побудова векторів від кутів до точки
                Vec2D PixelVector1 = Vec2D.sub(PixelPoint, ProjectionPoint1);
                Vec2D PixelVector2 = Vec2D.sub(PixelPoint, ProjectionPoint2);
                Vec2D PixelVector3 = Vec2D.sub(PixelPoint, ProjectionPoint3);

                //довжина векторнионого добутку для вектору кожного ребра та вектору до точки
                float lengthVectorProduct1 = vectorProduct(PixelVector1, Edge1);
                float lengthVectorProduct2 = vectorProduct(PixelVector2, Edge2);
                float lengthVectorProduct3 = vectorProduct(PixelVector3, Edge3);

                //перевірка чи потрапляє точка в трикутник
                if ((lengthVectorProduct1 <= 0 || (isTopLeft1 && lengthVectorProduct1 == 0.f)) &&
                        (lengthVectorProduct2 <= 0 || (isTopLeft2 && lengthVectorProduct2 == 0.f)) &&
                        (lengthVectorProduct3 <= 0 || (isTopLeft3 && lengthVectorProduct3 == 0.f))) {
                    // положення точки в масиві пікселів та масиві глибин
                    int PixelID = y * GS.getScreenWidth() + x;

                    //коефіціенти для інтерполяції кольоровів/глибини в трикутнику
                    float T1 = -lengthVectorProduct2 / baryCentricDiv;
                    float T2 = -lengthVectorProduct3 / baryCentricDiv;
                    float T3 = -lengthVectorProduct1 / baryCentricDiv;

                    //глибина пікселя
                    float Depth = T1 * TransformPoint1.z() + T2 * TransformPoint2.z() + T3 * TransformPoint3.z();

                    if (Depth >= 0 && Depth <= 1f && Depth < GS.DepthBuffer[PixelID]) {
                        // розраховуємо вклад кожного з кольорів в колір точки
                        Vec4D NewColorPart1 = Vec4D.mult(Colors[0], T1);
                        Vec4D NewColorPart2 = Vec4D.mult(Colors[1], T2);
                        Vec4D NewColorPart3 = Vec4D.mult(Colors[2], T3);

                        // новий колір, з кольорами в діапазоні 0 ... 1
                        Vec4D NewColor = NewColorPart1.add(NewColorPart2).add(NewColorPart3);

                        GS.pixels[PixelID] = toRGBA(NewColor);
                        GS.DepthBuffer[PixelID] = Depth;
                    }
                }

            }
        }

        //стара функція яка створювала проектований трикутник, і потім викликала його малювання
//        new com.my_program.rendering.triangle2D(TransformPoint1.Perspective(), TransformPoint2.Perspective(), TransformPoint3.Perspective(),
//                Colors,
//                new float[] {TransformPoint1.z(), TransformPoint2.z(), TransformPoint3.z()}).draw();
    }

    //перетворення вектору з кольорами в діапазоні 0 ... 1, в цілочисельне значення
    private int toRGBA(Vec4D Color) {
        return ((int)(Color.a() * 255) << 24) | ((int)(Color.r() * 255) << 16) | ((int)(Color.g() * 255) << 8) | (int)(Color.b() * 255);
    }

    // довжина векторного добутоку двох векторів
    private float vectorProduct (Vec2D TriangleEdge, Vec2D ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }
}

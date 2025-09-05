package com.my_program.rendering;

//Оскільки функціонал сортування точок за часовою стріллкою не портібен,
//оскільки пердбачається що точки відразу буддуть відсортовані,
//весь інший функціонал пренесенов в "com.my_program.rendering.DrawTriangle3D" для спрощення
@Deprecated
public class triangle2D {
    public Vec2D ProjectionPoint1;
    public Vec2D ProjectionPoint2;
    public Vec2D ProjectionPoint3;

    public float Depth1;
    public float Depth2;
    public float Depth3;

    public Vec4D Color1;
    public Vec4D Color2;
    public Vec4D Color3;

    //визначення меж в якому розташований трикутник
    private int minX;
    private int maxX;

    private int minY;
    private int maxY;

    //створення новго трикутника проектованого на площину екрана, та відсоровування кутів по часовій стрілці
    public triangle2D(Vec2D P1, Vec2D P2, Vec2D P3, Vec4D[] Colors, float[] Depth) {
        if (P1.x() <= P2.x() && P1.x() <= P3.x()) {
            this.ProjectionPoint1 = P1;
            this.Color1 = Colors[0];
            this.Depth1 = Depth[0];

            if(vectorProduct(Vec2D.sub(P2, ProjectionPoint1), Vec2D.sub(P3, ProjectionPoint1)) < 0) {
                this.ProjectionPoint2 = P2;
                this.ProjectionPoint3 = P3;

                this.Color2 = Colors[1];
                this.Color3 = Colors[2];

                this.Depth2 = Depth[1];
                this.Depth3 = Depth[2];
            } else {
                this.ProjectionPoint2 = P3;
                this.ProjectionPoint3 = P2;

                this.Color2 = Colors[2];
                this.Color3 = Colors[1];

                this.Depth2 = Depth[2];
                this.Depth3 = Depth[1];
            }
        } else if (P2.x() <= P3.x()) {
            this.ProjectionPoint1 = P2;
            this.Color1 = Colors[1];
            this.Depth1 = Depth[1];

            if(vectorProduct(Vec2D.sub(P1, ProjectionPoint1), Vec2D.sub(P3, ProjectionPoint1)) < 0) {
                this.ProjectionPoint2 = P1;
                this.ProjectionPoint3 = P3;

                this.Color2 = Colors[0];
                this.Color3 = Colors[2];

                this.Depth2 = Depth[0];
                this.Depth3 = Depth[2];
            } else {
                this.ProjectionPoint2 = P3;
                this.ProjectionPoint3 = P1;

                this.Color2 = Colors[2];
                this.Color3 = Colors[0];

                this.Depth2 = Depth[2];
                this.Depth3 = Depth[0];
            }
        } else {
            this.ProjectionPoint1 = P3;
            this.Color1 = Colors[2];
            this.Depth1 = Depth[2];

            if(vectorProduct(Vec2D.sub(P1, ProjectionPoint1), Vec2D.sub(P2, ProjectionPoint1)) < 0) {
                this.ProjectionPoint2 = P1;
                this.ProjectionPoint3 = P2;

                this.Color2 = Colors[0];
                this.Color3 = Colors[1];

                this.Depth2 = Depth[0];
                this.Depth3 = Depth[1];
            } else {
                this.ProjectionPoint2 = P2;
                this.ProjectionPoint3 = P1;

                this.Color2 = Colors[1];
                this.Color3 = Colors[0];

                this.Depth2 = Depth[1];
                this.Depth3 = Depth[0];
            }
        }

        //Пошук області в якій розташовано трикутник
        minX = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));
        maxX = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));

        minY = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));
        maxY = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));

        //перевірка щоб, облатись не виходила за межі екрану
        if (minX < 0) minX = 0;
        if (minY < 0) minY = 0;

        if (maxX >= GS.getScreenWidth()) maxX = GS.getScreenWidth() - 1;
        if (maxY >= GS.getScreenHeight()) maxY = GS.getScreenHeight() - 1;
    }


    //функція відмалбовування трикутників
    public void draw () {

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
                if ((lengthVectorProduct1 >= 0 || (isTopLeft1 && lengthVectorProduct1 == 0.f)) &&
                        (lengthVectorProduct2 >= 0 || (isTopLeft2 && lengthVectorProduct2 == 0.f)) &&
                        (lengthVectorProduct3 >= 0 || (isTopLeft3 && lengthVectorProduct3 == 0.f))) {
                    // положення точки в масиві пікселів та масиві глибин
                    int PixelID = y * GS.getScreenWidth() + x;

                    //коефіціенти для інтерполяції кольоровів/глибини в трикутнику
                    float T1 = -lengthVectorProduct2 / baryCentricDiv;
                    float T2 = -lengthVectorProduct3 / baryCentricDiv;
                    float T3 = -lengthVectorProduct1 / baryCentricDiv;

                    //глибина пікселя
                    float Depth = T1 / Depth1 + T2 / Depth2 + T3 / Depth3;

                    if (Depth > GS.DepthBuffer[PixelID]) {
                        // розраховуємо вклад кожного з кольорів в колір точки
                        Vec4D NewColorPart1 = Vec4D.mult(Color1, T1);
                        Vec4D NewColorPart2 = Vec4D.mult(Color2, T2);
                        Vec4D NewColorPart3 = Vec4D.mult(Color3, T3);

                        // новий колір, з кольорами в діапазоні 0 ... 1
                        Vec4D NewColor = NewColorPart1.add(NewColorPart2).add(NewColorPart3);

                        GS.pixels[PixelID] = toRGBA(NewColor);
                        GS.DepthBuffer[PixelID] = Depth;
                    }
                }

            }
        }

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

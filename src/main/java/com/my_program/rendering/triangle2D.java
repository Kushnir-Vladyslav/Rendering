package com.my_program.rendering;

//Оскільки функціонал сортування точок за часовою стріллкою не портібен,
//оскільки пердбачається що точки відразу буддуть відсортовані,
//весь інший функціонал пренесенов в "com.my_program.rendering.DrawTriangle3D" для спрощення
@Deprecated
public class triangle2D {
    public vec2D ProjectionPoint1;
    public vec2D ProjectionPoint2;
    public vec2D ProjectionPoint3;

    public float Depth1;
    public float Depth2;
    public float Depth3;

    public vec4D Color1;
    public vec4D Color2;
    public vec4D Color3;

    //визначення меж в якому розташований трикутник
    private int minX;
    private int maxX;

    private int minY;
    private int maxY;

    //створення новго трикутника проектованого на площину екрана, та відсоровування кутів по часовій стрілці
    public triangle2D(vec2D P1, vec2D P2, vec2D P3, vec4D[] Colors, float[] Depth) {
        if (P1.x() <= P2.x() && P1.x() <= P3.x()) {
            this.ProjectionPoint1 = P1;
            this.Color1 = Colors[0];
            this.Depth1 = Depth[0];

            if(vectorProduct(vec2D.sub(P2, ProjectionPoint1), vec2D.sub(P3, ProjectionPoint1)) < 0) {
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

            if(vectorProduct(vec2D.sub(P1, ProjectionPoint1), vec2D.sub(P3, ProjectionPoint1)) < 0) {
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

            if(vectorProduct(vec2D.sub(P1, ProjectionPoint1), vec2D.sub(P2, ProjectionPoint1)) < 0) {
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

        if (maxX >= GlobalState.getScreenWidth()) maxX = GlobalState.getScreenWidth() - 1;
        if (maxY >= GlobalState.getScreenHeight()) maxY = GlobalState.getScreenHeight() - 1;
    }


    //функція відмалбовування трикутників
    public void draw () {

        vec2D Edge1 = vec2D.sub(ProjectionPoint2, ProjectionPoint1);
        vec2D Edge2 = vec2D.sub(ProjectionPoint3, ProjectionPoint2);
        vec2D Edge3 = vec2D.sub(ProjectionPoint1, ProjectionPoint3);

        //перевіряємо чи є ребро верхнім лівим, щоб знати чи малювати його
        boolean isTopLeft1 = (Edge1.x() >= 0.f && Edge1.y() > 0.f) || (Edge1.x() > 0.f && Edge1.y() == 0.f);
        boolean isTopLeft2 = (Edge2.x() >= 0.f && Edge2.y() > 0.f) || (Edge2.x() > 0.f && Edge2.y() == 0.f);
        boolean isTopLeft3 = (Edge3.x() >= 0.f && Edge3.y() > 0.f) || (Edge3.x() > 0.f && Edge3.y() == 0.f);

        //спільний дільник для барецентричних координат
        float baryCentricDiv = vectorProduct(vec2D.sub(ProjectionPoint2, ProjectionPoint1), vec2D.sub(ProjectionPoint3, ProjectionPoint1));

        //проходимо по всім пікселям по екрану та перевіряємо чи порапляють вони в трикутник
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                //координати точки що потрпила що перевіряється
                vec2D PixelPoint = new vec2D(x, y).add(0.5f, 0.5f);

                //побудова векторів від кутів до точки
                vec2D PixelVector1 = vec2D.sub(PixelPoint, ProjectionPoint1);
                vec2D PixelVector2 = vec2D.sub(PixelPoint, ProjectionPoint2);
                vec2D PixelVector3 = vec2D.sub(PixelPoint, ProjectionPoint3);

                //довжина векторнионого добутку для вектору кожного ребра та вектору до точки
                float LengthVectorProduct1 = vectorProduct(PixelVector1, Edge1);
                float LengthVectorProduct2 = vectorProduct(PixelVector2, Edge2);
                float LengthVectorProduct3 = vectorProduct(PixelVector3, Edge3);

                //перевірка чи потрапляє точка в трикутник
                if ((LengthVectorProduct1 >= 0 || (isTopLeft1 && LengthVectorProduct1 == 0.f)) &&
                        (LengthVectorProduct2 >= 0 || (isTopLeft2 && LengthVectorProduct2 == 0.f)) &&
                        (LengthVectorProduct3 >= 0 || (isTopLeft3 && LengthVectorProduct3 == 0.f))) {
                    // положення точки в масиві пікселів та масиві глибин
                    int PixelID = y * GlobalState.getScreenWidth() + x;

                    //коефіціенти для інтерполяції кольоровів/глибини в трикутнику
                    float T1 = -LengthVectorProduct2 / baryCentricDiv;
                    float T2 = -LengthVectorProduct3 / baryCentricDiv;
                    float T3 = -LengthVectorProduct1 / baryCentricDiv;

                    //глибина пікселя
                    float Depth = T1 / Depth1 + T2 / Depth2 + T3 / Depth3;

                    if (Depth > GlobalState.DepthBuffer[PixelID]) {
                        // розраховуємо вклад кожного з кольорів в колір точки
                        vec4D NewColorPart1 = vec4D.mult(Color1, T1);
                        vec4D NewColorPart2 = vec4D.mult(Color2, T2);
                        vec4D NewColorPart3 = vec4D.mult(Color3, T3);

                        // новий колір, з кольорами в діапазоні 0 ... 1
                        vec4D NewColor = NewColorPart1.add(NewColorPart2).add(NewColorPart3);

                        GlobalState.Pixels[PixelID] = toRGBA(NewColor);
                        GlobalState.DepthBuffer[PixelID] = Depth;
                    }
                }

            }
        }

    }

    //перетворення вектору з кольорами в діапазоні 0 ... 1, в цілочисельне значення
    private int toRGBA(vec4D Color) {
        return ((int)(Color.a() * 255) << 24) | ((int)(Color.r() * 255) << 16) | ((int)(Color.g() * 255) << 8) | (int)(Color.b() * 255);
    }

    // довжина векторного добутоку двох векторів
    private float vectorProduct (vec2D TriangleEdge, vec2D ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }


}

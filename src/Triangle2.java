public class Triangle2 {
    public Vec2 Point1;
    public Vec2 Point2;
    public Vec2 Point3;

    public float Depth1;
    public float Depth2;
    public float Depth3;

    public Vec3 Color1;
    public Vec3 Color2;
    public Vec3 Color3;

    //визначення меж в якому розташований трикутник
    private int minX;
    private int maxX;

    private int minY;
    private int maxY;

    //створення новго трикутника проектованого на площину екрана, та відсоровування кутів по часовій стрілці
    public Triangle2(Vec2 P1, Vec2 P2, Vec2 P3, Vec3[] Colors, float[] Depth) {
        if (P1.x() <= P2.x() && P1.x() <= P3.x()) {
            this.Point1 = P1;
            this.Color1 = Colors[0];
            this.Depth1 = Depth[0];

            if(vectorProduct(Vec2.sub(P2, Point1), Vec2.sub(P3, Point1)) < 0) {
                this.Point2 = P2;
                this.Point3 = P3;

                this.Color2 = Colors[1];
                this.Color3 = Colors[2];

                this.Depth2 = Depth[1];
                this.Depth3 = Depth[2];
            } else {
                this.Point2 = P3;
                this.Point3 = P2;

                this.Color2 = Colors[2];
                this.Color3 = Colors[1];

                this.Depth2 = Depth[2];
                this.Depth3 = Depth[1];
            }
        } else if (P2.x() <= P3.x()) {
            this.Point1 = P2;
            this.Color1 = Colors[1];
            this.Depth1 = Depth[1];

            if(vectorProduct(Vec2.sub(P1, Point1), Vec2.sub(P3, Point1)) < 0) {
                this.Point2 = P1;
                this.Point3 = P3;

                this.Color2 = Colors[0];
                this.Color3 = Colors[2];

                this.Depth2 = Depth[0];
                this.Depth3 = Depth[2];
            } else {
                this.Point2 = P3;
                this.Point3 = P1;

                this.Color2 = Colors[2];
                this.Color3 = Colors[0];

                this.Depth2 = Depth[2];
                this.Depth3 = Depth[0];
            }
        } else {
            this.Point1 = P3;
            this.Color1 = Colors[2];
            this.Depth1 = Depth[2];

            if(vectorProduct(Vec2.sub(P1, Point1), Vec2.sub(P2, Point1)) < 0) {
                this.Point2 = P1;
                this.Point3 = P2;

                this.Color2 = Colors[0];
                this.Color3 = Colors[1];

                this.Depth2 = Depth[0];
                this.Depth3 = Depth[1];
            } else {
                this.Point2 = P2;
                this.Point3 = P1;

                this.Color2 = Colors[1];
                this.Color3 = Colors[0];

                this.Depth2 = Depth[1];
                this.Depth3 = Depth[0];
            }
        }

        //Пошук області в якій розташовано трикутник
        minX = (int) Math.floor(Point1.x());
        maxX = (int) Math.ceil(Math.max(Point2.x(), Point3.x()));

        minY = (int) Math.floor(Math.min(Math.min(Point1.y(), Point2.y()), Point3.y()));
        maxY = (int) Math.ceil(Math.max(Math.max(Point1.y(), Point2.y()), Point3.y()));

        //перевірка щоб, облатись не виходила за межі екрану
        if (minX < 0) minX = 0;
        if (minY < 0) minY = 0;

        if (maxX >= GlobalState.getScreenWidth()) maxX = GlobalState.getScreenWidth() - 1;
        if (maxY >= GlobalState.getScreenHeight()) maxY = GlobalState.getScreenHeight() - 1;
    }


    //функція відмалбовування трикутників
    public void draw () {

        Vec2 Edge1 = Vec2.sub(Point2, Point1);
        Vec2 Edge2 = Vec2.sub(Point3, Point2);
        Vec2 Edge3 = Vec2.sub(Point1, Point3);

        //перевіряємо чи є ребро верхнім лівим, щоб знати чи малювати його
        boolean isTopLeft1 = (Edge1.x() >= 0.f && Edge1.y() > 0.f) || (Edge1.x() > 0.f && Edge1.y() == 0.f);
        boolean isTopLeft2 = (Edge2.x() >= 0.f && Edge2.y() > 0.f) || (Edge2.x() > 0.f && Edge2.y() == 0.f);
        boolean isTopLeft3 = (Edge3.x() >= 0.f && Edge3.y() > 0.f) || (Edge3.x() > 0.f && Edge3.y() == 0.f);

        //спільний дільник для барецентричних координат
        float baryCentricDiv = vectorProduct(Vec2.sub(Point2, Point1), Vec2.sub(Point3, Point1));

        //проходимо по всім пікселям по екрану та перевіряємо чи порапляють вони в трикутник
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                //координати точки що потрпила що перевіряється
                Vec2 PixelPoint = new Vec2(x, y).add(0.5f, 0.5f);

                //побудова векторів від кутів до точки
                Vec2 PixelVector1 = Vec2.sub(PixelPoint, Point1);
                Vec2 PixelVector2 = Vec2.sub(PixelPoint, Point2);
                Vec2 PixelVector3 = Vec2.sub(PixelPoint, Point3);

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
                        Vec3 NewColorPart1 = Vec3.mult(Color1, T1);
                        Vec3 NewColorPart2 = Vec3.mult(Color2, T2);
                        Vec3 NewColorPart3 = Vec3.mult(Color3, T3);

                        // новий колір, з кольорами в діапазоні 0 ... 1
                        Vec3 NewColor = NewColorPart1.add(NewColorPart2).add(NewColorPart3);

                        GlobalState.Pixels[PixelID] = toRGBA(NewColor);
                        GlobalState.DepthBuffer[PixelID] = Depth;
                    }
                }

            }
        }

    }

    //перетворення вектору з кольорами в діапазоні 0 ... 1, в цілочисельне значення
    private int toRGBA(Vec3 Color) {
        return (0XFF << 24) | ((int)(Color.r() * 255) << 16) | ((int)(Color.g() * 255) << 8) | (int)(Color.b() * 255);
    }

    // довжина векторного добутоку двох векторів
    private float vectorProduct (Vec2 TriangleEdge, Vec2 ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }


}

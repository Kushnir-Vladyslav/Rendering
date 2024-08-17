//Використовувася для відмальовування трикутників з заданими кольорами на вершинах
//після додванання тестур застарів
@Deprecated
public class triangle3DSmoothColor {
    private vec4D Point1;
    private vec4D Point2;
    private vec4D Point3;

    private matrix4D Transforms;

    private vec4D[] Colors;

    public triangle3DSmoothColor(vec4D P1, vec4D P2, vec4D P3, matrix4D InitialPosition, vec4D[] Colors) {
        this.Point1 = P1;
        this.Point2 = P2;
        this.Point3 = P3;

        this.Transforms = InitialPosition;

        this.Colors = Colors;
    }

    public void tansform (matrix4D moveMatrix) {
        this.Transforms.mult(moveMatrix);
    }

    public void newColor(vec4D[] Colors) {
        this.Colors = Colors;
    }

    public vec2D getPoint2 (int num) {
        if (num == 0) return Point1.Perspective();
        if (num == 1) return Point2.Perspective();
        if (num == 2) return Point3.Perspective();
        return new vec2D(0);
    }

    //малювання трикутника заповнюючи його одним певним кольором
    public void draw () {
        vec4D TransformPoint1 = Transforms.mult(Point1);
        vec4D TransformPoint2 = Transforms.mult(Point2);
        vec4D TransformPoint3 = Transforms.mult(Point3);

        TransformPoint1.div3D(TransformPoint1.w());
        TransformPoint2.div3D(TransformPoint2.w());
        TransformPoint3.div3D(TransformPoint3.w());

        // Проектування точок на екран
        vec2D ProjectionPoint1 = TransformPoint1.NdcToPixels();
        vec2D ProjectionPoint2 = TransformPoint2.NdcToPixels();
        vec2D ProjectionPoint3 = TransformPoint3.NdcToPixels();

        //визначення меж в якому розташований трикутник
        //Пошук області в якій розташовано трикутник
        int minX = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));
        int maxX = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.x(), ProjectionPoint2.x()), ProjectionPoint3.x()));

        int minY = (int) Math.floor(Math.min(Math.min(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));
        int maxY = (int) Math.ceil(Math.max(Math.max(ProjectionPoint1.y(), ProjectionPoint2.y()), ProjectionPoint3.y()));

        //перевірка щоб, облатись не виходила за межі екрану
        if (minX < 0) minX = 0;
        if (minY < 0) minY = 0;

        if (maxX >= GlobalState.getScreenWidth()) maxX = GlobalState.getScreenWidth() - 1;
        if (maxY >= GlobalState.getScreenHeight()) maxY = GlobalState.getScreenHeight() - 1;

        //знаходження векторів ребер
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
                if ((LengthVectorProduct1 <= 0 || (isTopLeft1 && LengthVectorProduct1 == 0.f)) &&
                        (LengthVectorProduct2 <= 0 || (isTopLeft2 && LengthVectorProduct2 == 0.f)) &&
                        (LengthVectorProduct3 <= 0 || (isTopLeft3 && LengthVectorProduct3 == 0.f))) {
                    // положення точки в масиві пікселів та масиві глибин
                    int PixelID = y * GlobalState.getScreenWidth() + x;

                    //коефіціенти для інтерполяції кольоровів/глибини в трикутнику
                    float T1 = -LengthVectorProduct2 / baryCentricDiv;
                    float T2 = -LengthVectorProduct3 / baryCentricDiv;
                    float T3 = -LengthVectorProduct1 / baryCentricDiv;

                    //глибина пікселя
                    float Depth = T1 * TransformPoint1.z() + T2 * TransformPoint2.z() + T3 * TransformPoint3.z();

                    if (Depth >= 0 && Depth <= 1f && Depth < GlobalState.DepthBuffer[PixelID]) {
                        // розраховуємо вклад кожного з кольорів в колір точки
                        vec4D NewColorPart1 = vec4D.mult(Colors[0], T1);
                        vec4D NewColorPart2 = vec4D.mult(Colors[1], T2);
                        vec4D NewColorPart3 = vec4D.mult(Colors[2], T3);

                        // новий колір, з кольорами в діапазоні 0 ... 1
                        vec4D NewColor = NewColorPart1.add(NewColorPart2).add(NewColorPart3);

                        GlobalState.Pixels[PixelID] = toRGBA(NewColor);
                        GlobalState.DepthBuffer[PixelID] = Depth;
                    }
                }

            }
        }

        //стара функція яка створювала проектований трикутник, і потім викликала його малювання
//        new triangle2D(TransformPoint1.Perspective(), TransformPoint2.Perspective(), TransformPoint3.Perspective(),
//                Colors,
//                new float[] {TransformPoint1.z(), TransformPoint2.z(), TransformPoint3.z()}).draw();
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

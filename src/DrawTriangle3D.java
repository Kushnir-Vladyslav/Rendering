
public class DrawTriangle3D {

    //Вершини первинного трикутника
    private vec4D Point1;
    private vec4D Point2;
    private vec4D Point3;

    //матриця трансформації трикутник
    private matrix4D Transforms;

    //Вершини трикутника на карті текстури
    private vec2D[] UvPoints;

    //Текстура що відображається на трикутнику
    private texture Texture;

    //основний масив урізаних трикутників
    static private int NumOfMainTriangles;
    static private triangle[] MainTriangles;

    //допоміжний масив урізаних трикутників
    static private int NumOfSecondaryTriangles;
    static private triangle[] SecondaryTriangles;

    static {
        int SizeOfArrays = 128;

        MainTriangles = new triangle[SizeOfArrays];
        SecondaryTriangles = new triangle[SizeOfArrays];

        for (int i = 0; i < SizeOfArrays; i++) {
            MainTriangles[i] = new triangle();
            SecondaryTriangles[i] = new triangle();
        }

        NumOfMainTriangles = 0;
        NumOfSecondaryTriangles = 0;
    }

    public DrawTriangle3D(vec4D[] Points, matrix4D InitialPosition, vec2D[] UvPoints, texture Texture) {
        this.Point1 = Points[0];
        this.Point2 = Points[1];
        this.Point3 = Points[2];

        this.Transforms = InitialPosition;

        this.UvPoints = UvPoints;

        this.Texture = Texture;

        NumOfMainTriangles = 0;
        NumOfSecondaryTriangles = 0;
    }

    @Deprecated
    public vec2D getPoint2 (int num) {
        if (num == 0) return Point1.Perspective();
        if (num == 1) return Point2.Perspective();
        if (num == 2) return Point3.Perspective();
        return new vec2D(0);
    }

    private void clippingTriangle (clipAxis ClipAxis) {
        for (int TriangleID = 0; TriangleID < NumOfMainTriangles; TriangleID++) {

            boolean[] OutOfSightPoint ;

            boolean OutOfSightPoint1 = ClipAxis.IsOutOfSight(MainTriangles[TriangleID].Points[0]);
            boolean OutOfSightPoint2 = ClipAxis.IsOutOfSight(MainTriangles[TriangleID].Points[1]);
            boolean OutOfSightPoint3 = ClipAxis.IsOutOfSight(MainTriangles[TriangleID].Points[2]);

            int NumOutOfSight = 0;
            if (OutOfSightPoint1) NumOutOfSight++;
            if (OutOfSightPoint2) NumOutOfSight++;
            if (OutOfSightPoint3) NumOutOfSight++;

            switch (NumOutOfSight) {
                case 0 -> {
                    // весь трикутник в зоні видимості, додаємо в вихідний масив
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[0] = MainTriangles[TriangleID].Points[0];
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[1] = MainTriangles[TriangleID].Points[1];
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[2] = MainTriangles[TriangleID].Points[2];

                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[0] = MainTriangles[TriangleID].UvPoints[0];
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[1] = MainTriangles[TriangleID].UvPoints[1];
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[2] = MainTriangles[TriangleID].UvPoints[2];

                    NumOfSecondaryTriangles++;
                }
                case 1 -> {
                    float S;

                    //шукаємо точку яка не входить і вдповідно шукаємо нові точки перетену
                    //в результаті повинно вийти два нових трикутники
                    int IsNotInOfSight;
                    if(OutOfSightPoint1) {
                        IsNotInOfSight = 0;
                    } else if (OutOfSightPoint2) {
                        IsNotInOfSight = 1;
                    } else {
                        IsNotInOfSight = 2;
                    }

                    //перша точка це перетин прямої з точкою що не входить і наступною
                    S = ClipAxis.IntersectionPoint(
                            MainTriangles[TriangleID].Points[IsNotInOfSight],
                            MainTriangles[TriangleID].Points[(IsNotInOfSight + 1) % 3]
                    );
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[0] =
                            vec4D.mult(MainTriangles[TriangleID].Points[IsNotInOfSight], 1f - S).add(
                                    vec4D.mult(MainTriangles[TriangleID].Points[(IsNotInOfSight + 1) % 3], S)
                            );
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[0] =
                            vec2D.mult(MainTriangles[TriangleID].UvPoints[IsNotInOfSight], 1f - S).add(
                                    vec2D.mult(MainTriangles[TriangleID].UvPoints[(IsNotInOfSight + 1) % 3], S)
                            );

                    //наступні дві точки це ті що входять, в тійже послідовності
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[1] =
                            MainTriangles[TriangleID].Points[(IsNotInOfSight + 1) % 3];
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[1] =
                            MainTriangles[TriangleID].UvPoints[(IsNotInOfSight + 1) % 3];

                    SecondaryTriangles[NumOfSecondaryTriangles].Points[2] =
                            MainTriangles[TriangleID].Points[(IsNotInOfSight + 2) % 3];
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[2] =
                            MainTriangles[TriangleID].UvPoints[(IsNotInOfSight + 2) % 3];

                    //новий трикутник
                    NumOfSecondaryTriangles++;

                    //новий трикутник починаєтья з знайденої точки перетину, і останньої точки
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[0] =
                            SecondaryTriangles[NumOfSecondaryTriangles - 1].Points[0].clone();
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[0] =
                            SecondaryTriangles[NumOfSecondaryTriangles - 1].UvPoints[0].clone();

                    SecondaryTriangles[NumOfSecondaryTriangles].Points[1] =
                            SecondaryTriangles[NumOfSecondaryTriangles - 1].Points[2].clone();
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[1] =
                            SecondaryTriangles[NumOfSecondaryTriangles - 1].UvPoints[2].clone();

                    //остання точка це точка перетину попердньої і тієї що не входить
                    S = ClipAxis.IntersectionPoint(
                            MainTriangles[TriangleID].Points[(IsNotInOfSight + 2) % 3],
                            MainTriangles[TriangleID].Points[IsNotInOfSight]
                    );
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[2] =
                            vec4D.mult(MainTriangles[TriangleID].Points[(IsNotInOfSight + 2) % 3], 1f - S).add(
                                    vec4D.mult(MainTriangles[TriangleID].Points[IsNotInOfSight], S)
                            );
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[2] =
                            vec2D.mult(MainTriangles[TriangleID].UvPoints[(IsNotInOfSight + 2) % 3], 1f - S).add(
                                    vec2D.mult(MainTriangles[TriangleID].UvPoints[IsNotInOfSight], S)
                            );

                    NumOfSecondaryTriangles++;
                }
                case 2 -> {
                    float S;

                    //перевіряємо чи точка входить, якщо так то відповідно проводими розрахунки інших двох
                    int IsInOfSight;
                    if(!OutOfSightPoint1) {
                        IsInOfSight = 0;
                    } else if (!OutOfSightPoint2) {
                        IsInOfSight = 1;
                    } else {
                        IsInOfSight = 2;
                    }

                    //клонуємо точку яка входить
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[0] =
                            MainTriangles[TriangleID].Points[IsInOfSight];
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[0] =
                            MainTriangles[TriangleID].UvPoints[IsInOfSight];

                    //шукаємо нову точку перетину на прямі з точок що входить і наступної
                    S = ClipAxis.IntersectionPoint(
                            MainTriangles[TriangleID].Points[IsInOfSight],
                            MainTriangles[TriangleID].Points[(IsInOfSight + 1) % 3]
                    );
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[1] =
                            vec4D.mult(MainTriangles[TriangleID].Points[IsInOfSight], 1f - S).add(
                                    vec4D.mult(MainTriangles[TriangleID].Points[(IsInOfSight + 1) % 3], S)
                            );
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[1] =
                            vec2D.mult(MainTriangles[TriangleID].UvPoints[IsInOfSight], 1f - S).add(
                                    vec2D.mult(MainTriangles[TriangleID].UvPoints[(IsInOfSight + 1) % 3], S)
                            );


                    //шукаємо нову точку перетину на прямі з точок що попередня і входить
                    S = ClipAxis.IntersectionPoint(
                            MainTriangles[TriangleID].Points[(IsInOfSight + 2) % 3],
                            MainTriangles[TriangleID].Points[IsInOfSight]
                    );
                    SecondaryTriangles[NumOfSecondaryTriangles].Points[2] =
                            vec4D.mult(MainTriangles[TriangleID].Points[(IsInOfSight + 2) % 3], 1f - S).add(
                                    vec4D.mult(MainTriangles[TriangleID].Points[IsInOfSight], S)
                            );
                    SecondaryTriangles[NumOfSecondaryTriangles].UvPoints[2] =
                            vec2D.mult(MainTriangles[TriangleID].UvPoints[(IsInOfSight + 2) % 3], 1f - S).add(
                                    vec2D.mult(MainTriangles[TriangleID].UvPoints[IsInOfSight], S)
                            );

                    NumOfSecondaryTriangles++;
                }
                case 3 -> {
                    //весь трикутник поза зони видимості, нічого додавати
                }
            }
        }

        triangle[] SwapTriangles = MainTriangles;
        MainTriangles = SecondaryTriangles;
        SecondaryTriangles = SwapTriangles;

        NumOfMainTriangles = NumOfSecondaryTriangles;
        NumOfSecondaryTriangles = 0;
    }

    //малювання трикутника заповнюючи його одним певним кольором
    public void draw () {
        //копіювання трикутника до масиву в якому будуть зберігатись обрізані трикутники
        MainTriangles[0].Points[0] = Transforms.mult(Point1);
        MainTriangles[0].Points[1] = Transforms.mult(Point2);
        MainTriangles[0].Points[2] = Transforms.mult(Point3);

        MainTriangles[0].UvPoints = UvPoints;

        NumOfMainTriangles = 1;


        //перевірка та обрізка трикутників при перетині з одною з обмежуючих площин
        for (clipAxis ClipAxis : clipAxis.values()) {
            clippingTriangle(ClipAxis);
        }

        //основний цикл відмальовування трикутників
        for (int TriangleID = 0; TriangleID < NumOfMainTriangles; TriangleID++) {

            vec4D TransformPoint1 = MainTriangles[TriangleID].Points[0].div3D(
                    MainTriangles[TriangleID].Points[0].w());
            vec4D TransformPoint2 = MainTriangles[TriangleID].Points[1].div3D(
                    MainTriangles[TriangleID].Points[1].w());
            vec4D TransformPoint3 = MainTriangles[TriangleID].Points[2].div3D(
                    MainTriangles[TriangleID].Points[2].w());

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
            //після додавання функціоналу обрізання, ця перевірка не має сенсу,
            //оскільки всі обєкти точно входять в площину екрану

            //знаходження векторів ребер
            vec2D Edge1 = vec2D.sub(ProjectionPoint2, ProjectionPoint1);
            vec2D Edge2 = vec2D.sub(ProjectionPoint3, ProjectionPoint2);
            vec2D Edge3 = vec2D.sub(ProjectionPoint1, ProjectionPoint3);

            //перевіряємо чи є ребро верхнім лівим, щоб знати чи малювати його
            boolean isTopLeft1 = (Edge1.y() > 0.f) || (Edge1.x() > 0.f && Edge1.y() == 0.f);
            boolean isTopLeft2 = (Edge2.y() > 0.f) || (Edge2.x() > 0.f && Edge2.y() == 0.f);
            boolean isTopLeft3 = (Edge3.y() > 0.f) || (Edge3.x() > 0.f && Edge3.y() == 0.f);

            //спільний дільник для барецентричних координат
            //барцентричний коефіціент заміненно на обернений для заміни дорого ділення на множення в наступних операціяч
            float ConverseBaryCentricDiv = 1.f / vectorProduct(vec2D.sub(ProjectionPoint2, ProjectionPoint1), vec2D.sub(ProjectionPoint3, ProjectionPoint1));

            //розраховується обрененний коефіціент для заміни дорого ділення на множення в наступних операціях
            float ConverseW1 = 1.f / TransformPoint1.w();
            float ConverseW2 = 1.f / TransformPoint2.w();
            float ConverseW3 = 1.f / TransformPoint3.w();

            //наступний код винесено за межі циклу, і буде розраховувати не для кожної точки окремо,
            //а для базової з додванням зміщення для кожної наступної точки.

            //координати точки що потрпила що перевіряється
            vec2D PixelPoint = new vec2D(minX, minY).add(0.5f, 0.5f);

            //побудова векторів від кутів до точки
            vec2D PixelVector1 = vec2D.sub(PixelPoint, ProjectionPoint1);
            vec2D PixelVector2 = vec2D.sub(PixelPoint, ProjectionPoint2);
            vec2D PixelVector3 = vec2D.sub(PixelPoint, ProjectionPoint3);

            //довжина векторнионого добутку для вектору кожного ребра та вектору до точки
            float BaseLengthVectorProduct1 = vectorProduct(PixelVector1, Edge1);
            float BaseLengthVectorProduct2 = vectorProduct(PixelVector2, Edge2);
            float BaseLengthVectorProduct3 = vectorProduct(PixelVector3, Edge3);


            //проходимо по всім пікселям по екрану та перевіряємо чи порапляють вони в трикутник
            for (int y = minY; y < maxY; y++) {

                //розрахунок довжини вектрного добутку відносно базової точки на y
                float OffsetYLengthVectorProduct1 = BaseLengthVectorProduct1 - Edge1.x() * (y - minY);
                float OffsetYLengthVectorProduct2 = BaseLengthVectorProduct2 - Edge2.x() * (y - minY);
                float OffsetYLengthVectorProduct3 = BaseLengthVectorProduct3 - Edge3.x() * (y - minY);

                for (int x = minX; x < maxX; x++) {

                    //Розрахунок остаточної довжини векторногодобутку вектору ребра та вектору до точки
                    //розраховується відносно базової точки з врахуванням зміщення x та y
                    float LengthVectorProduct1 = OffsetYLengthVectorProduct1 + Edge1.y() * (x - minX);
                    float LengthVectorProduct2 = OffsetYLengthVectorProduct2 + Edge2.y() * (x - minX);
                    float LengthVectorProduct3 = OffsetYLengthVectorProduct3 + Edge3.y() * (x - minX);

                    //перевірка чи потрапляє точка в трикутник
                    if ((LengthVectorProduct1 <= 0 || (isTopLeft1 && LengthVectorProduct1 == 0.f)) &&
                            (LengthVectorProduct2 <= 0 || (isTopLeft2 && LengthVectorProduct2 == 0.f)) &&
                            (LengthVectorProduct3 <= 0 || (isTopLeft3 && LengthVectorProduct3 == 0.f))) {
                        // положення точки в масиві пікселів та масиві глибин
                        int PixelID = y * GlobalState.getScreenWidth() + x;

                        //коефіціенти для інтерполяції кольоровів/глибини в трикутнику
                        //заміненна дорога операція ділення на дешевшу множення
                        float T1 = -LengthVectorProduct2 * ConverseBaryCentricDiv;
                        float T2 = -LengthVectorProduct3 * ConverseBaryCentricDiv;
                        float T3 = -LengthVectorProduct1 * ConverseBaryCentricDiv;

                        //глибина пікселя
                        float Depth = T1 * TransformPoint1.z() + T2 * TransformPoint2.z() + T3 * TransformPoint3.z();


                        if ( Depth < GlobalState.DepthBuffer[PixelID]) {
                            //замінено дорогого ділення на множення
                            //float OneOverW = T1 / TransformPoint1.w() + T2 / TransformPoint2.w() + T3 / TransformPoint3.w();

                            float OneOverW = T1 * ConverseW1 + T2 * ConverseW2 + T3 * ConverseW3;

                            // інтерполяція положення точки в трикутнику, відносно вершин
                            //замінено дороге ділення на множення
                            //vec2D Uv1 = vec2D.mult(MainTriangles[TriangleID].UvPoints[0], T1).div(TransformPoint1.w());

                            vec2D Uv1 = vec2D.mult(MainTriangles[TriangleID].UvPoints[0], T1).mult(ConverseW1);
                            vec2D Uv2 = vec2D.mult(MainTriangles[TriangleID].UvPoints[1], T2).mult(ConverseW2);
                            vec2D Uv3 = vec2D.mult(MainTriangles[TriangleID].UvPoints[2], T3).mult(ConverseW3);

                            GlobalState.Pixels[PixelID] = Texture.getColor(Uv1.add(Uv2).add(Uv3).div(OneOverW));
                            GlobalState.DepthBuffer[PixelID] = Depth;
                        }
                    }
                }
            }
        }
    }


    //перетворення вектору з кольорами в діапазоні 0 ... 1, в цілочисельне значення
    //для текстур не потрібно вираховувати колір
    //перенесено в клас "texture"
    @Deprecated
    private int toRGBA(vec4D Color) {
        return ((int)(Color.a() * 255) << 24) | ((int)(Color.r() * 255) << 16) | ((int)(Color.g() * 255) << 8) | (int)(Color.b() * 255);
    }

    // довжина векторного добутоку двох векторів
    private float vectorProduct (vec2D TriangleEdge, vec2D ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }
}

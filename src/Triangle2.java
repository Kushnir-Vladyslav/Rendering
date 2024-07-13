public class Triangle2 {
    public Vec2 Point1;
    public Vec2 Point2;
    public Vec2 Point3;

    //створення новго трикутника проектованого на площину екрана, та відсоровування кутів по часовій стрілці
    public Triangle2(Vec2 P1, Vec2 P2, Vec2 P3) {
        if (P1.x() <= P2.x() && P1.x() <= P3.x()) {
            this.Point1 = P1;

            if(P2.y() >= P3.y()) {
                this.Point2 = P2;
                this.Point3 = P3;
            } else {
                this.Point2 = P3;
                this.Point3 = P2;
            }
        } else if (P2.x() <= P3.x()) {
            this.Point1 = P2;

            if(P1.y() >= P3.y()) {
                this.Point2 = P1;
                this.Point3 = P3;
            } else {
                this.Point2 = P3;
                this.Point3 = P1;
            }
        } else {
            this.Point1 = P3;

            if(P1.y() >= P2.y()) {
                this.Point2 = P1;
                this.Point3 = P2;
            } else {
                this.Point2 = P2;
                this.Point3 = P1;
            }
        }
    }

    //функція відмалбовування трикутників
    public void draw (int color) {

        Vec2 Edge1 = Vec2.sub(Point2, Point1);
        Vec2 Edge2 = Vec2.sub(Point3, Point2);
        Vec2 Edge3 = Vec2.sub(Point1, Point3);

        //перевіряємо чи є ребро верхнім лівим, щоб знати чи малювати його
        boolean isTopLeft1 = (Edge1.x() >= 0.f && Edge1.y() > 0.f) || (Edge1.x() > 0.f && Edge1.y() == 0.f);
        boolean isTopLeft2 = (Edge2.x() >= 0.f && Edge2.y() > 0.f) || (Edge2.x() > 0.f && Edge2.y() == 0.f);
        boolean isTopLeft3 = (Edge3.x() >= 0.f && Edge3.y() > 0.f) || (Edge3.x() > 0.f && Edge3.y() == 0.f);

        //проходимо по всім пікселям по екрану та перевіряємо чи порапляють вони в трикутник
        for (int y = 0; y < GlobalState.getScreenHeight(); y++) {
            for (int x = 0; x < GlobalState.getScreenWidth(); x++) {
                //координати точки що потрпила що перевіряється
                Vec2 PixelPoint = new Vec2(x, y).add(0.5f, 0.5f);

                //побудова векторів від кутів до точки
                Vec2 PixelVector1 = Vec2.sub(PixelPoint, Point1);
                Vec2 PixelVector2 = Vec2.sub(PixelPoint, Point2);
                Vec2 PixelVector3 = Vec2.sub(PixelPoint, Point3);

                //довжина векторнионого добутку для вектору кожного ребра та вектору до точки
                float lengthVectorProduct1 = vectorProduct(PixelVector1, Edge1);
                float lengthVectorProduct2 = vectorProduct(PixelVector2, Edge2);
                float lengthVectorProduct3 = vectorProduct(PixelVector3, Edge3);

                //перевірка чи потрапляє точка в трикутник
                if ((lengthVectorProduct1 >= 0 || (isTopLeft1 && lengthVectorProduct1 == 0.f)) &&
                        (lengthVectorProduct2 >= 0 || (isTopLeft2 && lengthVectorProduct2 == 0.f)) &&
                        (lengthVectorProduct3 >= 0 || (isTopLeft3 && lengthVectorProduct3 == 0.f))) {

                    int pixelID = y * GlobalState.getScreenWidth() + x;
                    GlobalState.Pixels[pixelID] = color;
                }

            }
        }

    }

    // довжина векторного добутоку двох векторів
    private float vectorProduct (Vec2 TriangleEdge, Vec2 ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }


}

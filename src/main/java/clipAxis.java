public enum clipAxis {
    CLIP_AXIS_LEFT {
         @Override
         public boolean IsOutOfSight(vec4D Point)
         {
             return Point.x() < -Point.w();
         }

         @Override
         public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
             return -(PointFirst.w() + PointFirst.x()) /
                     ((PointSecond.x() - PointFirst.x()) + (PointSecond.w() - PointFirst.w()));
         }
    },
    CLIP_AXIS_RIGHT {
        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.x() > Point.w();
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return (PointFirst.w() - PointFirst.x()) /
                    ((PointSecond.x() - PointFirst.x()) - (PointSecond.w() - PointFirst.w()));
        }
    },
    CLIP_AXIS_TOP {
        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.y() > Point.w();
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return (PointFirst.w() - PointFirst.y()) /
                    ((PointSecond.y() - PointFirst.y()) - (PointSecond.w() - PointFirst.w()));
        }
    },
    CLIP_AXIS_BOTTOM {
        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.y() < -Point.w();
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return -(PointFirst.w() + PointFirst.y()) /
                    ((PointSecond.y() - PointFirst.y()) + (PointSecond.w() - PointFirst.w()));
        }
    },
    CLIP_AXIS_NEAR {
        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.z() < 0;
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return -(PointFirst.z()) /
                    ((PointSecond.z() - PointFirst.z()));
        }
    },
    CLIP_AXIS_FAR {
        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.z() > Point.w();
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return (PointFirst.w() - PointFirst.z()) /
                    ((PointSecond.z() - PointFirst.z()) - (PointSecond.w() - PointFirst.w()));
        }
    },
    CLIP_AXIS_W {
        private final float SmallNumber = 0.00001f;

        @Override
        public boolean IsOutOfSight(vec4D Point)
        {
            return Point.w() < SmallNumber;
        }

        @Override
        public float IntersectionPoint(vec4D PointFirst, vec4D PointSecond) {
            return (SmallNumber - PointFirst.w()) /
                    ((PointSecond.w() - PointFirst.w()));
        }
    };

    //перевірка чи входить передана очка в зону видимості по відповідній площинні відсікання
    public abstract boolean IsOutOfSight(vec4D Point);

    //пошук коефіціенту S, для пошуку нововї точки що знаходиться на відповідній площинні відсікання
    public abstract float IntersectionPoint(vec4D PointFirst, vec4D PointSecond);


}

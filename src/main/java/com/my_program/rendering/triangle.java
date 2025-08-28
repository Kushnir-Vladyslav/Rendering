package com.my_program.rendering;

public class triangle {

    //вершини трикутника
    public Vec4D[] Points;

    //Вершини трикутника на карті текстури
    public Vec2D[] UvPoints;

    triangle() {
        Points = new Vec4D[3];

        UvPoints = new Vec2D[3];
    }

}

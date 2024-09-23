package com.my_program.rendering;

public class triangle {

    //вершини трикутника
    public vec4D[] Points;

    //Вершини трикутника на карті текстури
    public vec2D[] UvPoints;

    triangle() {
        Points = new vec4D[3];

        UvPoints = new vec2D[3];
    }

}

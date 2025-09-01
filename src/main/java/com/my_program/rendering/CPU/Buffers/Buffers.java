package com.my_program.rendering.CPU.Buffers;

import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Texture;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.atomic.AtomicInteger;

public class Buffers {
    //starting buffers
    public static Vec4D[] bufferVertices;
    public static Vec2D[] bufferUV;
    public static int[] bufferIndexes;

    public static int[] bufferObjectsRef;
    public static ObjectMaterial[] vertexMaterial;
    public static Texture[] textures;

    public static Matrix4D transformMatrix;

    //transformed buffers
    public static Vec4D[] bufferTransformedVertices;

    //primer polygons
    public static Vec4D[] bufferPolygonsVertices;
    public static Vec2D[] bufferPolygonsUV;
    public static ObjectMaterial[] polygonMaterial;

    //Clipping polygons
    public static Vec4D[] clippingPolygon;
    public static Vec2D[] clippingPolygonsUV;
    public static ObjectMaterial[] clippingPolygonMaterial;

    public AtomicInteger clippingCounter = new AtomicInteger(0);


}

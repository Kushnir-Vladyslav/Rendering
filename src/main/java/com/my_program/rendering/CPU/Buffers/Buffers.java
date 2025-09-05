package com.my_program.rendering.CPU.Buffers;

import com.my_program.rendering.CPU.Pipeline.PixelLinkedList;
import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Texture;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Buffers {
    //host data
    public static int vertexNum;
    public static int indexNum;

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
    public static int numberVertexes = 0;

    //primer polygons
    public static Vec4D[] bufferPolygonsVertices;
    public static Vec2D[] bufferPolygonsUV;
    public static ObjectMaterial[] polygonMaterial;

    //Clipping polygons
    public static Vec4D[] clippingPolygon;
    public static Vec2D[] clippingPolygonsUV;
    public static ObjectMaterial[] clippingPolygonMaterial;

    public static AtomicInteger clippingCounter;

    //Viewport Transform
    public static int width;
    public static int height;

    //Rasterization
    public static PixelLinkedList[] fragments;
    public static AtomicIntegerArray heads;
    public static AtomicInteger pixelCounter;

    //processing
    public static int[] pixels;

}

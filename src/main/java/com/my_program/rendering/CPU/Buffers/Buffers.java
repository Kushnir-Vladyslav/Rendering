package com.my_program.rendering.CPU.Buffers;

import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Texture;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

public class Buffers {
    public static Vec4D[] bufferVertices;
    public static Vec2D[] bufferUV;
    public static int[] bufferIndices;
    public static Matrix4D transformMatrix;

    public static int[] bufferObjectsRef;
    
    public static ObjectMaterial[] objects;

    public static Texture[] textures;
}

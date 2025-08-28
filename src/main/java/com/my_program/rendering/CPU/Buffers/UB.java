package com.my_program.rendering.CPU.Buffers;

import com.my_program.rendering.Matrix4D;

public class UB {
    public Matrix4D transformMatrix;
    public float[] lightPos = new float[3];
    public float[] lightColor = new float[4];
    public float[] cameraPos = new float[4];
}

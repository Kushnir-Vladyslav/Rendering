package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class PerspectiveDivideAndViewportTransformThreads extends RecursiveAction {
    private int id;
    private Vec4D[] vertexBuffer;
    private int width;
    private int height;

    public PerspectiveDivideAndViewportTransformThreads (
            int id,
            Vec4D[] vertexBuffer,
            int width,
            int height
    )
    {
        this.id = id;
        this.vertexBuffer = vertexBuffer;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void compute() {
        try {
            Vec4D vertex = vertexBuffer[id];

            float x = vertex.x() / vertex.w();
            float y = vertex.y() / vertex.w();
            float z = vertex.z() / vertex.w();

            x = (x + 1) * 0.5f * width;
            y = (-y + 1) * 0.5f * height;

            vertexBuffer[id] = new Vec4D(x, y, z, vertex.w());
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class VertexTransformingThreads extends RecursiveAction {
    private final int id;
    private final Vec4D[] vertices;
    private Vec4D[] transformedVertices;
    private final Matrix4D transformer;

    public VertexTransformingThreads(
            int id,
            Vec4D[] vertices,
            Vec4D[] transformedVertices,
            Matrix4D transformer)
    {
        this.id = id;
        this.vertices = vertices;
        this.transformedVertices = transformedVertices;
        this.transformer = transformer;
    }

    @Override
    protected void compute() {
        Vec4D vertex = vertices[id];
        transformedVertices[id] = transformer.mult(vertex);
    }
}

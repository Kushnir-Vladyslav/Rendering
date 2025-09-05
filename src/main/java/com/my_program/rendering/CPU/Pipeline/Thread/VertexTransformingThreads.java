package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class VertexTransformingThreads extends KernelCPU {
    private final Vec4D[] vertices;
    private Vec4D[] transformedVertices;
    private final Matrix4D transformer;
    private final int numberOfTasks;

    public VertexTransformingThreads(
            int id,
            Vec4D[] vertices,
            Vec4D[] transformedVertices,
            Matrix4D transformer,
            int numberOfTasks
            )
    {
        this.idWorkGroup = id;
        this.vertices = vertices;
        this.transformedVertices = transformedVertices;
        this.transformer = transformer;
        this.numberOfTasks = numberOfTasks;
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

            Vec4D vertex = vertices[id];
            transformedVertices[id] = transformer.mult(vertex);
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

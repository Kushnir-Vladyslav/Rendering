package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.Matrix4D;
import com.my_program.rendering.Vec4D;


public class VertexTransformingThreads extends KernelCPU {
    private final float[] vertices;
    private float[] transformedVertices;
    private final Matrix4D transformer;
    private final int numberOfTasks;

    public VertexTransformingThreads(
            int id,
            float[] vertices,
            float[] transformedVertices,
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

            float x = 0;
            float y = 0;
            float z = 0;
            float w = 0;

            Vec4D[] trans = transformer.getMatrixData();

            for (int i = 0; i < 4; i++) {
                float ver = vertices[id * 4 + i];
                x += trans[i].x() * ver;
                y += trans[i].y() * ver;
                z += trans[i].z() * ver;
                w += trans[i].w() * ver;
            }

            transformedVertices[id * 4 + 0] = x;
            transformedVertices[id * 4 + 1] = y;
            transformedVertices[id * 4 + 2] = z;
            transformedVertices[id * 4 + 3] = w;

        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

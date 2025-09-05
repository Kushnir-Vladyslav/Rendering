package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class PerspectiveDivideAndViewportTransformThreads extends KernelCPU {
    private Vec4D[] vertexBuffer;
    private int width;
    private int height;
    private final int numberOfTasks;

    public PerspectiveDivideAndViewportTransformThreads (
            int id,
            Vec4D[] vertexBuffer,
            int width,
            int height,
            AtomicInteger clippingCounter
    )
    {
        this.idWorkGroup = id;
        this.vertexBuffer = vertexBuffer;
        this.width = width;
        this.height = height;
        this.numberOfTasks = clippingCounter.get() * 3;
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

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

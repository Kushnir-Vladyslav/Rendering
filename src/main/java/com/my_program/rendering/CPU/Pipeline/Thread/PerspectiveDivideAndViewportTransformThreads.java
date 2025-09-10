package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class PerspectiveDivideAndViewportTransformThreads extends KernelCPU {
    private float[] vertexBuffer;
    private int width;
    private int height;
    private final int numberOfTasks;

    public PerspectiveDivideAndViewportTransformThreads (
            int id,
            float[] vertexBuffer,
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

            int vertexID = id * 4;

            vertexBuffer[vertexID + 0] = (vertexBuffer[vertexID + 0] / vertexBuffer[vertexID + 3] + 1) * 0.5f * width;
            vertexBuffer[vertexID + 1] = (-vertexBuffer[vertexID + 1] / vertexBuffer[vertexID + 3] + 1) * 0.5f * height;
            vertexBuffer[vertexID + 2] = vertexBuffer[vertexID + 2] / vertexBuffer[vertexID + 3];

        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class PolygonFormationThreads extends KernelCPU {
    private final float[] transformedVertices;
    private Vec4D[] polygons;
    private final float[] bufferUV;
    private Vec2D[] bufferPolygonsUV;
    private final int[] vertexMaterial;
    private ObjectMaterial[] polygonMaterial;
    private final int[] bufferIndexes;
    private final int numberOfTasks;


    public PolygonFormationThreads(
            int id,
            float[] transformedVertices,
            Vec4D[] polygons,
            float[] bufferUV,
            Vec2D[] bufferPolygonsUV,
            int[] vertexMaterial,
            ObjectMaterial[] polygonMaterial,
            int[] bufferIndexes,
            int numberOfTasks
    )
    {
        this.idWorkGroup = id;
        this.transformedVertices = transformedVertices;
        this.polygons = polygons;
        this.bufferUV = bufferUV;
        this.bufferPolygonsUV = bufferPolygonsUV;
        this.vertexMaterial = vertexMaterial;
        this.polygonMaterial = polygonMaterial;
        this.bufferIndexes = bufferIndexes;
        this.numberOfTasks = numberOfTasks;
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

            for (int i = 0; i < 3; i++) {
                int vertexIndex = bufferIndexes[id * 3 + i];
                polygons[id * 3 + i] = new Vec4D(
                        transformedVertices[vertexIndex * 4 + 0],
                        transformedVertices[vertexIndex * 4 + 1],
                        transformedVertices[vertexIndex * 4 + 2],
                        transformedVertices[vertexIndex * 4 + 3]
                );
                bufferPolygonsUV[id * 3 + i] = new Vec2D(
                        bufferUV[vertexIndex * 2 + 0],
                        bufferUV[vertexIndex * 2 + 1]
                );
            }

            polygonMaterial[id] = new ObjectMaterial(0,0, vertexMaterial[bufferIndexes[id * 3]]);
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

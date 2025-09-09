package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class PolygonFormationThreads extends KernelCPU {
    private final float[] transformedVertices;
    private float[] polygons;
    private final float[] bufferUV;
    private float[] bufferPolygonsUV;
    private final int[] vertexMaterial;
    private int[] polygonMaterial;
    private final int[] bufferIndexes;
    private final int numberOfTasks;


    public PolygonFormationThreads(
            int id,
            float[] transformedVertices,
            float[] polygons,
            float[] bufferUV,
            float[] bufferPolygonsUV,
            int[] vertexMaterial,
            int[] polygonMaterial,
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
                int vertexID = id * 3 + i;
                int vertexIndex = bufferIndexes[vertexID];
                System.arraycopy(transformedVertices, vertexIndex * 4, polygons, vertexID * 4, 4);
                System.arraycopy(bufferUV, vertexIndex * 2, bufferPolygonsUV, vertexID * 2, 2);
            }

            polygonMaterial[id] = vertexMaterial[bufferIndexes[id * 3]];
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

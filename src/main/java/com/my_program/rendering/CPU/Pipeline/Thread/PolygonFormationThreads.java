package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;

public class PolygonFormationThreads extends RecursiveAction {
    private int id;
    private final Vec4D[] transformedVertices;
    private Vec4D[] polygons;
    private final Vec2D[] bufferUV;
    private Vec2D[] bufferPolygonsUV;
    private final ObjectMaterial[] vertexMaterial;
    private ObjectMaterial[] polygonMaterial;
    private final int[] bufferIndexes;


    public PolygonFormationThreads(
            int id,
            Vec4D[] transformedVertices,
            Vec4D[] polygons,
            Vec2D[] bufferUV,
            Vec2D[] bufferPolygonsUV,
            ObjectMaterial[] vertexMaterial,
            ObjectMaterial[] polygonMaterial,
            int[] bufferIndexes
    )
    {
        this.id = id;
        this.transformedVertices = transformedVertices;
        this.polygons = polygons;
        this.bufferUV = bufferUV;
        this.bufferPolygonsUV = bufferPolygonsUV;
        this.vertexMaterial = vertexMaterial;
        this.polygonMaterial = polygonMaterial;
        this.bufferIndexes = bufferIndexes;
    }

    @Override
    protected void compute() {
        try {
            int vertexIndex1 = bufferIndexes[id * 3 + 0];
            int vertexIndex2 = bufferIndexes[id * 3 + 1];
            int vertexIndex3 = bufferIndexes[id * 3 + 2];

            Vec4D vertex1 = transformedVertices[vertexIndex1];
            Vec4D vertex2 = transformedVertices[vertexIndex2];
            Vec4D vertex3 = transformedVertices[vertexIndex3];

            Vec2D UV1 = bufferUV[vertexIndex1];
            Vec2D UV2 = bufferUV[vertexIndex2];
            Vec2D UV3 = bufferUV[vertexIndex3];

            ObjectMaterial material = vertexMaterial[vertexIndex1];

            polygons[id * 3 + 0] = vertex1;
            polygons[id * 3 + 1] = vertex2;
            polygons[id * 3 + 2] = vertex3;

            bufferPolygonsUV[id * 3 + 0] = UV1;
            bufferPolygonsUV[id * 3 + 1] = UV2;
            bufferPolygonsUV[id * 3 + 2] = UV3;

            polygonMaterial[id] = material;
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

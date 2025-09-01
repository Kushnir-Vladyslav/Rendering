package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ClippingProjectionThreads extends RecursiveAction {
    private int id;
    private Vec4D[] bufferPolygonsVertices;
    private Vec2D[] bufferPolygonsUV;
    private ObjectMaterial[] polygonMaterial;
    private Vec4D[] clippingPolygon;
    private Vec2D[] clippingPolygonsUV;
    private ObjectMaterial[] clippingPolygonMaterial;
    private AtomicInteger clippingCounter;

    public ClippingProjectionThreads(
            int id,
            Vec4D[] bufferPolygonsVertices,
            Vec2D[] bufferPolygonsUV,
            ObjectMaterial[] polygonMaterial,
            Vec4D[] clippingPolygon,
            Vec2D[] clippingPolygonsUV,
            ObjectMaterial[] clippingPolygonMaterial,
            AtomicInteger clippingCounter
    )
    {
        this.id = id;
        this.bufferPolygonsVertices = bufferPolygonsVertices;
        this.bufferPolygonsUV = bufferPolygonsUV;
        this.polygonMaterial = polygonMaterial;
        this.clippingPolygon = clippingPolygon;
        this.clippingPolygonsUV = clippingPolygonsUV;
        this.clippingPolygonMaterial = clippingPolygonMaterial;
        this.clippingCounter = clippingCounter;
    }

    private boolean isInside(Vec4D vertex, int plane) {
        switch (plane) {
            case 0: return vertex.x() >= -vertex.w(); // left
            case 1: return vertex.x() <=  vertex.w(); // right
            case 2: return vertex.y() >= -vertex.w(); // bottom
            case 3: return vertex.y() <=  vertex.w(); // top
            case 4: return vertex.z() >= -vertex.w(); // near
            case 5: return vertex.z() <=  vertex.w(); // far
            case 6: return vertex.w() >=    0.00001f; // w
        }
        return false;
    }

    private float intersectionPoint(Vec4D a, Vec4D b, int plane) {
        switch (plane) {
            case 0: return -(a.w() + a.x()) /
                    ((b.x() - a.x()) + (b.w() - a.w()));  // left
            case 1: return (a.w() - a.x()) /
                    ((b.x() - a.x()) - (b.w() - a.w())); // right
            case 2: return -(a.w() + a.y()) /
                    ((b.y() - a.y()) + (b.w() - a.w())); // bottom
            case 3: return (a.w() - a.y()) /
                    ((b.y() - a.y()) - (b.w() - a.w())); // top
            case 4: return -(a.z() + a.w()) /
                    ((b.z() - a.z()) + (b.w() - a.w()));  // near
            case 5: return (a.w() - a.z()) /
                    ((b.z() - a.z()) - (b.w() - a.w())); // far
            case 6: return (0.00001f - a.w()) /
                    (b.w() - a.w()); // w
        }
        return 0;
    }

    private Vec4D interpolateVertex(Vec4D a, Vec4D b, float t) {
        return Vec4D.mult(a, 1f - t).add(Vec4D.mult(b, t));
    }

    private Vec2D interpolateUV(Vec2D a, Vec2D b, float t) {
        return Vec2D.mult(a, 1f - t).add(Vec2D.mult(b, t));
    }

    private int clippingAlongPlane (
            Vec4D[] inVertex, Vec4D[] outVertex,
            Vec2D[] inUV, Vec2D[] outUV,
            int plane, int inCounter)
    {
        int outCounter = 0;
        Vec4D prevVertex = inVertex[inCounter - 1];
        Vec2D prevUV = inUV[inCounter - 1];
        boolean prevInside = isInside(prevVertex, plane);

        for (int i = 0; i < inCounter; i++) {
            Vec4D currVertex = inVertex[i];
            Vec2D currUV = inUV[i];
            boolean currInside = isInside(currVertex, plane);

            if (currInside) {
                if (!prevInside) {
                    float crossingCoefficient = intersectionPoint(prevVertex, currVertex, plane);

                    outVertex[outCounter] = interpolateVertex(prevVertex, currVertex, crossingCoefficient);
                    outUV[outCounter] = interpolateUV(prevUV, currUV, crossingCoefficient);
                    outCounter++;
                }

                outVertex[outCounter] = currVertex;
                outUV[outCounter] = currUV;
                outCounter++;
            } else if (prevInside) {
                float crossingCoefficient = intersectionPoint(prevVertex, currVertex, plane);

                outVertex[outCounter] = interpolateVertex(prevVertex, currVertex, crossingCoefficient);
                outUV[outCounter] = interpolateUV(prevUV, currUV, crossingCoefficient);
                outCounter++;
            }

            prevVertex = currVertex;
            prevUV = currUV;
            prevInside = currInside;
        }

        return outCounter;
    }



    @Override
    protected void compute() {

        Vec4D tempVertex1[] = new Vec4D[16];
        Vec4D tempVertex2[] = new Vec4D[16];

        Vec2D tempUV1[] = new Vec2D[16];
        Vec2D tempUV2[] = new Vec2D[16];


        int counter = 3;

        for (int i = 0; i < 3; i++) {
            tempVertex1[i] = bufferPolygonsVertices[id * 3 + i];
            tempUV1[i] = bufferPolygonsUV[id * 3 + i];
        }

        for (int i = 0; i < 7; i++) {
            counter = clippingAlongPlane(tempVertex1, tempVertex2,
                    tempUV1, tempUV2, i, counter);

            if(counter < 3) {
                return;
            }

            Vec4D[] tempV = tempVertex1;
            tempVertex1 = tempVertex2;
            tempVertex2 = tempV;

            Vec2D[] tempUV = tempUV1;
            tempUV1 = tempUV2;
            tempUV2 = tempUV;
        }

        int clippingPolygonID = clippingCounter.getAndAdd(counter - 2);
        ObjectMaterial material = polygonMaterial[id];

        for (int i = 0; i < counter - 2; i++) {
            clippingPolygon[(clippingPolygonID + i) * 3] = tempVertex1[0];
            clippingPolygonsUV[(clippingPolygonID + i) * 3] = tempUV1[0];
            for (int j = 1; j < 3; j++) {
                clippingPolygon[(clippingPolygonID + i) * 3 + j] = tempVertex1[i + j];
                clippingPolygonsUV[(clippingPolygonID + i) * 3 + j] = tempUV1[i + j];
            }

            clippingPolygonMaterial[clippingPolygonID + i] = material;
        }
    }
}

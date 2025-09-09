package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.atomic.AtomicInteger;

public class ClippingProjectionThreads extends KernelCPU {
    private final float[] bufferPolygonsVertices;
    private final float[] bufferPolygonsUV;
    private final int[] polygonMaterial;
    private Vec4D[] clippingPolygon;
    private Vec2D[] clippingPolygonsUV;
    private ObjectMaterial[] clippingPolygonMaterial;
    private AtomicInteger clippingCounter;
    private final int numberOfTasks;

    private float[] tempVertex1 = new float[64];
    private float[] tempVertex2 = new float[64];

    private float[] tempUV1 = new float[32];
    private float[] tempUV2 = new float[32];

    public ClippingProjectionThreads(
            int id,
            float[] bufferPolygonsVertices,
            float[] bufferPolygonsUV,
            int[] polygonMaterial,
            Vec4D[] clippingPolygon,
            Vec2D[] clippingPolygonsUV,
            ObjectMaterial[] clippingPolygonMaterial,
            AtomicInteger clippingCounter,
            int numberOfTasks
    )
    {
        this.idWorkGroup = id;
        this.bufferPolygonsVertices = bufferPolygonsVertices;
        this.bufferPolygonsUV = bufferPolygonsUV;
        this.polygonMaterial = polygonMaterial;
        this.clippingPolygon = clippingPolygon;
        this.clippingPolygonsUV = clippingPolygonsUV;
        this.clippingPolygonMaterial = clippingPolygonMaterial;
        this.clippingCounter = clippingCounter;
        this.numberOfTasks = numberOfTasks;
    }

    private boolean isInside(float[] vertex, int plane) {
        switch (plane) {
            case 0: return vertex[3] >=    0.00001f; // w
            case 1: return vertex[0] >= -vertex[3]; // left
            case 2: return vertex[0] <=  vertex[3]; // right
            case 3: return vertex[1] >= -vertex[3]; // bottom
            case 4: return vertex[1] <=  vertex[3]; // top
            case 5: return vertex[2] >= -vertex[3]; // near
            case 6: return vertex[2] <=  vertex[3]; // far
        }
        return false;
    }

    private float intersectionPoint(float[] a, float[] b, int plane) {
        switch (plane) {
            case 0: return (0.00001f - a[3]) /
                    (b[3] - a[3]); // w
            case 1: return -(a[3] + a[0]) /
                    ((b[0] - a[0]) + (b[3] - a[3]));  // left
            case 2: return (a[3] - a[0]) /
                    ((b[0] - a[0]) - (b[3] - a[3])); // right
            case 3: return -(a[3] + a[1]) /
                    ((b[1] - a[1]) + (b[3] - a[3])); // bottom
            case 4: return (a[3] - a[1]) /
                    ((b[1] - a[1]) - (b[3] - a[3])); // top
            case 5: return -(a[2] + a[3]) /
                    ((b[2] - a[2]) + (b[3] - a[3]));  // near
            case 6: return (a[3] - a[2]) /
                    ((b[2] - a[2]) - (b[3] - a[3])); // far
        }
        return 0;
    }

    private float[] interpolateOutV = new float[4];

    private float[] interpolateVertex(float[] a, float[] b, float t) {
        for (int i = 0; i < 4; i++) {
            interpolateOutV[i] = a [i] * (1f - t) + b[i] * t;
        }

        return interpolateOutV;
    }

    private float[] interpolateOutUV = new float[4];

    private float[] interpolateUV(float[] a, float[] b, float t) {
        for (int i = 0; i < 2; i++) {
            interpolateOutUV[i] = a [i] * (1f - t) + b[i] * t;
        }

        return interpolateOutUV;
    }

    private float[] prevVertex = new float[4];
    private float[] currVertex = new float[4];

    private float[] prevUV = new float[2];
    private float[] currUV = new float[2];


    private int clippingAlongPlane (
            float[] inVertex, float[] outVertex,
            float[] inUV, float[] outUV,
            int plane, int inCounter)
    {
        int outCounter = 0;
        System.arraycopy(inVertex, (inCounter - 1) * 4, prevVertex, 0, 4);
        System.arraycopy(inUV, (inCounter - 1) * 2, prevUV, 0, 2);
        boolean prevInside = isInside(prevVertex, plane);

        for (int i = 0; i < inCounter; i++) {
            System.arraycopy(inVertex, i * 4, currVertex, 0, 4);
            System.arraycopy(inUV, i * 2, currUV, 0, 2);
            boolean currInside = isInside(currVertex, plane);

            if (currInside) {
                if (!prevInside) {
                    float crossingCoefficient = intersectionPoint(prevVertex, currVertex, plane);

                    float[] interV = interpolateVertex(prevVertex, currVertex, crossingCoefficient);
                    System.arraycopy(interV, 0, outVertex, outCounter * 4, 4);

                    float[] interUV = interpolateUV(prevUV, currUV, crossingCoefficient);
                    System.arraycopy(interUV, 0, outUV, outCounter * 2, 2);

                    outCounter++;
                }

                System.arraycopy(currVertex, 0, outVertex, outCounter * 4, 4);
                System.arraycopy(currUV, 0, outUV, outCounter * 2, 2);

                outCounter++;
            } else if (prevInside) {
                float crossingCoefficient = intersectionPoint(prevVertex, currVertex, plane);

                float[] interV = interpolateVertex(prevVertex, currVertex, crossingCoefficient);
                System.arraycopy(interV, 0, outVertex, outCounter * 4, 4);

                float[] interUV = interpolateUV(prevUV, currUV, crossingCoefficient);
                System.arraycopy(interUV, 0, outUV, outCounter * 2, 2);

                outCounter++;
            }

            float[] temp;

            temp = prevVertex;
            prevVertex = currVertex;
            currVertex = temp;

            temp = prevUV;
            prevUV = currUV;
            currUV = temp;

            prevInside = currInside;
        }

        return outCounter;
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

            int counter = 3;

            for (int i = 0; i < 3; i++) {
                System.arraycopy(bufferPolygonsVertices, (id * 3 + i) * 4, tempVertex1, i * 4, 4);
                System.arraycopy(bufferPolygonsUV, (id * 3 + i) * 2, tempUV1, i * 2, 2);
            }

            for (int i = 0; i < 7; i++) {
                counter = clippingAlongPlane(tempVertex1, tempVertex2,
                        tempUV1, tempUV2, i, counter);

                if (counter < 3) {
                    return;
                }

                float[] tempV = tempVertex1;
                tempVertex1 = tempVertex2;
                tempVertex2 = tempV;

                float[] tempUV = tempUV1;
                tempUV1 = tempUV2;
                tempUV2 = tempUV;
            }

            int clippingPolygonID = clippingCounter.getAndAdd(counter - 2);
            ObjectMaterial material = new ObjectMaterial(0, 0, polygonMaterial[id]);

            for (int i = 0; i < counter - 2; i++) {
//                System.arraycopy(tempVertex1, 0, clippingPolygon, (clippingPolygonID + i) * 3 * 4, 4);
//                System.arraycopy(tempUV1, 0, clippingPolygonsUV, (clippingPolygonID + i) * 3 * 2, 2);
                clippingPolygon[(clippingPolygonID + i) * 3] = new Vec4D(
                        tempVertex1[0], tempVertex1[1], tempVertex1[2], tempVertex1[3]
                );
                clippingPolygonsUV[(clippingPolygonID + i) * 3] = new Vec2D(
                        tempUV1[0], tempUV1[1]
                );

                for (int j = 1; j < 3; j++) {
//                    System.arraycopy(tempVertex1, (i + j) * 4, clippingPolygon, ((clippingPolygonID + i) * 3 + j) * 4, 4);
//                    System.arraycopy(tempUV1, (i + j) * 2, clippingPolygonsUV, ((clippingPolygonID + i) * 3 + j) * 2, 2);
                    clippingPolygon[(clippingPolygonID + i) * 3 + j] = new Vec4D(
                            tempVertex1[(i + j) * 4 + 0],
                            tempVertex1[(i + j) * 4 + 1],
                            tempVertex1[(i + j) * 4 + 2],
                            tempVertex1[(i + j) * 4 + 3]
                    );
                    clippingPolygonsUV[(clippingPolygonID + i) * 3 + j] = new Vec2D(
                            tempUV1[(i + j) * 2 + 0],
                            tempUV1[(i + j) * 2 + 1]
                    );
                }

                clippingPolygonMaterial[clippingPolygonID + i] = material;
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

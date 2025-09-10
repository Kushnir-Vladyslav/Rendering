package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Pipeline.PixelLinkedList;
import com.my_program.rendering.Texture;
import com.my_program.rendering.Vec2D;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class RasterizationThreads extends KernelCPU {
    private float[] vertexBuffer;
    private float[] bufferUV;
    private int[] materials;
    private Texture[] texture;
    public int[] fragmentsColor;
    public float[] fragmentsDepth;
    public int[] fragmentsNext;
    private AtomicIntegerArray heads;
    private AtomicInteger pixelCounter;
    private int width;
    private int height;
    private final int numberOfTasks;


    private float[] vertex1 = new float[4];
    private float[] vertex2 = new float[4];
    private float[] vertex3 = new float[4];

    private float[] UV1 = new float[2];
    private float[] UV2 = new float[2];
    private float[] UV3 = new float[2];

    private float[] edge1 = new float[2];
    private float[] edge2 = new float[2];
    private float[] edge3 = new float[2];

    private float[] pointVector = new float[2];

    private float[] pixelPoint = new float[2];
    
    private float[] pixelVector1 = new float[2];
    private float[] pixelVector2 = new float[2];
    private float[] pixelVector3 = new float[2];
    
    public RasterizationThreads(
            int id,
            float[] vertexBuffer,
            float[] bufferUV,
            int[] materials,
            Texture[] texture,
            int[] fragmentsColor,
            float[] fragmentsDepth,
            int[] fragmentsNext,
            AtomicIntegerArray heads,
            AtomicInteger pixelCounter,
            int width,
            int height,
            AtomicInteger clippingCounter
    )
    {
        this.idWorkGroup = id;
        this.vertexBuffer = vertexBuffer;
        this.bufferUV = bufferUV;
        this.materials = materials;
        this.texture = texture;
        this.fragmentsColor = fragmentsColor;
        this.fragmentsDepth = fragmentsDepth;
        this.fragmentsNext = fragmentsNext;
        this.heads = heads;
        this.pixelCounter = pixelCounter;
        this.width = width;
        this.height = height;
        this.numberOfTasks = clippingCounter.get();
    }

    private float vectorProduct (float[] TriangleEdge, float[] ToPointVector) {
        return (TriangleEdge[0] * ToPointVector[1] - TriangleEdge[1] * ToPointVector[0]);
    }

    private void pushPixel(int pixelID, int color, float depth) {
        int currPixel = pixelCounter.getAndIncrement();
        int prevPixel = heads.getAndSet(pixelID, currPixel);

        fragmentsColor[currPixel] = color;
        fragmentsDepth[currPixel] = depth;
        fragmentsNext[currPixel] = prevPixel;
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

            System.arraycopy(vertexBuffer, (id * 3 + 0) * 4, vertex1, 0, 4);
            System.arraycopy(vertexBuffer, (id * 3 + 1) * 4, vertex2, 0, 4);
            System.arraycopy(vertexBuffer, (id * 3 + 2) * 4, vertex3, 0, 4);

            System.arraycopy(bufferUV, (id * 3 + 0) * 2, UV1, 0, 2);
            System.arraycopy(bufferUV, (id * 3 + 1) * 2, UV2, 0, 2);
            System.arraycopy(bufferUV, (id * 3 + 2) * 2, UV3, 0, 2);

            int minX = (int) Math.floor(vertex1[0]);
            if (minX > vertex2[0]) minX = (int) Math.floor(vertex2[0]);
            if (minX > vertex3[0]) minX = (int) Math.floor(vertex3[0]);
            if (minX < 0) minX = 0;

            int maxX = (int) Math.ceil(vertex1[0]);
            if (maxX < vertex2[0]) maxX = (int) Math.ceil(vertex2[0]);
            if (maxX < vertex3[0]) maxX = (int) Math.ceil(vertex3[0]);
            if (maxX >= width) maxX = width - 1;

            int minY = (int) Math.floor(vertex1[1]);
            if (minY > vertex2[1]) minY = (int) Math.floor(vertex2[1]);
            if (minY > vertex3[1]) minY = (int) Math.floor(vertex3[1]);
            if (minY < 0) minY = 0;

            int maxY = (int) Math.ceil(vertex1[1]);
            if (maxY < vertex2[1]) maxY = (int) Math.ceil(vertex2[1]);
            if (maxY < vertex3[1]) maxY = (int) Math.ceil(vertex3[1]);
            if (maxY >= height) maxY = height - 1;

            edge1[0] = vertex2[0] - vertex1[0];
            edge1[1] = vertex2[1] - vertex1[1];

            edge2[0] = vertex3[0] - vertex2[0];
            edge2[1] = vertex3[1] - vertex2[1];

            edge3[0] = vertex1[0] - vertex3[0];
            edge3[1] = vertex1[1] - vertex3[1];

            boolean isTopLeft1 = (edge1[1] > 0.f) || (edge1[0] > 0.f && edge1[1] == 0.f);
            boolean isTopLeft2 = (edge2[1] > 0.f) || (edge2[0] > 0.f && edge2[1] == 0.f);
            boolean isTopLeft3 = (edge3[1] > 0.f) || (edge3[0] > 0.f && edge3[1] == 0.f);

            pointVector[0] = vertex3[0] - vertex1[0];
            pointVector[1] = vertex3[1] - vertex1[1];
            
            float converseBaryCentricDiv = 1.f / vectorProduct(edge1, pointVector);

            float converseW1 = 1.f / vertex1[3];
            float converseW2 = 1.f / vertex2[3];
            float converseW3 = 1.f / vertex3[3];

            pixelPoint[0] = minX + 0.5f;
            pixelPoint[1] = minY + 0.5f;

            pixelVector1[0] = pixelPoint[0] - vertex1[0];
            pixelVector1[1] = pixelPoint[1] - vertex1[1];

            pixelVector2[0] = pixelPoint[0] - vertex2[0];
            pixelVector2[1] = pixelPoint[1] - vertex2[1];

            pixelVector3[0] = pixelPoint[0] - vertex3[0];
            pixelVector3[1] = pixelPoint[1] - vertex3[1];

            float baseLengthVectorProduct1 = vectorProduct(pixelVector1, edge1);
            float baseLengthVectorProduct2 = vectorProduct(pixelVector2, edge2);
            float baseLengthVectorProduct3 = vectorProduct(pixelVector3, edge3);

            for (int y = minY; y <= maxY; y++) {
                float offsetYLengthVectorProduct1 = baseLengthVectorProduct1 - edge1[0] * (y - minY);
                float offsetYLengthVectorProduct2 = baseLengthVectorProduct2 - edge2[0] * (y - minY);
                float offsetYLengthVectorProduct3 = baseLengthVectorProduct3 - edge3[0] * (y - minY);

                for (int x = minX; x <= maxX; x++) {
                    float lengthVectorProduct1 = offsetYLengthVectorProduct1 + edge1[1] * (x - minX);
                    float lengthVectorProduct2 = offsetYLengthVectorProduct2 + edge2[1] * (x - minX);
                    float lengthVectorProduct3 = offsetYLengthVectorProduct3 + edge3[1] * (x - minX);

                    if ((lengthVectorProduct1 <= 0 || (isTopLeft1 && lengthVectorProduct1 == 0.f)) &&
                            (lengthVectorProduct2 <= 0 || (isTopLeft2 && lengthVectorProduct2 == 0.f)) &&
                            (lengthVectorProduct3 <= 0 || (isTopLeft3 && lengthVectorProduct3 == 0.f))) {
                        int pixelID = y * width + x;

                        float T1 = -lengthVectorProduct2 * converseBaryCentricDiv;
                        float T2 = -lengthVectorProduct3 * converseBaryCentricDiv;
                        float T3 = -lengthVectorProduct1 * converseBaryCentricDiv;

                        float depth = T1 * vertex1[2] + T2 * vertex2[2] + T3 * vertex3[2];

                        float oneOverW = T1 * converseW1 + T2 * converseW2 + T3 * converseW3;

                        Vec2D pixelUV1 = new Vec2D(UV1[0] * converseW1 * T1, UV1[1] * converseW1 * T1);
                        Vec2D pixelUV2 = new Vec2D(UV2[0] * converseW2 * T2, UV2[1] * converseW2 * T2);
                        Vec2D pixelUV3 = new Vec2D(UV3[0] * converseW3 * T3, UV3[1] * converseW3 * T3);

                        int color = texture[materials[id]].getColor(
                                new Vec2D(
                                        (pixelUV1.x() + pixelUV2.x() + pixelUV3.x()) / oneOverW,
                                        (pixelUV1.y() + pixelUV2.y() + pixelUV3.y()) / oneOverW
                                )
                        );

                        pushPixel(pixelID, color, depth);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

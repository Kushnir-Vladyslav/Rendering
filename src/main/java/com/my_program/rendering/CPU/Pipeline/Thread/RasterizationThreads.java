package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.CPU.Pipeline.PixelLinkedList;
import com.my_program.rendering.Texture;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class RasterizationThreads extends KernelCPU {
    private Vec4D[] vertexBuffer;
    private Vec2D[] bufferUV;
    private ObjectMaterial[] materials;
    private Texture[] texture;
    private PixelLinkedList[] pixelLinkedList;
    private AtomicIntegerArray heads;
    private AtomicInteger pixelCounter;
    private int width;
    private int height;
    private final int numberOfTasks;

    public RasterizationThreads(
            int id,
            Vec4D[] vertexBuffer,
            Vec2D[] bufferUV,
            ObjectMaterial[] materials,
            Texture[] texture,
            PixelLinkedList[] pixelLinkedList,
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
        this.pixelLinkedList = pixelLinkedList;
        this.heads = heads;
        this.pixelCounter = pixelCounter;
        this.width = width;
        this.height = height;
        this.numberOfTasks = clippingCounter.get();
    }

    private float vectorProduct (Vec2D TriangleEdge, Vec2D ToPointVector) {
        return (TriangleEdge.x() * ToPointVector.y() - TriangleEdge.y() * ToPointVector.x());
    }

    private void pushPixel(int pixelID, int color, float depth) {
        int currPixel = pixelCounter.getAndIncrement();
        int prevPixel = heads.getAndSet(pixelID, currPixel);

        pixelLinkedList[currPixel] = new PixelLinkedList(color, depth, prevPixel);
    }

    @Override
    protected void thread() {
        try {
            int id = get_global_id();
            if (id >= numberOfTasks) {
                return;
            }

            Vec4D vertex1 = vertexBuffer[id * 3 + 0];
            Vec4D vertex2 = vertexBuffer[id * 3 + 1];
            Vec4D vertex3 = vertexBuffer[id * 3 + 2];

            Vec2D UV1 = bufferUV[id * 3 + 0];
            Vec2D UV2 = bufferUV[id * 3 + 1];
            Vec2D UV3 = bufferUV[id * 3 + 2];

            int minX = (int) Math.floor(vertex1.x());
            if (minX > vertex2.x()) minX = (int) Math.floor(vertex2.x());
            if (minX > vertex3.x()) minX = (int) Math.floor(vertex3.x());
            if (minX < 0) minX = 0;

            int maxX = (int) Math.ceil(vertex1.x());
            if (maxX < vertex2.x()) maxX = (int) Math.ceil(vertex2.x());
            if (maxX < vertex3.x()) maxX = (int) Math.ceil(vertex3.x());
            if (maxX >= width) maxX = width - 1;

            int minY = (int) Math.floor(vertex1.y());
            if (minY > vertex2.y()) minY = (int) Math.floor(vertex2.y());
            if (minY > vertex3.y()) minY = (int) Math.floor(vertex3.y());
            if (minY < 0) minY = 0;

            int maxY = (int) Math.ceil(vertex1.y());
            if (maxY < vertex2.y()) maxY = (int) Math.ceil(vertex2.y());
            if (maxY < vertex3.y()) maxY = (int) Math.ceil(vertex3.y());
            if (maxY >= height) maxY = height - 1;

            Vec2D edge1 = new Vec2D(vertex2.x() - vertex1.x(), vertex2.y() - vertex1.y());
            Vec2D edge2 = new Vec2D(vertex3.x() - vertex2.x(), vertex3.y() - vertex2.y());
            Vec2D edge3 = new Vec2D(vertex1.x() - vertex3.x(), vertex1.y() - vertex3.y());

            boolean isTopLeft1 = (edge1.y() > 0.f) || (edge1.x() > 0.f && edge1.y() == 0.f);
            boolean isTopLeft2 = (edge2.y() > 0.f) || (edge2.x() > 0.f && edge2.y() == 0.f);
            boolean isTopLeft3 = (edge3.y() > 0.f) || (edge3.x() > 0.f && edge3.y() == 0.f);

            Vec2D pointVector = new Vec2D(vertex3.x() - vertex1.x(), vertex3.y() - vertex1.y());
            float converseBaryCentricDiv = 1.f / vectorProduct(edge1, pointVector);

            float converseW1 = 1.f / vertex1.w();
            float converseW2 = 1.f / vertex2.w();
            float converseW3 = 1.f / vertex3.w();

            Vec2D pixelPoint = new Vec2D(minX, minY).add(0.5f, 0.5f);

            Vec2D pixelVector1 = new Vec2D(pixelPoint.x() - vertex1.x(), pixelPoint.y() - vertex1.y());
            Vec2D pixelVector2 = new Vec2D(pixelPoint.x() - vertex2.x(), pixelPoint.y() - vertex2.y());
            Vec2D pixelVector3 = new Vec2D(pixelPoint.x() - vertex3.x(), pixelPoint.y() - vertex3.y());

            float baseLengthVectorProduct1 = vectorProduct(pixelVector1, edge1);
            float baseLengthVectorProduct2 = vectorProduct(pixelVector2, edge2);
            float baseLengthVectorProduct3 = vectorProduct(pixelVector3, edge3);

            for (int y = minY; y <= maxY; y++) {
                float offsetYLengthVectorProduct1 = baseLengthVectorProduct1 - edge1.x() * (y - minY);
                float offsetYLengthVectorProduct2 = baseLengthVectorProduct2 - edge2.x() * (y - minY);
                float offsetYLengthVectorProduct3 = baseLengthVectorProduct3 - edge3.x() * (y - minY);

                for (int x = minX; x <= maxX; x++) {
                    float lengthVectorProduct1 = offsetYLengthVectorProduct1 + edge1.y() * (x - minX);
                    float lengthVectorProduct2 = offsetYLengthVectorProduct2 + edge2.y() * (x - minX);
                    float lengthVectorProduct3 = offsetYLengthVectorProduct3 + edge3.y() * (x - minX);

                    if ((lengthVectorProduct1 <= 0 || (isTopLeft1 && lengthVectorProduct1 == 0.f)) &&
                            (lengthVectorProduct2 <= 0 || (isTopLeft2 && lengthVectorProduct2 == 0.f)) &&
                            (lengthVectorProduct3 <= 0 || (isTopLeft3 && lengthVectorProduct3 == 0.f))) {
                        int pixelID = y * width + x;

                        float T1 = -lengthVectorProduct2 * converseBaryCentricDiv;
                        float T2 = -lengthVectorProduct3 * converseBaryCentricDiv;
                        float T3 = -lengthVectorProduct1 * converseBaryCentricDiv;

                        float depth = T1 * vertex1.z() + T2 * vertex2.z() + T3 * vertex3.z();

                        float oneOverW = T1 * converseW1 + T2 * converseW2 + T3 * converseW3;

                        Vec2D pixelUV1 = new Vec2D(UV1.x() * converseW1 * T1, UV1.y() * converseW1 * T1);
                        Vec2D pixelUV2 = new Vec2D(UV2.x() * converseW2 * T2, UV2.y() * converseW2 * T2);
                        Vec2D pixelUV3 = new Vec2D(UV3.x() * converseW3 * T3, UV3.y() * converseW3 * T3);

                        int color = texture[materials[id].textureOrder].getColor(
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

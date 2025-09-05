package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.*;
import com.my_program.rendering.CPU.Buffers.Buffers;
import com.my_program.rendering.CPU.Buffers.ObjectMaterial;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class BuffersPreparation {
    public static final int CLIPPING_MULTIPLIER = 9;
    public static final int FRAGMENTS_PER_PIXEL = 64;

    private static Vector<DrawableObject> objects = new Vector<>();

    public static void addObject(DrawableObject object) {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }

        objects.add(object);
    }

    public static void cleanupObjects() {
        objects.clear();
    }

    public static void preparation() {
        if (objects.isEmpty()) return;
        int[] numOfVertex = new int[objects.size() + 1];

        numOfVertex[0] = 0;

        for (int i = 0; i < objects.size(); i++) {
            numOfVertex[i + 1] = numOfVertex[i] + objects.get(i).getNumVertices();
        }

        Buffers.bufferVertices = new Vec4D[numOfVertex[objects.size()]];
        Buffers.bufferUV = new Vec2D[numOfVertex[objects.size()]];
        Buffers.vertexMaterial = new ObjectMaterial[numOfVertex[objects.size()]];

        Buffers.textures = new Texture[objects.size()];

        for (int i = 0; i < objects.size(); i++) {
            DrawableObject object = objects.get(i);
            Buffers.textures[i] = object.getTexture();

            for(int j = 0; j < object.getNumVertices(); j++) {
                Buffers.bufferVertices[numOfVertex[i] + j] = object.getVerticesBuffer()[j];
                Buffers.bufferUV[numOfVertex[i] + j] = object.getUVBuffer()[j];
                Buffers.vertexMaterial[numOfVertex[i] + j] = new ObjectMaterial(
                        Buffers.textures[i].getTextureWidth(),
                        Buffers.textures[i].getTextureHeight(),
                        i
                );
            }
        }

        Buffers.bufferObjectsRef = new int[objects.size() + 1];

        for (int i = 0; i < objects.size(); i++) {
            Buffers.bufferObjectsRef[i + 1] = Buffers.bufferObjectsRef[i] + objects.get(i).getNumIndex();
        }

        Buffers.bufferIndexes = new int[Buffers.bufferObjectsRef[objects.size()]];

        Buffers.indexNum = 0;

        for (int i = 0; i < objects.size(); i++) {
            int[] indexes = objects.get(i).getIndexBuffer();
            Buffers.indexNum += indexes.length;
            for (int j = 0; j < indexes.length; j++) {
                Buffers.bufferIndexes[Buffers.bufferObjectsRef[i] + j] = indexes[j] + numOfVertex[i];
            }
        }

        //transformed buffers
        Buffers.bufferTransformedVertices = new Vec4D[numOfVertex[objects.size()]];

        //primer polygons
        Buffers.bufferPolygonsVertices = new Vec4D[Buffers.bufferObjectsRef[objects.size()]];
        Buffers.bufferPolygonsUV = new Vec2D[Buffers.bufferObjectsRef[objects.size()]];
        Buffers.polygonMaterial = new ObjectMaterial[Buffers.bufferObjectsRef[objects.size()] / 3];

        //clipped polygons
        Buffers.clippingPolygon = new Vec4D[Buffers.bufferObjectsRef[objects.size()] * CLIPPING_MULTIPLIER];
        Buffers.clippingPolygonsUV = new Vec2D[Buffers.bufferObjectsRef[objects.size()] * CLIPPING_MULTIPLIER];
        Buffers.clippingPolygonMaterial = new ObjectMaterial[Buffers.bufferObjectsRef[objects.size()] * CLIPPING_MULTIPLIER / 3];

        Buffers.clippingCounter = new AtomicInteger(0);

        //Viewport Transform
        Buffers.width = GS.getScreenWidth();
        Buffers.height = GS.getScreenHeight();

        //Rasterization
        Buffers.heads = new AtomicIntegerArray(Buffers.width * Buffers.height);
        Buffers.fragments = new PixelLinkedList[Buffers.width * Buffers.height * FRAGMENTS_PER_PIXEL];

        Buffers.pixelCounter = new AtomicInteger(0);

        //processing
        Buffers.pixels = new int[Buffers.width * Buffers.height];

        //host data
        Buffers.vertexNum = numOfVertex[objects.size()];
    }

    public static void clearHeadBuffer() {
        for (int i = 0; i < Buffers.heads.length(); i++) {
            Buffers.heads.set(i, -1);
        }
    }

    public static void resize() {
        //Viewport Transform
        Buffers.width = GS.getScreenWidth();
        Buffers.height = GS.getScreenHeight();

        //Rasterization
        Buffers.heads = new AtomicIntegerArray(Buffers.width * Buffers.height);
        Buffers.fragments = new PixelLinkedList[Buffers.width * Buffers.height * FRAGMENTS_PER_PIXEL];

        //processing
        Buffers.pixels = new int[Buffers.width * Buffers.height];
    }

    public static void clearPixels() {
        for (int i = 0; i < Buffers.pixels.length; i++) {
            Buffers.pixels[i] = 0xFF000000;
        }
    }

    public static void clearCounters() {
        Buffers.clippingCounter.set(0);
        Buffers.pixelCounter.set(0);
    }
}

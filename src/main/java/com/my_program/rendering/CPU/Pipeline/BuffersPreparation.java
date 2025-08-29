package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.*;
import com.my_program.rendering.CPU.Buffers.Buffers;
import com.my_program.rendering.CPU.Buffers.ObjectMaterial;

import java.util.Vector;

public class BuffersPreparation {
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

            for(int j = 0; j < object.getNumIndex(); j++) {
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

        for (int i = 0; i < objects.size(); i++) {
            int[] indexes = objects.get(i).getIndexBuffer();
            for (int j = 0; j < indexes.length; j++) {
                Buffers.bufferIndexes[Buffers.bufferObjectsRef[i] + j] = indexes[j];
            }
        }

        //transformed buffers
        Buffers.bufferTransformedVertices = new Vec4D[numOfVertex[objects.size()]];

        //primer polygons
        Buffers.bufferPolygonsVertices = new Vec4D[Buffers.bufferObjectsRef[objects.size()]];
        Buffers.bufferPolygonsUV = new Vec2D[Buffers.bufferObjectsRef[objects.size()]];
        Buffers.polygonMaterial = new ObjectMaterial[Buffers.bufferObjectsRef[objects.size()] / 3];


    }


}

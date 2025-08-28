package com.my_program.rendering.CPU.Pipeline;

import com.my_program.rendering.CPU.Buffers.Buffers;
import com.my_program.rendering.CPU.Buffers.ObjectMaterial;
import com.my_program.rendering.DrawableObject;
import com.my_program.rendering.GS;
import com.my_program.rendering.Vec2D;
import com.my_program.rendering.Vec4D;

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

        Buffers buffers = GS.buffers;

        buffers.bufferVertices = new Vec4D[objects.size()];
        buffers.bufferUV = new Vec2D[objects.size()];
        buffers.objects = new ObjectMaterial[objects.size()];
    }


}

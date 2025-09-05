package com.my_program.rendering.CPU.Pipeline.Thread;

import com.my_program.rendering.CPU.Pipeline.PixelLinkedList;
import com.my_program.rendering.Vec4D;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class FragmentProcessing extends RecursiveAction {
    private int id;
    private PixelLinkedList[] pixelLinkedList;
    private AtomicIntegerArray heads;
    private int[] pixels;

    public FragmentProcessing(
            int id,
            PixelLinkedList[] pixelLinkedList,
            AtomicIntegerArray heads,
            int[] pixels
    ) {
        this.id = id;
        this.pixelLinkedList = pixelLinkedList;
        this.heads = heads;
        this.pixels = pixels;
    }

    private Vec4D colorIntToARGB(int color) {
        return new Vec4D(
                ((color & 0X00FF0000) >>> 16) / 255.f,
                ((color & 0x0000FF00) >>> 8) / 255.f,
                (color & 0x000000FF) / 255.f,
                ((color & 0XFF000000) >>> 24) / 255.f
        );
    }

    private int colorARGBToInt(Vec4D color) {
        int ai = Math.max(0, Math.min(255, (int)(color.a() * 255f + 0.5f)));
        int ri = Math.max(0, Math.min(255, (int)(color.r() * 255f + 0.5f)));
        int gi = Math.max(0, Math.min(255, (int)(color.g() * 255f + 0.5f)));
        int bi = Math.max(0, Math.min(255, (int)(color.b() * 255f + 0.5f)));
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }

    @Override
    protected void compute() {
        try {
            int fragmentCounter = 0;
            int fragmentPointer = heads.get(id);

            int[] colors = new int[128];
            float[] depth = new float[128];

            if (fragmentPointer == -1) {
                return;
            }

            while (fragmentPointer != -1) {
                PixelLinkedList fragment = pixelLinkedList[fragmentPointer];
                colors[fragmentCounter] = fragment.color;
                depth[fragmentCounter] = fragment.depth;

                fragmentCounter++;

                fragmentPointer = fragment.nextIndex;
            }

            for (int i = 0; i < fragmentCounter - 1; i++) {
                int pointer = i;
                for (int j = i; j < fragmentCounter; j++) {
                    if (depth[j] > depth[pointer]) {
                        pointer = j;
                    }
                }
                if (pointer != i) {
                    int tempColor = colors[i];
                    colors[i] = colors[pointer];
                    colors[pointer] = tempColor;

                    float tempDepth = depth[i];
                    depth[i] = depth[pointer];
                    depth[pointer] = tempDepth;
                }
            }

            Vec4D color = new Vec4D(0, 0, 0, 0);

            for (int i = 0; i < fragmentCounter; i++) {
                Vec4D polygonColor = colorIntToARGB(colors[i]);

                color.mult(1 - polygonColor.a());
                polygonColor.mult3D(polygonColor.a());

                color.add(polygonColor);
            }

            pixels[id] = colorARGBToInt(color);
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}

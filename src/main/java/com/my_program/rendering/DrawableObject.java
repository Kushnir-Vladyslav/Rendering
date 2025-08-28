package com.my_program.rendering;

public class DrawableObject {
    // Масив вершин трикутників
    private Vec4D[] verticesBuffer;

    //масив вершин в координатах текстури
    private Vec2D[] objectUv;

    // масив індексів вершин для відмальовування сторін
    private int[] indexBuffer;

    // Теукстура обєкту
    Texture texture;

    public int getNumVertices() {
        return verticesBuffer.length;
    }

    public int getNumIndex() {
        return indexBuffer.length;
    }

    public Vec4D[] getVerticesBuffer() {
        return verticesBuffer;
    }

    public Vec2D[] getUVBuffer() {
        return objectUv;
    }

    public int[] getIndexBuffer() {
        return indexBuffer;
    }

    DrawableObject(Vec4D[] VerticesBuffer, Vec2D[] ObjectUv, int[] IndexBuffer, Texture texture) {
        this.verticesBuffer = VerticesBuffer;
        this.objectUv = ObjectUv;
        this.indexBuffer = IndexBuffer;
        this.texture = texture;
    }


    public void draw (Matrix4D tr) {
        for (int i = 0; i < indexBuffer.length; i+= 3) {
            new DrawTriangle3D(
                    new Vec4D[] {verticesBuffer[indexBuffer[i]],
                            verticesBuffer[indexBuffer[i + 1]],
                            verticesBuffer[indexBuffer[i + 2]]},
                    tr.clone(),
                    new Vec2D[] {objectUv[indexBuffer[i]],
                            objectUv[indexBuffer[i + 1]],
                            objectUv[indexBuffer[i + 2]]},
                    texture
            ).draw();
        }
    }




}

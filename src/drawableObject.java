public class drawableObject {
    // Масив вершин трикутників
    private vec4D[] VerticesBuffer;

    //масив вершин в координатах текстури
    private vec2D[] ObjectUv;

    // масив індексів вершин для відмальовування сторін
    private int[] IndexBuffer;

    // Теукстура обєкту
    texture Texture;

    drawableObject(vec4D[] VerticesBuffer, vec2D[] ObjectUv, int[] IndexBuffer, texture Texture) {
        this.VerticesBuffer = VerticesBuffer;
        this.ObjectUv = ObjectUv;
        this.IndexBuffer = IndexBuffer;
        this.Texture = Texture;
    }

    public void draw (matrix4D tr) {
        for (int i = 0; i < IndexBuffer.length; i+= 3) {
            new DrawTriangle3D(
                    new vec4D[] {VerticesBuffer[IndexBuffer[i]],
                            VerticesBuffer[IndexBuffer[i + 1]],
                            VerticesBuffer[IndexBuffer[i + 2]]},
                    tr.clone(),
                    new vec2D[] {ObjectUv[IndexBuffer[i]],
                            ObjectUv[IndexBuffer[i + 1]],
                            ObjectUv[IndexBuffer[i + 2]]},
                    Texture
            ).draw();
        }
    }

}

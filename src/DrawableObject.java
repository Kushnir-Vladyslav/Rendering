public class DrawableObject {
    // Масив вершин трикутників
    private Vec4[] VerticesBuffer;

    //масив кольорів вершин трикутників
    private Vec4[] ColorsBuffer;

    // масив індексів вершин для відмальовування сторін
    private int[] IndexBuffer;

    DrawableObject(Vec4[] VerticesBuffer, Vec4[] ColorsBuffer, int[] IndexBuffer) {
        this.VerticesBuffer = VerticesBuffer;
        this.ColorsBuffer = ColorsBuffer;
        this.IndexBuffer = IndexBuffer;
    }

    public void draw (Matrix4 tr) {
        for (int i = 0; i < IndexBuffer.length; i+= 3) {
            new Triangle3(
                    VerticesBuffer[IndexBuffer[i]],
                    VerticesBuffer[IndexBuffer[i + 1]],
                    VerticesBuffer[IndexBuffer[i + 2]],
                    tr.clone(),
                    new Vec4[] {ColorsBuffer[IndexBuffer[i]], ColorsBuffer[IndexBuffer[i + 1]], ColorsBuffer[IndexBuffer[i + 2]]}
            ).draw();
        }
    }

}

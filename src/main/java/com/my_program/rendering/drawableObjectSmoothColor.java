package com.my_program.rendering;

//Використовувася для відмальовування трикутників з заданими кольорами на вершинах
//після додванання тестур застарів
@Deprecated
public class drawableObjectSmoothColor {
    // Масив вершин трикутників
    private vec4D[] VerticesBuffer;

    //масив кольорів вершин трикутників
    private vec4D[] ColorsBuffer;

    // масив індексів вершин для відмальовування сторін
    private int[] IndexBuffer;

    drawableObjectSmoothColor(vec4D[] VerticesBuffer, vec4D[] ColorsBuffer, int[] IndexBuffer) {
        this.VerticesBuffer = VerticesBuffer;
        this.ColorsBuffer = ColorsBuffer;
        this.IndexBuffer = IndexBuffer;
    }

    public void draw (matrix4D tr) {
        for (int i = 0; i < IndexBuffer.length; i+= 3) {
            new triangle3DSmoothColor(
                    VerticesBuffer[IndexBuffer[i]],
                    VerticesBuffer[IndexBuffer[i + 1]],
                    VerticesBuffer[IndexBuffer[i + 2]],
                    tr.clone(),
                    new vec4D[] {ColorsBuffer[IndexBuffer[i]], ColorsBuffer[IndexBuffer[i + 1]], ColorsBuffer[IndexBuffer[i + 2]]}
            ).draw();
        }
    }

}

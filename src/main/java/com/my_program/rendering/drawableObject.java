package com.my_program.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class drawableObject {
    // Масив вершин трикутників
    private vec4D[] VerticesBuffer;

    //масив вершин в координатах текстури
    private vec2D[] ObjectUv;

    // масив індексів вершин для відмальовування сторін
    private int[] IndexBuffer;

    // Теукстура обєкту
    texture Texture;

    //Змінні для рисування OpenGL
    //зберігає стан привязаних буферів vbo та ebo
    private int vaoID;
    //зберігає вершини, зберігає і вершини і текстури координат
    private int vboID;
    //зберігає індекси
    private int eboID;
    //зберігає текстуру
    private int textureID;


    drawableObject(vec4D[] VerticesBuffer, vec2D[] ObjectUv, int[] IndexBuffer, texture Texture) {
        this.VerticesBuffer = VerticesBuffer;
        this.ObjectUv = ObjectUv;
        this.IndexBuffer = IndexBuffer;
        this.Texture = Texture;

        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {


            // Створюємо VAO
            vaoID = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoID);

            // Створюємо VBO для вершинних даних
            vboID = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);

            float[] VTBs = getVertexTextureBuffers();



            float[] FBO = {
                    -0.5f, -0.5f, 0.0f,  // left
                    0.5f, -0.5f, 0.0f,  // right
                    0.0f,  0.5f, 0.0f   // top
            };

//            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(VTBs.length);
//            vertexBuffer.put(VTBs).flip();
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, FBO, GL30.GL_STATIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // Unbind the VBO and VAO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

//            // Вказуємо OpenGL як інтерпретувати вершини
//            GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 5 * Float.BYTES, 0);
//            GL30.glEnableVertexAttribArray(0);
//
//            // Текстурні координати (2 компоненти)
//            GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
//            GL30.glEnableVertexAttribArray(1);
//
//            // Створюємо EBO для індексованого рендерингу
//            eboID = GL30.glGenBuffers();
//            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);
//
////            IntBuffer indexBuffer = BufferUtils.createIntBuffer(IndexBuffer.length);
////            indexBuffer.put(IndexBuffer).flip();
//            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer, GL30.GL_STATIC_DRAW);
//
//            // Створюємо текстурний об'єкт у OpenGL
//            textureID = GL30.glGenTextures();
//            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
//
//            // Налаштовуємо параметри текстури
//            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
//            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
//            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
//            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
//
//            ByteBuffer rgbaBuffer = Texture.ConvertToByteBuffer();
//
//            // Завантажуємо текстуру в OpenGL
//            GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA,
//                    Texture.getWidth(), Texture.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, rgbaBuffer);
////            GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D); // Генеруємо міп-карти (якщо потрібно)

        }
    }

    public float[] getF() {
        float[] f = new float[VerticesBuffer.length * 3];

        for (int i = 0;  i < VerticesBuffer.length; i++) {
            f[i * 3 + 0] = VerticesBuffer[i].x();
            f[i * 3 + 1] = VerticesBuffer[i].y();
            f[i * 3 + 2] = VerticesBuffer[i].z();
        }


        return f;
    }

    private float[] getVertexTextureBuffers () {
        float[] VTBs = new float[VerticesBuffer.length * 5];

        for (int i = 0; i < VerticesBuffer.length; i++) {
            VTBs[i * 5 + 0] = VerticesBuffer[i].x();
            VTBs[i * 5 + 1] = VerticesBuffer[i].y();
            VTBs[i * 5 + 2] = VerticesBuffer[i].z();
            VTBs[i * 5 + 3] = ObjectUv[i].x();
            VTBs[i * 5 + 4] = ObjectUv[i].y();
        }

        return VTBs;
    }


    public void draw (matrix4D tr) {
        if (GlobalState.RenderingMethod == GlobalState.renderingMethod.GPU) {
            drawOpenGL();
        } else {
            for (int i = 0; i < IndexBuffer.length; i += 3) {
                new DrawTriangle3D(
                        new vec4D[]{VerticesBuffer[IndexBuffer[i]],
                                VerticesBuffer[IndexBuffer[i + 1]],
                                VerticesBuffer[IndexBuffer[i + 2]]},
                        tr.clone(),
                        new vec2D[]{ObjectUv[IndexBuffer[i]],
                                ObjectUv[IndexBuffer[i + 1]],
                                ObjectUv[IndexBuffer[i + 2]]},
                        Texture
                ).draw();
            }
        }
    }

    public void drawOpenGL() {

        // Активуємо текстуру перед рендерингом

//        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);

        GL30.glUseProgram(GlobalState.openGL.shaderProgram);
        // Прив'язуємо VAO перед малюванням
        GL30.glBindVertexArray(vaoID);
        // Малюємо елементи (індексований рендеринг)

//        GL30.glDrawElements(GL30.GL_TRIANGLES, IndexBuffer.length, GL30.GL_UNSIGNED_INT, 0);

        glDrawArrays(GL_TRIANGLES, 0, 3);

        // Відв'язуємо VAO після малювання
        GL30.glBindVertexArray(0);
    }


}

package com.my_program.rendering;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class OpenGl_prob {
    private long window;
    private int fbo;
    private int texture;

    //шейдери для малювання
    public int shaderProgram;

    public OpenGl_prob() {

        init();
    }

    private void init() {
        // Ініціалізуємо GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Налаштовуємо GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Вікно буде невидимим
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Створюємо вікно
        window = glfwCreateWindow(GlobalState.getScreenWidth(), GlobalState.getScreenHeight(), "Hidden OpenGL Window", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Робимо OpenGL контекст поточним
        glfwMakeContextCurrent(window);

        // Дуже важливо! Створюємо можливості OpenGL
        GL.createCapabilities();

        //завантаження шейдерів
//        shaderProgram = createShaderProgram("Shaders/vertexShader.glsl", "Shaders/fragmentShader.glsl");

        // Create and compile shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                "}");
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main()\n" +
                "{\n" +
                "    FragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);\n" +
                "}");
        glCompileShader(fragmentShader);

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Delete the shaders as they're linked into our program now and no longer necessary
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        //=======================================

        // Створення фреймбуфера
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Створення текстури, текстура витупає в ролі поля для малювання
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, GlobalState.getScreenWidth(), GlobalState.getScreenHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Прикріплення текстури до фреймбуфера
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        windowUpDate();

        glEnable(GL_DEPTH_TEST);
    }

    public void windowUpDate() {
        glViewport(0, 0, GlobalState.getScreenWidth(), GlobalState.getScreenHeight());

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, GlobalState.getScreenWidth(), GlobalState.getScreenHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    // Метод для створення шейдерної програми
    public int createShaderProgram(String vertexPath, String fragmentPath) {
        // Завантажуємо вихідний код шейдерів
        String vertexCode = readFile(vertexPath);
        String fragmentCode = readFile(fragmentPath);

        //Створюємо та компілюємо вершинний і фрагментний шейдери
        int vertexShader = compileShader(GL30.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = compileShader(GL30.GL_FRAGMENT_SHADER, fragmentCode);

        // Створюємо програму шейдерів і прив'язуємо шейдери до неї
        int programID = GL30.glCreateProgram();
        GL30.glAttachShader(programID, vertexShader);
        GL30.glAttachShader(programID, fragmentShader);
        GL30.glLinkProgram(programID);

        // Перевіряємо, чи програма шейдерів лінкується без помилок
        if (GL30.glGetProgrami(programID, GL30.GL_LINK_STATUS) == GL30.GL_FALSE) {
            System.err.println("Не вдалося лінкувати шейдерну програму:");
            System.err.println(GL30.glGetProgramInfoLog(programID));
            return -1;
        }

//        glDetachShader(programID, vertexShader);
//        glDetachShader(programID, fragmentShader);

        // Видаляємо шейдери після того, як вони прив'язані до програми
        GL30.glDeleteShader(vertexShader);
        GL30.glDeleteShader(fragmentShader);

        return programID;
    }

    // Метод для компіляції шейдера
    private int compileShader(int type, String shaderCode) {
        int shaderID = GL30.glCreateShader(type);
        GL30.glShaderSource(shaderID, shaderCode);
        GL30.glCompileShader(shaderID);

        // Перевіряємо наявність помилок компіляції
        if (GL30.glGetShaderi(shaderID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
            System.err.println("Помилка компіляції шейдера:");
            System.err.println(GL30.glGetShaderInfoLog(shaderID));
            return -1;
        }

        return shaderID;
    }

    // Метод для читання коду шейдера з файлу
    private String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        URL resource = getClass().getResource(filePath);
        if (resource == null) {
            throw new IllegalArgumentException("Файл не знайдено: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    //очищення екрану та буферу глибини
    public void clear(float r, float g, float b) {
        glClearColor(r, g, b, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }

    //отримання  результатів рендиренгу, пренесення їх у буфер пікселів
    public void getResultOfRenderingOpenGL() {
        // Читаємо пікселі
        glReadPixels(0, 0, GlobalState.getScreenWidth(), GlobalState.getScreenHeight(), GL_RGBA, GL_UNSIGNED_BYTE, GlobalState.Pixels);
    }

    //видалення ресурсів, напевно потрібно це використовувати, але покищо система робить це за мене
    public void cleanup() {
        glDeleteFramebuffers(fbo);

        // Звільняємо ресурси вікна
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Завершуємо роботу GLFW
        glfwTerminate();
    }
}
